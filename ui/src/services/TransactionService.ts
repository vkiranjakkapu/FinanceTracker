import { apiClient, type ApiResponse } from "../api/api";
import type {
    Transaction,
    TransactionType,
    CreateTransactionRequest,
} from "../types/FinanceTypes";

export interface TransactionFilters {
    type?: TransactionType;
    categoryId?: string;
    fromDate?: string;
    toDate?: string;
}

class TransactionService {
    async getTransactions(
        filters?: TransactionFilters,
    ): Promise<ApiResponse<Transaction[]>> {
        const params = new URLSearchParams();

        if (filters?.type) params.set("type", filters.type);
        if (filters?.categoryId) params.set("categoryId", filters.categoryId);
        if (filters?.fromDate) params.set("fromDate", filters.fromDate);
        if (filters?.toDate) params.set("toDate", filters.toDate);

        const query = params.toString();

        return apiClient({
            type: "get",
            service: "finance",
            uri: `/transactions${query ? `?${query}` : ""}`,
        });
    }

    async getTransaction(id: string): Promise<ApiResponse<Transaction>> {
        return apiClient({
            type: "get",
            service: "finance",
            uri: `/transactions/${id}`,
        });
    }

    async createTransaction(
        payload: CreateTransactionRequest,
    ): Promise<ApiResponse<Transaction>> {
        return apiClient({
            type: "post",
            service: "finance",
            uri: "/transactions",
            payload,
        });
    }

    async updateTransaction(
        id: string,
        payload: CreateTransactionRequest,
    ): Promise<ApiResponse<Transaction>> {
        return apiClient({
            type: "put",
            service: "finance",
            uri: `/transactions/${id}`,
            payload,
        });
    }

    async deleteTransaction(id: string): Promise<ApiResponse<void>> {
        return apiClient({
            type: "delete",
            service: "finance",
            uri: `/transactions/${id}`,
        });
    }
}

export default new TransactionService();