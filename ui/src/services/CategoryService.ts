import { apiClient, type ApiResponse } from "../api/api";
import type {
    Category,
    TransactionType,
    CreateCategoryRequest,
} from "../types/FinanceTypes";

class CategoryService {
    async getCategories(
        type?: TransactionType,
    ): Promise<ApiResponse<Category[]>> {
        return apiClient({
            type: "get",
            service: "finance",
            uri: `/categories${type ? `?type=${type}` : ""}`,
        });
    }

    async createCategory(
        payload: CreateCategoryRequest,
    ): Promise<ApiResponse<Category>> {
        return apiClient({
            type: "post",
            service: "finance",
            uri: "/categories",
            payload,
        });
    }

    async deleteCategory(id: string): Promise<ApiResponse<void>> {
        return apiClient({
            type: "delete",
            service: "finance",
            uri: `/categories/${id}`,
        });
    }
}

export default new CategoryService();