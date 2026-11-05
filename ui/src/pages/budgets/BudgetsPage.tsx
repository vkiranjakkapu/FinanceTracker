import {
    ArrowPathIcon,
    BanknotesIcon,
    CalendarDaysIcon,
    CheckBadgeIcon,
    PencilSquareIcon,
    PlusIcon,
    TrashIcon,
} from "@heroicons/react/24/outline";
import { useEffect, useMemo, useState } from "react";

import type { ErrorResponse } from "../../api/api";

import InputComponent from "../../components/formelements/InputComponent";
import SelectComponent from "../../components/formelements/SelectComponent";
import SectionLayoutComponent from "../../components/layout/SectionLayoutComponent";
import ModalComponent from "../../components/ModalComponent";

import Notification, {
    type NotificationProps,
} from "../../components/notifications/Notification";

import type { BadgeProps } from "../../components/BadgeComponent";
import usePagination from "../../components/pagination/usePagination";
import TableComponent from "../../components/TableComponent";

import BudgetService from "../../services/BudgetService";
import CategoryService from "../../services/CategoryService";
import DashboardService from "../../services/DashboardService";

import type {
    Budget,
    BudgetUtilization,
    Category,
    CreateBudgetRequest,
} from "../../types/FinanceTypes";

import ActionButton from "../../components/ActionButtonComponent";
import { formatCurrency } from "../../utils/CurrencyFormatter";
import SummaryCard from "../dashboard/SummaryCard";

function getCurrentMonth(): string {
    const now = new Date();

    return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
}

const EMPTY_FORM: CreateBudgetRequest = {
    categoryId: "",
    month: getCurrentMonth(),
    amount: 0,
};

type BudgetRow = Budget & {
    spent: number;
    remaining: number;
    utilizationPercentage: number;
};

function toBackendMonth(month: string): string {
    return `${month}-01`;
}

export default function BudgetsPage() {
    const [budgets, setBudgets] = useState<Budget[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [utilization, setUtilization] = useState<BudgetUtilization[]>([]);

    const [selectedMonth, setSelectedMonth] = useState(getCurrentMonth());

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);

    const [notifications, setNotifications] =
        useState<NotificationProps | null>(null);

    const [refreshCount, setRefreshCount] = useState(0);

    const [modalOpen, setModalOpen] = useState(false);
    const [editingBudget, setEditingBudget] = useState<Budget | null>(null);
    const [form, setForm] = useState<CreateBudgetRequest>(EMPTY_FORM);

    const [fetchError, setFetchError] = useState<boolean>(false);
    useEffect(() => {
        if (!selectedMonth.trim()) {
            queueMicrotask(() => {
                setLoading(false);
            });
            return;
        }

        let active = true;

        Promise.all([
            BudgetService.getBudgets(selectedMonth),
            CategoryService.getCategories("EXPENSE"),
            DashboardService.getDashboard(selectedMonth),
        ])
            .then(([budgetResponse, categoryResponse, dashboardResponse]) => {
                if (!active) return;

                setBudgets(budgetResponse.data);
                setCategories(categoryResponse.data);
                setUtilization(dashboardResponse.data.budgetUtilization ?? []);
                setFetchError(false);
            })
            .catch((error: ErrorResponse) => {
                if (!active) return;

                if (error.errorCode === "500") {
                    setFetchError(true);
                }

                setNotifications({
                    type: "error",
                    messages:
                        error.errorCode === "500"
                            ? "Unable to load budget information."
                            : error,
                });
            })
            .finally(() => {
                if (active) {
                    setLoading(false);
                }
            });

        return () => {
            active = false;
        };
    }, [selectedMonth, refreshCount]);

    const budgetRows = useMemo<BudgetRow[]>(() => {
        const utilizationByCategory = new Map(
            utilization.map((item) => [item.categoryId, item]),
        );

        return budgets.map((budget) => {
            const data = utilizationByCategory.get(budget.categoryId);

            return {
                ...budget,
                spent: data?.spentAmount ?? 0,
                remaining: data?.remainingAmount ?? budget.amount,
                utilizationPercentage: data?.utilizationPercentage ?? 0,
            };
        });
    }, [budgets, utilization]);

    const summary = useMemo(
        () =>
            budgetRows.reduce(
                (result, budget) => ({
                    totalBudget: result.totalBudget + budget.amount,
                    totalSpent: result.totalSpent + budget.spent,
                    totalRemaining: result.totalRemaining + budget.remaining,
                }),
                {
                    totalBudget: 0,
                    totalSpent: 0,
                    totalRemaining: 0,
                },
            ),
        [budgetRows],
    );

    const availableCategories = useMemo(
        () =>
            categories.filter(
                (category) =>
                    !budgets.some(
                        (budget) =>
                            budget.categoryId === category.id &&
                            budget.id !== editingBudget?.id,
                    ),
            ),
        [categories, budgets, editingBudget],
    );

    const pagination = usePagination(budgetRows, 10);

    const resetForm = () => {
        setForm({
            ...EMPTY_FORM,
            month: selectedMonth,
        });

        setEditingBudget(null);
    };

    const openCreateModal = () => {
        resetForm();
        setModalOpen(true);
    };

    const openEditModal = (budget: Budget) => {
        setEditingBudget(budget);

        setForm({
            categoryId: budget.categoryId,
            month: budget.month.slice(0, 7),
            amount: budget.amount,
        });

        setModalOpen(true);
    };

    const closeModal = () => {
        if (saving) return;

        setModalOpen(false);
        resetForm();
    };

    const handleSubmit = () => {
        if (!form.categoryId) {
            setNotifications({
                type: "error",
                messages: "Please select a category.",
            });
            return;
        }

        if (form.amount <= 0) {
            setNotifications({
                type: "error",
                messages: "Budget amount must be greater than zero.",
            });
            return;
        }

        setSaving(true);
        setNotifications(null);

        const payload: CreateBudgetRequest = {
            categoryId: form.categoryId,
            month: toBackendMonth(form.month),
            amount: Number(form.amount),
        };

        const actionPromise = editingBudget
            ? BudgetService.updateBudget(editingBudget.id, payload)
            : BudgetService.createBudget(payload);

        actionPromise
            .then(() => {
                setModalOpen(false);
                resetForm();

                setNotifications({
                    type: "success",
                    messages: editingBudget
                        ? "Budget updated successfully."
                        : "Budget created successfully.",
                });

                setSelectedMonth(form.month);
                setRefreshCount((count) => count + 1);
            })
            .catch((error: ErrorResponse) => {
                setNotifications({
                    type: "error",
                    messages:
                        error.errorCode === "500"
                            ? "Unable to save budget."
                            : error,
                });
            })
            .finally(() => {
                setSaving(false);
            });
    };

    const handleDelete = (budget: Budget) => {
        const confirmed = window.confirm(
            `Are you sure you want to delete the budget for "${budget.categoryName}"?`,
        );

        if (!confirmed) return;

        setNotifications(null);

        BudgetService.deleteBudget(budget.id)
            .then(() => {
                setNotifications({
                    type: "success",
                    messages: "Budget deleted successfully.",
                });

                setRefreshCount((count) => count + 1);
            })
            .catch((error: ErrorResponse) => {
                setNotifications({
                    type: "error",
                    messages:
                        error.errorCode === "500"
                            ? "Unable to delete budget."
                            : error,
                });
            });
    };

    return (
        <>
            <SectionLayoutComponent
                title="Budgets"
                description="Plan your monthly spending and monitor budget utilization."
                actionButtons={
                    fetchError
                        ? [
                              {
                                  icon: ArrowPathIcon,
                                  text: "Retry",
                                  className: "primary",
                                  disabled: loading && budgets.length === 0,
                                  onClick() {
                                      setLoading(true);
                                      setNotifications(null);
                                      setRefreshCount((count) => count + 1);
                                  },
                              },
                          ]
                        : [
                              {
                                  icon: PlusIcon,
                                  text: "Add Budget",
                                  className: "primary",
                                  onClick: openCreateModal,
                              },
                          ]
                }
            >
                <div className="space-y-5">
                    {notifications && <Notification {...notifications} />}

                    {/* Budget summary */}
                    <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
                        <SummaryCard
                            title="Total Budget"
                            amount={summary.totalBudget}
                            description="Planned spending"
                            spinner={{
                                isLoading: loading && budgets.length === 0,
                            }}
                        />

                        <SummaryCard
                            title="Total Spent"
                            amount={summary.totalSpent}
                            description="Amount utilized"
                            spinner={{
                                isLoading: loading && budgets.length === 0,
                            }}
                        />

                        <SummaryCard
                            title="Remaining"
                            amount={summary.totalRemaining}
                            description="Available budget"
                            spinner={{
                                isLoading: loading && budgets.length === 0,
                            }}
                        />
                    </div>

                    {/* Budget table */}
                    <TableComponent
                        title="Monthly Budgets"
                        description={`${budgetRows.length} budget(s) found`}
                        emptyDataMessage="No budgets found for this month. Start by creating your first budget."
                        spinner={{
                            isLoading: loading,
                            text: "Loading budgets...",
                        }}
                        className="rounded-xl border"
                        body={pagination.currentItems}
                        columns={[
                            {
                                key: "categoryName",
                                alias: "category",
                            },
                            {
                                key: "amount",
                                alias: "budget",
                                renderColumn(value) {
                                    return formatCurrency(Number(value));
                                },
                            },
                            {
                                key: "spent",
                                renderColumn(value) {
                                    return formatCurrency(Number(value));
                                },
                            },
                            {
                                key: "remaining",
                                renderType: "html",
                                renderColumn(value) {
                                    const amount = Number(value);

                                    return (
                                        <span
                                            className={
                                                amount < 0
                                                    ? "text-danger"
                                                    : "text-emerald-600"
                                            }
                                        >
                                            {formatCurrency(amount)}
                                        </span>
                                    );
                                },
                            },
                            {
                                key: "utilizationPercentage",
                                alias: "utilization",
                                renderType: "badge",
                                renderColumn(value) {
                                    const percentage = Number(value);

                                    return {
                                        value: `${percentage.toFixed(1)}%`,
                                        type:
                                            percentage >= 100
                                                ? "danger"
                                                : percentage >= 80
                                                  ? "warning"
                                                  : "success",
                                        hideIcon: true,
                                    } as BadgeProps;
                                },
                            },
                        ]}
                        actionEvents={[
                            {
                                title: "Actions",
                                className: "text-end justify-end",
                                clickEvents: [
                                    {
                                        icon: PencilSquareIcon,
                                        onClick(budget) {
                                            openEditModal(budget);
                                        },
                                    },
                                    {
                                        icon: TrashIcon,
                                        className: "theme-danger",
                                        onClick(budget) {
                                            handleDelete(budget);
                                        },
                                    },
                                ],
                            },
                        ]}
                        search={{
                            id: "budget-month",
                            type: "month",
                            label: { icon: CalendarDaysIcon },
                            value: selectedMonth,
                            onChange: (event) => {
                                setLoading(true);
                                setNotifications(null);
                                setSelectedMonth(event.target.value);
                            },
                        }}
                        pagination={pagination}
                    />

                    {/* Utilization progress */}
                    {budgetRows.length > 0 && (
                        <div className="rounded-xl border bg-theme p-5 shadow-sm dark:bg-background-dark">
                            <h2 className="mb-4 font-semibold">
                                Budget Utilization
                            </h2>

                            <div className="space-y-5">
                                {budgetRows.map((budget) => {
                                    const percentage =
                                        budget.utilizationPercentage;

                                    return (
                                        <div
                                            key={budget.id}
                                            className="space-y-2"
                                        >
                                            <div className="flex flex-wrap justify-between gap-2 text-sm">
                                                <span className="font-medium">
                                                    {budget.categoryName}
                                                </span>

                                                <span className="text-secondary">
                                                    {formatCurrency(
                                                        budget.spent,
                                                    )}{" "}
                                                    /{" "}
                                                    {formatCurrency(
                                                        budget.amount,
                                                    )}
                                                </span>
                                            </div>

                                            <div className="h-2 overflow-hidden rounded-full bg-gray-200 dark:bg-gray-700">
                                                <div
                                                    className={`h-full rounded-full ${
                                                        percentage >= 100
                                                            ? "bg-rose-500"
                                                            : percentage >= 80
                                                              ? "bg-amber-500"
                                                              : "bg-emerald-500"
                                                    }`}
                                                    style={{
                                                        width: `${Math.min(
                                                            Math.max(
                                                                percentage,
                                                                0,
                                                            ),
                                                            100,
                                                        )}%`,
                                                    }}
                                                />
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        </div>
                    )}
                </div>
            </SectionLayoutComponent>

            {/* Create / Edit Budget Modal */}
            <ModalComponent
                isOpen={modalOpen}
                onClose={closeModal}
                title={editingBudget ? "Edit Budget" : "Add Budget"}
                maxWidthClass="max-w-xl"
            >
                <form action={handleSubmit} className="space-y-4 py-1">
                    <SelectComponent
                        id="budget-category"
                        label={{ text: "ExpenseCategory:" }}
                        value={form.categoryId}
                        onChange={(event) =>
                            setForm((previous) => ({
                                ...previous,
                                categoryId: event.target.value,
                            }))
                        }
                        emptyOption="Select a category"
                        options={availableCategories.map((category) => ({
                            value: category.id,
                            text: category.name,
                        }))}
                        required
                    />

                    <InputComponent
                        id="budget-month-input"
                        type="month"
                        label={{ icon: CalendarDaysIcon }}
                        value={form.month}
                        onChange={(event) =>
                            setForm((previous) => ({
                                ...previous,
                                month: event.target.value,
                            }))
                        }
                        required
                    />

                    <InputComponent
                        id="budget-amount"
                        type="number"
                        label={{ icon: BanknotesIcon }}
                        value={form.amount || ""}
                        onChange={(event) =>
                            setForm((previous) => ({
                                ...previous,
                                amount: Number(event.target.value),
                            }))
                        }
                        placeholder="Enter budget amount"
                        min="0.01"
                        step="0.01"
                        required
                    />

                    <div className="flex justify-end gap-3 border-t pt-4">
                        <ActionButton
                            text="Cancel"
                            onClick={closeModal}
                            disabled={saving}
                        />
                        <ActionButton
                            type="submit"
                            className="primary"
                            text={
                                saving
                                    ? "Saving..."
                                    : editingBudget
                                      ? "Update Budget"
                                      : "Save Budget"
                            }
                            icon={CheckBadgeIcon}
                            disabled={
                                saving ||
                                !form.categoryId ||
                                form.amount <= 0 ||
                                !form.month
                            }
                        />
                    </div>
                </form>
            </ModalComponent>
        </>
    );
}
