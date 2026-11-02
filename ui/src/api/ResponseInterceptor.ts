import type {
    AxiosError,
    AxiosInstance,
    AxiosResponse,
    InternalAxiosRequestConfig,
} from "axios";
import TokenStorage from "../storage/TokenStorage";
import { AppConfig } from "../config/AppConfig";

// Interceptor state variables
let isRefreshing = false;
let failedQueue: Array<{
    resolve: (token: string) => void;
    reject: (error: unknown) => void;
}> = [];

const processQueue = (error: unknown, token: string | null = null) => {
    failedQueue.forEach((promise) => {
        if (error) {
            promise.reject(error);
        } else if (token) {
            promise.resolve(token);
        }
    });
    failedQueue = [];
};

export default function configureResponseInterceptor(api: AxiosInstance) {
    api.interceptors.response.use(
        (response: AxiosResponse) => response,
        async (error: AxiosError) => {
            const originalRequest =
                error.config as InternalAxiosRequestConfig & {
                    _retry?: boolean;
                };

            if (!originalRequest) {
                return Promise.reject(error);
            }

            if (error.response?.status === 401 && !originalRequest._retry) {
                const isAuthEndpoint = AppConfig.PUBLIC_ENDPOINTS.some((path) =>
                    originalRequest.url?.includes(path),
                );

                if (isAuthEndpoint) {
                    return Promise.reject(error);
                }

                // If a token refresh is already in progress, queue failed requests
                if (isRefreshing) {
                    return new Promise((resolve, reject) => {
                        failedQueue.push({ resolve, reject });
                    })
                        .then((newToken) => {
                            originalRequest.headers.Authorization = `Bearer ${newToken}`;
                            return api(originalRequest);
                        })
                        .catch((err) => Promise.reject(err));
                }

                originalRequest._retry = true;
                isRefreshing = true;

                const refreshToken = TokenStorage.getRefreshToken();
                if (!refreshToken) {
                    TokenStorage.clear();
                    isRefreshing = false;
                    return Promise.reject(error);
                }

                try {
                    const refreshUrl = `${AppConfig.IDENTITY_AUTH_URL}/refresh`;
                    const response = await api.post(refreshUrl, {
                        refreshToken,
                    });

                    const { accessToken, newRefreshToken } = response.data.data;

                    TokenStorage.save(
                        accessToken,
                        newRefreshToken || refreshToken,
                    );

                    // Update headers for retry and future requests
                    api.defaults.headers.common.Authorization = `Bearer ${accessToken}`;
                    originalRequest.headers.Authorization = `Bearer ${accessToken}`;

                    processQueue(null, accessToken);
                    return api(originalRequest);
                } catch (refreshError) {
                    processQueue(refreshError, null);
                    TokenStorage.clear();
                    return Promise.reject(refreshError);
                } finally {
                    isRefreshing = false;
                }
            }

            return Promise.reject(error);
        },
    );
}
