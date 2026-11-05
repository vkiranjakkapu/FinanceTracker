import { useCallback, useEffect, useMemo, useState } from "react";
import {
    Bar,
    BarChart,
    CartesianGrid,
    Cell,
    Legend,
    Pie,
    PieChart,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis,
} from "recharts";

import { ArrowPathIcon } from "@heroicons/react/24/outline";
import type { ErrorResponse } from "../../api/api";
import type { InputComponentProps } from "../../components/formelements/InputComponent";
import SectionLayoutComponent from "../../components/layout/SectionLayoutComponent";
import Notification, {
    type NotificationProps,
} from "../../components/notifications/Notification";
import TableComponent from "../../components/TableComponent";
import DashboardService from "../../services/DashboardService";
import type { Dashboard } from "../../types/FinanceTypes";
import { formatCurrency } from "../../utils/CurrencyFormatter";
import { DateFormatter } from "../../utils/DateFormatter";
import SectionCard from "./SectionCard";
import SummaryCard from "./SummaryCard";

const CHART_COLORS = [
    "#0d9488",
    "#3b82f6",
    "#f59e0b",
    "#8b5cf6",
    "#ec4899",
    "#64748b",
];

function getCurrentMonth(): string {
    const now = new Date();

    return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(
        2,
        "0",
    )}`;
}

export default function DashboardPage() {
    const [month, setMonth] = useState(getCurrentMonth());
    const [dashboard, setDashboard] = useState<Dashboard | null>(null);
    const [loading, setLoading] = useState(true);
    const [errors, setNotifications] = useState<NotificationProps | null>(null);

    const loadDashboard = useCallback(() => {
        DashboardService.getDashboard(month.trim() ? month : getCurrentMonth())
            .then((resp) => {
                setDashboard(resp.data);
                setNotifications(null);
            })
            .catch((e: ErrorResponse) => {
                let msg;
                if (e.errorCode === "500") {
                    msg = "Unable to load dashboard.";
                }

                setDashboard(null);
                setNotifications({
                    type: "error",
                    messages: msg ?? e,
                });
            })
            .finally(() => {
                setLoading(false);
            });
    }, [month]);

    useEffect(() => {
        loadDashboard();
    }, [loadDashboard]);

    const categoryExpenses = useMemo(
        () =>
            dashboard?.categoryExpenses?.filter((item) => item.amount > 0) ??
            [],
        [dashboard],
    );

    const monthlyTrends = dashboard?.monthlyTrends ?? [];

    return (
        <SectionLayoutComponent
            title="Dashboard"
            description="Your financial overview at a glance."
            search={
                !errors && dashboard
                    ? ({
                          type: "month",
                          defaultValue: month,
                          onChange(e) {
                              setLoading(true);
                              setMonth(e.target.value);
                          },
                          resultsLabel: !month.trim()
                              ? `Showing data for ${DateFormatter.toFormattedDate(
                                    getCurrentMonth(),
                                    {
                                        month: "short",
                                        year: "numeric",
                                    },
                                )}`
                              : undefined,
                      } as InputComponentProps)
                    : undefined
            }
            spinner={{
                isLoading: loading && !dashboard,
                text: "Loading dashboard...",
            }}
            actionButtons={
                errors && !dashboard
                    ? [
                          {
                              icon: ArrowPathIcon,
                              text: "Retry",
                              className: "primary",
                              onClick() {
                                  setLoading(true);
                                  loadDashboard();
                              },
                          },
                      ]
                    : undefined
            }
        >
            {errors ? (
                <Notification {...errors} />
            ) : (
                dashboard && (
                    <div className="space-y-3">
                        {/* Summary cards */}
                        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3">
                            <SummaryCard
                                title="Total Income"
                                amount={dashboard.totalIncome}
                                description="Income recorded this month"
                                color="text-emerald-600"
                            />

                            <SummaryCard
                                title="Total Expenses"
                                amount={dashboard.totalExpenses}
                                description="Expenses recorded this month"
                                color="text-danger"
                            />

                            <SummaryCard
                                title="Net Balance"
                                amount={dashboard.balance}
                                description="Income minus expenses"
                                color={
                                    dashboard.balance >= 0
                                        ? "text-emerald-600"
                                        : "text-danger"
                                }
                            />
                        </div>

                        {/* Charts */}
                        <div className="grid grid-cols-1 gap-6 xl:grid-cols-2">
                            <SectionCard title="Monthly Income vs Expenses">
                                {monthlyTrends.length === 0 ? (
                                    <Notification
                                        type="info"
                                        messages={
                                            "No monthly trend data available."
                                        }
                                    />
                                ) : (
                                    <div className="h-72 w-full">
                                        <ResponsiveContainer
                                            width="100%"
                                            height="100%"
                                        >
                                            <BarChart
                                                data={monthlyTrends}
                                                margin={{
                                                    top: 10,
                                                    right: 10,
                                                    left: 5,
                                                    bottom: 5,
                                                }}
                                            >
                                                <CartesianGrid
                                                    strokeDasharray="3 3"
                                                    vertical={false}
                                                />
                                                <XAxis dataKey="month" />
                                                <YAxis
                                                    tickFormatter={(value) =>
                                                        `₹${Number(value) / 1000}k`
                                                    }
                                                />
                                                <Tooltip
                                                    formatter={(value) =>
                                                        formatCurrency(
                                                            Number(value),
                                                        )
                                                    }
                                                />
                                                <Legend />
                                                <Bar
                                                    dataKey="income"
                                                    name="Income"
                                                    fill="#0d9488"
                                                    radius={[4, 4, 0, 0]}
                                                />
                                                <Bar
                                                    dataKey="expenses"
                                                    name="Expenses"
                                                    fill="#f43f5e"
                                                    radius={[4, 4, 0, 0]}
                                                />
                                            </BarChart>
                                        </ResponsiveContainer>
                                    </div>
                                )}
                            </SectionCard>

                            <SectionCard title="Expense Breakdown">
                                {categoryExpenses.length === 0 ? (
                                    <Notification
                                        type="info"
                                        messages={
                                            "No expense data available for this month."
                                        }
                                    />
                                ) : (
                                    <div className="h-72 w-full">
                                        <ResponsiveContainer
                                            width="100%"
                                            height="100%"
                                        >
                                            <PieChart>
                                                <Pie
                                                    data={categoryExpenses}
                                                    dataKey="amount"
                                                    nameKey="categoryName"
                                                    cx="50%"
                                                    cy="50%"
                                                    outerRadius={95}
                                                    innerRadius={55}
                                                    paddingAngle={3}
                                                >
                                                    {categoryExpenses.map(
                                                        (item, index) => (
                                                            <Cell
                                                                key={
                                                                    item.categoryId
                                                                }
                                                                fill={
                                                                    CHART_COLORS[
                                                                        index %
                                                                            CHART_COLORS.length
                                                                    ]
                                                                }
                                                            />
                                                        ),
                                                    )}
                                                </Pie>
                                                <Tooltip
                                                    formatter={(value) =>
                                                        formatCurrency(
                                                            Number(value),
                                                        )
                                                    }
                                                />
                                                <Legend />
                                            </PieChart>
                                        </ResponsiveContainer>
                                    </div>
                                )}
                            </SectionCard>
                        </div>

                        {/* Budget utilization */}
                        <SectionCard title="Budget Utilization">
                            {!dashboard.budgetUtilization?.length ? (
                                <Notification
                                    type="info"
                                    messages={
                                        "No budgets configured for this month."
                                    }
                                />
                            ) : (
                                <div className="space-y-5">
                                    {dashboard.budgetUtilization.map(
                                        (budget) => {
                                            const percentage = Math.max(
                                                0,
                                                budget.utilizationPercentage,
                                            );

                                            const exceeded = percentage > 100;

                                            return (
                                                <div key={budget.categoryId}>
                                                    <div className="mb-2 flex flex-wrap items-center justify-between gap-2 text-sm">
                                                        <span className="font-medium">
                                                            {
                                                                budget.categoryName
                                                            }
                                                        </span>

                                                        <span className="text-slate-500">
                                                            {formatCurrency(
                                                                budget.spentAmount,
                                                            )}{" "}
                                                            /{" "}
                                                            {formatCurrency(
                                                                budget.budgetAmount,
                                                            )}
                                                        </span>
                                                    </div>

                                                    <div className="h-2.5 overflow-hidden rounded-full bg-slate-200 dark:bg-slate-700">
                                                        <div
                                                            className={`h-full rounded-full ${
                                                                exceeded
                                                                    ? "bg-rose-500"
                                                                    : percentage >=
                                                                        80
                                                                      ? "bg-amber-500"
                                                                      : "bg-emerald-500"
                                                            }`}
                                                            style={{
                                                                width: `${Math.min(percentage, 100)}%`,
                                                            }}
                                                        />
                                                    </div>

                                                    <div className="mt-1 flex justify-between text-xs text-slate-500">
                                                        <span>
                                                            {percentage.toFixed(
                                                                1,
                                                            )}
                                                            % utilized
                                                        </span>
                                                        <span
                                                            className={
                                                                exceeded
                                                                    ? "font-medium text-rose-600"
                                                                    : ""
                                                            }
                                                        >
                                                            {exceeded
                                                                ? `${formatCurrency(Math.abs(budget.remainingAmount))} over budget`
                                                                : `${formatCurrency(budget.remainingAmount)} remaining`}
                                                        </span>
                                                    </div>
                                                </div>
                                            );
                                        },
                                    )}
                                </div>
                            )}
                        </SectionCard>

                        {/* Recent transactions */}
                        <TableComponent
                            title="Recent Transactions"
                            description="Only the recent transactions were shown below."
                            emptyDataMessage="No transactions recorded this month."
                            className="bg-theme rounded-xl border hover:shadow-md"
                            body={dashboard.recentTransactions}
                            columns={[
                                {
                                    key: "description",
                                },
                                {
                                    key: "categoryName",
                                    alias: "category",
                                },
                                {
                                    key: "transactionDate",
                                    alias: "Date",
                                    renderColumn(val) {
                                        return DateFormatter.toFormattedDate(
                                            String(val),
                                            {
                                                dateStyle: "medium",
                                            },
                                        );
                                    },
                                },
                                {
                                    key: "amount",
                                    renderType: "html",
                                    renderColumn(val, transaction) {
                                        return (
                                            <span
                                                className={`${
                                                    transaction?.type ===
                                                    "INCOME"
                                                        ? "text-emerald-600"
                                                        : "text-danger"
                                                }`}
                                            >
                                                {transaction?.type === "INCOME"
                                                    ? "+"
                                                    : "-"}
                                                {formatCurrency(Number(val))}
                                            </span>
                                        );
                                    },
                                },
                            ]}
                        />

                        <p className="text-right text-xs">
                            {`Showing data for ${DateFormatter.toFormattedDate(
                                month.trim() ? month : getCurrentMonth(),
                                {
                                    month: "short",
                                    year: "numeric",
                                },
                            )}`}
                        </p>
                    </div>
                )
            )}
        </SectionLayoutComponent>
    );
}
