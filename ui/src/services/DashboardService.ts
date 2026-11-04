import { apiClient, type ApiResponse } from "../api/api";
import type { Dashboard } from "../types/FinanceTypes";

class DashboardService {
    async getDashboard(month: string): Promise<ApiResponse<Dashboard>> {
        return apiClient({
            type: "get",
            service: "finance",
            uri: `/dashboard?month=${month}`,
        });
    }
}

export default new DashboardService();