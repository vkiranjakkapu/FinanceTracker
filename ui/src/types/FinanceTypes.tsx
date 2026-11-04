export type TransactionType = "INCOME" | "EXPENSE";

export interface Transaction {
    id: string;
    type: TransactionType;
    amount: number;
    description: string;
    categoryId: string;
    categoryName: string;
    transactionDate: string;
    notes?: string;
    createdAt: string;
    updatedAt: string;
}

export interface Category {
    id: string;
    name: string;
    type: TransactionType;
    color?: string;
    icon?: string;
    description?: string;
    isDefault: boolean;
}

export interface Budget {
    id: string;
    categoryId: string;
    categoryName: string;
    month: string;
    amount: number;
    createdAt: string;
    updatedAt: string;
}

export interface CategoryExpense {
    categoryId: string;
    categoryName: string;
    amount: number;
}

export interface MonthlyTrend {
    month: string;
    income: number;
    expenses: number;
}

export interface BudgetUtilization {
    categoryId: string;
    categoryName: string;
    budgetAmount: number;
    spent: number;
    remaining: number;
    utilizationPercentage: number;
}

export interface Dashboard {
    month: string;
    totalIncome: number;
    totalExpenses: number;
    balance: number;
    categoryExpenses: CategoryExpense[];
    monthlyTrends: MonthlyTrend[];
    recentTransactions: Transaction[];
    budgetUtilization: BudgetUtilization[];
}

// Request types

export interface CreateTransactionRequest {
    type: TransactionType;
    amount: number;
    description: string;
    categoryId: string;
    transactionDate: string;
    notes?: string;
}

export interface CreateCategoryRequest {
    name: string;
    type: TransactionType;
    color?: string;
    description?: string;
    icon?: string;
}

export interface CreateBudgetRequest {
    categoryId: string;
    month: string;
    amount: number;
}