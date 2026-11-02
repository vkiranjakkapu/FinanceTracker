import { apiClient, type ApiResponse } from "../api/api";
import type { RoleType } from "../context/useAuthContext";

class UserService {
    async getUsersByEmail<T>(email: string): Promise<ApiResponse<T>> {
        return apiClient({
            type: "get",
            service: "profile",
            uri: "/search/" + email,
        });
    }

    async register<T>(payload: unknown): Promise<ApiResponse<T>> {
        return apiClient({
            type: "post",
            service: "profile",
            uri: "/register",
            payload,
        });
    }

    async getUserById<T>(userId: string): Promise<ApiResponse<T>> {
        return apiClient({
            type: "get",
            service: "profile",
            uri: "/" + userId,
        });
    }

    async getAllUsers<T>(role?: RoleType): Promise<ApiResponse<T>> {
        return apiClient({
            type: "get",
            service: "profile",
            uri: role ? "/role/" + role : "/",
        });
    }

    async createUser<T>(payload: unknown): Promise<ApiResponse<T>> {
        return apiClient({
            type: "post",
            service: "profile",
            uri: "/",
            payload,
        });
    }

    async updateProfile<T>(
        id: string,
        payload: unknown,
    ): Promise<ApiResponse<T>> {
        return apiClient({
            type: "put",
            service: "profile",
            uri: "/" + id,
            payload,
        });
    }

    async deleteProfile<T>(id: string): Promise<ApiResponse<T>> {
        return apiClient({
            type: "delete",
            service: "profile",
            uri: "/" + id,
        });
    }

    async changePassword<T>(payload: unknown): Promise<ApiResponse<T>> {
        return apiClient({
            type: "patch",
            service: "profile",
            uri: "/",
            payload,
        });
    }
}

export default new UserService();
