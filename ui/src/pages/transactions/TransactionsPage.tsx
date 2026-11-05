import {
    ArrowPathIcon,
    BanknotesIcon,
    CalendarDaysIcon,
    InformationCircleIcon,
    MagnifyingGlassIcon,
    PencilSquareIcon,
    PlusIcon,
    SwatchIcon,
    TrashIcon,
    XCircleIcon,
} from "@heroicons/react/24/outline";
import { useCallback, useEffect, useMemo, useState } from "react";

import type { ErrorResponse } from "../../api/api";

import InputComponent from "../../components/formelements/InputComponent";
import SelectComponent from "../../components/formelements/SelectComponent";
import TextareaComponent from "../../components/formelements/TextareaComponent";
import SectionLayoutComponent from "../../components/layout/SectionLayoutComponent";
import ModalComponent from "../../components/ModalComponent";

import Notification, {
    type NotificationProps,
} from "../../components/notifications/Notification";

import TransactionService, {
    type TransactionFilters,
} from "../../services/TransactionService";

import CategoryService from "../../services/CategoryService";

import type {
    Category,
    CreateTransactionRequest,
    Transaction,
    TransactionType,
} from "../../types/FinanceTypes";

import ActionButton from "../../components/ActionButtonComponent";
import type { BadgeProps } from "../../components/BadgeComponent";
import usePagination from "../../components/pagination/usePagination";
import TableComponent from "../../components/TableComponent";
import { formatCurrency } from "../../utils/CurrencyFormatter";
import { DateFormatter } from "../../utils/DateFormatter";

const EMPTY_FORM: CreateTransactionRequest = {
    type: "EXPENSE",
    amount: 0,
    description: "",
    categoryId: "",
    transactionDate: new Date().toLocaleDateString("en-CA"),
    notes: "",
};

export default function TransactionsPage() {
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);

    const [notifications, setNotifications] =
        useState<NotificationProps | null>(null);

    const [typeFilter, setTypeFilter] = useState("");
    const [categoryFilter, setCategoryFilter] = useState("");
    const [fromDate, setFromDate] = useState("");
    const [toDate, setToDate] = useState("");
    const [searchQuery, setSearchQuery] = useState("");

    const [refreshCount, setRefreshCount] = useState(0);

    const [modalOpen, setModalOpen] = useState(false);
    const [editingTransaction, setEditingTransaction] =
        useState<Transaction | null>(null);

    const [form, setForm] = useState<CreateTransactionRequest>(EMPTY_FORM);

    const [categoryError, setCategoryError] = useState("");

    useEffect(() => {
        let active = true;

        CategoryService.getCategories()
            .then((response) => {
                if (active) {
                    setCategories(response.data);
                }
            })
            .catch((error: ErrorResponse) => {
                if (active) {
                    setNotifications({
                        type: "error",
                        messages: error,
                    });
                }
            });

        return () => {
            active = false;
        };
    }, []);

    useEffect(() => {
        let active = true;

        const filters: TransactionFilters = {
            type: typeFilter ? (typeFilter as TransactionType) : undefined,
            categoryId: categoryFilter || undefined,
            fromDate: fromDate || undefined,
            toDate: toDate || undefined,
        };

        TransactionService.getTransactions(filters)
            .then((response) => {
                if (!active) return;

                setTransactions(response.data);
                setNotifications(null);
            })
            .catch((error: ErrorResponse) => {
                if (!active) return;

                setTransactions([]);

                setNotifications({
                    type: "error",
                    messages:
                        error.errorCode === "500"
                            ? "Unable to load transactions."
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
    }, [typeFilter, categoryFilter, fromDate, toDate, refreshCount]);

    const availableCategories = useMemo(
        () => categories.filter((category) => category.type === form.type),
        [categories, form.type],
    );

    const filterCategories = useMemo(
        () =>
            categories.filter(
                (category) => !typeFilter || category.type === typeFilter,
            ),
        [categories, typeFilter],
    );

    const resetForm = useCallback(() => {
        setForm({
            ...EMPTY_FORM,
            transactionDate: new Date().toLocaleDateString("en-CA", {
                timeZone: "Asia/Kolkata",
            }),
        });
        setEditingTransaction(null);
        setCategoryError("");
    }, []);

    const openCreateModal = () => {
        resetForm();
        setModalOpen(true);
    };

    const openEditModal = (transaction: Transaction) => {
        setEditingTransaction(transaction);

        setForm({
            type: transaction.type,
            amount: transaction.amount,
            description: transaction.description,
            categoryId: transaction.categoryId,
            transactionDate: transaction.transactionDate,
            notes: transaction.notes ?? "",
        });

        setCategoryError("");
        setModalOpen(true);
    };

    const closeModal = () => {
        if (saving) return;

        setModalOpen(false);
        resetForm();
    };

    const handleTypeChange = (type: TransactionType) => {
        setForm((previous) => ({
            ...previous,
            type,
            categoryId: "",
        }));

        setCategoryError("");
    };

    const handleSubmit = () => {
        if (!form.categoryId) {
            setCategoryError("Please select a category.");
            return;
        }

        if (form.amount <= 0) {
            setNotifications({
                type: "error",
                messages: "Amount must be greater than zero.",
            });
            return;
        }

        setSaving(true);
        setNotifications(null);

        const payload: CreateTransactionRequest = {
            ...form,
            amount: Number(form.amount),
            description: form.description.trim(),
            notes: form.notes?.trim() || undefined,
        };

        const actionPromise = editingTransaction
            ? TransactionService.updateTransaction(
                  editingTransaction.id,
                  payload,
              )
            : TransactionService.createTransaction(payload);

        actionPromise
            .then(() => {
                setModalOpen(false);
                resetForm();

                setNotifications({
                    type: "success",
                    messages: editingTransaction
                        ? "Transaction updated successfully."
                        : "Transaction created successfully.",
                });

                setRefreshCount((count) => count + 1);
            })
            .catch((e: ErrorResponse) => {
                setNotifications({
                    type: "error",
                    messages:
                        e.errorCode === "500"
                            ? "Unable to save transaction."
                            : e,
                });
            })
            .finally(() => {
                setSaving(false);
            });
    };

    const handleDelete = async (transaction: Transaction) => {
        const confirmed = window.confirm(
            `Are you sure you want to delete "${transaction.description}"?`,
        );

        if (!confirmed) return;

        setNotifications(null);

        TransactionService.deleteTransaction(transaction.id)
            .then(() => {
                setNotifications({
                    type: "success",
                    messages: "Transaction deleted successfully.",
                });
                setRefreshCount((count) => count + 1);
            })
            .catch((e: ErrorResponse) => {
                setNotifications({
                    type: "error",
                    messages:
                        e.errorCode === "500"
                            ? "Unable to delete transaction."
                            : e,
                });
            });
    };

    const clearFilters = () => {
        setTypeFilter("");
        setCategoryFilter("");
        setFromDate("");
        setToDate("");
        setSearchQuery("");
    };

    const hasFilters =
        Boolean(typeFilter) ||
        Boolean(categoryFilter) ||
        Boolean(fromDate) ||
        Boolean(toDate) ||
        Boolean(searchQuery);

    const queryTransactions = useMemo(() => {
        if (!searchQuery.trim()) {
            return transactions;
        }

        return transactions.filter((tx) =>
            tx.description.toLowerCase().includes(searchQuery.toLowerCase()),
        );
    }, [searchQuery, transactions]);

    const pagination = usePagination(queryTransactions ?? [], 10);
    return (
        <>
            <SectionLayoutComponent
                title="Transactions"
                description="Manage and track your income and expenses."
                spinner={{
                    isLoading: loading && transactions.length === 0,
                    text: "Loading transactions...",
                }}
                actionButtons={[
                    {
                        icon: PlusIcon,
                        text: "Add Transaction",
                        className: "primary",
                        onClick: openCreateModal,
                    },
                    {
                        icon: ArrowPathIcon,
                        text: "Refresh",
                        onClick() {
                            setLoading(true);
                            setRefreshCount((count) => count + 1);
                        },
                    },
                ]}
            >
                <div className="space-y-5">
                    {notifications && <Notification {...notifications} />}

                    {/* Filters */}
                    <div className="bg-theme dark:bg-background-dark rounded-xl border p-4 shadow-sm">
                        <div className="mb-4 flex flex-wrap items-center justify-between gap-2">
                            <h2 className="font-semibold">
                                Filter Transactions
                            </h2>

                            {hasFilters && (
                                <ActionButton
                                    text="Clear filters"
                                    icon={XCircleIcon}
                                    className="primary text-sm"
                                    onClick={clearFilters}
                                />
                            )}
                        </div>

                        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
                            <SelectComponent
                                id="filter-type"
                                value={typeFilter}
                                onChange={(event) => {
                                    setLoading(true);
                                    setTypeFilter(event.target.value);
                                    setCategoryFilter("");
                                }}
                                emptyOption="All types"
                                options={[
                                    {
                                        value: "INCOME",
                                        text: "Income",
                                    },
                                    {
                                        value: "EXPENSE",
                                        text: "Expense",
                                    },
                                ]}
                                label={{ text: "Type" }}
                                disabled={
                                    !hasFilters && transactions.length === 0
                                }
                            />

                            <SelectComponent
                                id="filter-category"
                                value={categoryFilter}
                                onChange={(event) => {
                                    setLoading(true);
                                    setCategoryFilter(event.target.value);
                                }}
                                emptyOption="All categories"
                                options={filterCategories.map((category) => ({
                                    value: category.id,
                                    text: category.name,
                                }))}
                                label={{ icon: SwatchIcon }}
                                disabled={
                                    !hasFilters && transactions.length === 0
                                }
                            />

                            <InputComponent
                                id="from-date"
                                type="date"
                                value={fromDate}
                                onChange={(event) => {
                                    const value = event.target.value;

                                    setLoading(true);
                                    setFromDate(value);

                                    if (toDate && value > toDate) {
                                        setToDate("");
                                    }
                                }}
                                label={{ text: "From" }}
                                disabled={
                                    !hasFilters && transactions.length === 0
                                }
                            />

                            <InputComponent
                                id="to-date"
                                type="date"
                                value={toDate}
                                min={fromDate || undefined}
                                onChange={(event) => {
                                    setLoading(true);
                                    setToDate(event.target.value);
                                }}
                                label={{ text: "To" }}
                                disabled={
                                    !hasFilters && transactions.length === 0
                                }
                            />
                        </div>
                    </div>

                    {/* Transaction list */}
                    <TableComponent
                        title={`All Transactions`}
                        description={`${transactions.length} transaction(s) found`}
                        emptyDataMessage={
                            loading
                                ? "No transactions found."
                                : hasFilters
                                  ? "Try changing your filters."
                                  : "Start by adding your first transaction."
                        }
                        spinner={{
                            isLoading: loading,
                            text: "Loading transactions...",
                        }}
                        className="rounded-xl border"
                        body={pagination.currentItems}
                        columns={[
                            { key: "description" },
                            { key: "categoryName", alias: "category" },
                            {
                                key: "type",
                                renderType: "badge",
                                renderColumn(val) {
                                    return {
                                        value: val,
                                        type:
                                            (val as TransactionType) ===
                                            "INCOME"
                                                ? "success"
                                                : "danger",
                                        hideIcon: true,
                                    } as BadgeProps;
                                },
                            },
                            {
                                key: "transactionDate",
                                alias: "date",
                                renderColumn(val) {
                                    return DateFormatter.toFormattedDate(
                                        String(val),
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
                                                transaction?.type === "INCOME"
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
                        actionEvents={[
                            {
                                title: "Actions",
                                className: "text-end justify-end",
                                clickEvents: [
                                    {
                                        icon: PencilSquareIcon,
                                        onClick(transaction) {
                                            openEditModal(transaction);
                                        },
                                    },
                                    {
                                        icon: TrashIcon,
                                        className: "theme-danger",
                                        onClick(transaction) {
                                            handleDelete(transaction);
                                        },
                                    },
                                ],
                            },
                        ]}
                        search={{
                            id: "search-transactions",
                            placeholder: "Search by description",
                            label: { icon: MagnifyingGlassIcon },
                            onChange(e) {
                                setSearchQuery(e.target.value);
                            },
                            disabled: transactions.length === 0,
                            value: searchQuery,
                            resultsLabel: searchQuery
                                ? `Found ${pagination.currentItems.length} matches`
                                : undefined,
                        }}
                        pagination={pagination}
                    />
                </div>
            </SectionLayoutComponent>

            {/* Create / Edit Modal */}
            <ModalComponent
                isOpen={modalOpen}
                onClose={closeModal}
                title={
                    editingTransaction ? "Edit Transaction" : "Add Transaction"
                }
                maxWidthClass="max-w-xl"
            >
                <form action={handleSubmit} className="space-y-4 py-1">
                    <SelectComponent
                        id="transaction-type"
                        label={{ text: "Type:" }}
                        value={form.type}
                        onChange={(event) =>
                            handleTypeChange(
                                event.target.value as TransactionType,
                            )
                        }
                        emptyOption="Select Transaction Type"
                        options={[
                            {
                                value: "INCOME",
                                text: "Income",
                            },
                            {
                                value: "EXPENSE",
                                text: "Expense",
                            },
                        ]}
                        required
                    />

                    <InputComponent
                        id="transaction-description"
                        label={{ text: "Note: " }}
                        value={form.description}
                        onChange={(event) =>
                            setForm((previous) => ({
                                ...previous,
                                description: event.target.value,
                            }))
                        }
                        placeholder="e.g. Monthly salary"
                        required
                        maxLength={150}
                    />

                    <InputComponent
                        id="transaction-amount"
                        label={{ icon: BanknotesIcon }}
                        type="number"
                        value={form.amount || ""}
                        onChange={(event) =>
                            setForm((previous) => ({
                                ...previous,
                                amount: Number(event.target.value),
                            }))
                        }
                        placeholder="Enter amount"
                        min="0.01"
                        step="0.01"
                        required
                    />

                    <SelectComponent
                        id="transaction-category"
                        label={{ icon: SwatchIcon }}
                        value={form.categoryId}
                        onChange={(event) => {
                            setForm((previous) => ({
                                ...previous,
                                categoryId: event.target.value,
                            }));
                            setCategoryError("");
                        }}
                        emptyOption="Select a category"
                        options={availableCategories.map((category) => ({
                            value: category.id,
                            text: category.name,
                        }))}
                        required
                    />

                    {categoryError && (
                        <p className="text-sm text-rose-600">{categoryError}</p>
                    )}

                    <InputComponent
                        id="transaction-date"
                        label={{ icon: CalendarDaysIcon }}
                        type="date"
                        value={form.transactionDate}
                        onChange={(event) =>
                            setForm((previous) => ({
                                ...previous,
                                transactionDate: event.target.value,
                            }))
                        }
                        required
                    />

                    <div className="px-1">
                        <p className="flex items-center gap-1 text-sm">
                            <InformationCircleIcon className="size-4" />
                            <span>Notes (Optional)</span>
                        </p>
                        <TextareaComponent
                            id="transaction-notes"
                            value={form.notes ?? ""}
                            onChange={(event) =>
                                setForm((previous) => ({
                                    ...previous,
                                    notes: event.target.value,
                                }))
                            }
                            rows={3}
                            maxLength={500}
                            placeholder="Additional details..."
                        />
                    </div>

                    <div className="flex justify-end gap-3 border-t pt-4">
                        <button
                            type="button"
                            onClick={closeModal}
                            disabled={saving}
                            className="secondary"
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            disabled={
                                saving || availableCategories.length === 0
                            }
                            className="primary"
                        >
                            {saving
                                ? "Saving..."
                                : editingTransaction
                                  ? "Update Transaction"
                                  : "Save Transaction"}
                        </button>
                    </div>
                </form>
            </ModalComponent>
        </>
    );
}
