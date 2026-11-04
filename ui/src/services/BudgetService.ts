import { apiClient, type ApiResponse } from "../api/api";
import type {
    Budget,
    CreateBudgetRequest,
} from "../types/FinanceTypes";

class BudgetService {
    async getBudgets(month: string): Promise<ApiResponse<Budget[]>> {
        return apiClient({
            type: "get",
            service: "finance",
            uri: `/budgets?month=${month}`,
        });
    }

    async createBudget(
        payload: CreateBudgetRequest,
    ): Promise<ApiResponse<Budget>> {
        return apiClient({
            type: "post",
            service: "finance",
            uri: "/budgets",
            payload,
        });
    }

    async updateBudget(
        id: string,
        payload: CreateBudgetRequest,
    ): Promise<ApiResponse<Budget>> {
        return apiClient({
            type: "put",
            service: "finance",
            uri: `/budgets/${id}`,
            payload,
        });
    }

    async deleteBudget(id: string): Promise<ApiResponse<void>> {
        return apiClient({
            type: "delete",
            service: "finance",
            uri: `/budgets/${id}`,
        });
    }
}

export default new BudgetService();