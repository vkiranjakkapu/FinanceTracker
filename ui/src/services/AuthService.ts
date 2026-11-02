import { apiClient, type ApiResponse } from "../api/api";

class AuthService {
    async getProfile<T>(): Promise<ApiResponse<T>> {
        return apiClient({
            type: "get",
            service: "profile",
            uri: "/me",
        });
    }

    async authenticate<T>(payload: {
        email: string;
        password: string;
    }): Promise<ApiResponse<T>> {
        return apiClient({
            type: "post",
            service: "identity",
            uri: "/",
            payload,
        });
    }

    async refresh<T>(payload: {
        refreshToken: string;
    }): Promise<ApiResponse<T>> {
        return apiClient({
            type: "post",
            service: "identity",
            uri: "/refresh",
            payload,
        });
    }

    async logout<T>(payload: {
        refreshToken: string;
    }): Promise<ApiResponse<T>> {
        return apiClient({
            type: "post",
            service: "identity",
            uri: "/logout",
            payload,
        });
    }
}

export default new AuthService();

export interface LoginResponse {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
}
