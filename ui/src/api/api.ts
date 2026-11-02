import axios, { AxiosError, type AxiosRequestConfig } from "axios";
import { AppConfig } from "../config/AppConfig";
import { handleErrorResponse } from "./ErrorHandler";
import configureRequestInterceptor from "./RequestInterceptor";
import configureResponseInterceptor from "./ResponseInterceptor";

const api = axios.create({
    baseURL: AppConfig.API_BASE_URL,
    timeout: 5000,
});

configureRequestInterceptor(api);
configureResponseInterceptor(api);

export default api;

export interface ApiResponse<T> {
    data: T;
    status: number;
}

export type ErrorResponse = {
    errorName: string;
    errorCode: string;
    errorMessage: string;
    validationErrors: ValidationErrors[];
    timestamp: string;
};

export type ValidationErrors = {
    field: string;
    rejectedValue: object;
    message: string;
};

export type ApiClientProps = {
    type: "get" | "post" | "put" | "patch" | "delete";
    uri: string;
    service: "identity" | "profile" | "bookings" | "rides";
    payload?: unknown;
    config?: AxiosRequestConfig<unknown, unknown>;
    rawResponse?: boolean;
};

export async function apiClient<T>({
    type,
    uri,
    service,
    payload,
    config,
    rawResponse = false,
}: ApiClientProps): Promise<ApiResponse<T>> {
    try {
        let response, url;

        if (service == "identity") {
            url = AppConfig.IDENTITY_AUTH_URL;
        } else if (service == "profile") {
            url = AppConfig.IDENTITY_PROFILE_URL;
        } else if (service == "bookings") {
            url = AppConfig.BOOKINGS_SERVICE_URL;
        } else if (service == "rides") {
            url = AppConfig.BOOKING_RIDES_URL;
        } else {
            // url = AppConfig.CMS_PUBLICATION_URL;
        }

        if (type.toLowerCase() == "post") {
            response = await api.post(url + uri, payload, config);
        } else if (type.toLowerCase() == "put") {
            response = await api.put(url + uri, payload, config);
        } else if (type.toLowerCase() == "patch") {
            response = await api.patch(url + uri, payload, config);
        } else if (type.toLowerCase() == "delete") {
            response = await api.delete(url + uri, config);
        } else {
            response = await api.get(url + uri, config);
        }

        if (rawResponse) {
            return {
                data: response.data as T,
                status: response.status,
            };
        }

        const apiResponse = response.data as {
            data: T;
            timestamp: string;
        };

        return {
            data: apiResponse.data,
            status: response.status,
        } as ApiResponse<T>;
    } catch (er) {
        const error = er as AxiosError;
        const contentType =
            (error.response?.headers["content-type"] as string) ??
            "application/json";

        if (contentType.includes("application/json") && error.response) {
            const data = error.response.data;

            // handling according to the responseType config
            if (data instanceof Blob) {
                error.response.data = JSON.parse(await data.text());
            } else if (typeof data === "string") {
                error.response.data = JSON.parse(data);
            } else {
                error.response.data = data;
            }
        }

        throw handleErrorResponse(error);
    }
}
