import {
    ArrowPathIcon,
    InformationCircleIcon,
    MagnifyingGlassIcon,
    PlusIcon,
    SwatchIcon,
    TrashIcon,
    XCircleIcon,
} from "@heroicons/react/24/outline";
import { useEffect, useMemo, useState } from "react";

import type { ErrorResponse } from "../../api/api";

import InputComponent from "../../components/formelements/InputComponent";
import SelectComponent from "../../components/formelements/SelectComponent";
import TextareaComponent from "../../components/formelements/TextareaComponent";
import SectionLayoutComponent from "../../components/layout/SectionLayoutComponent";
import ModalComponent from "../../components/ModalComponent";

import Notification, {
    type NotificationProps,
} from "../../components/notifications/Notification";

import ActionButton from "../../components/ActionButtonComponent";
import type { BadgeProps } from "../../components/BadgeComponent";
import usePagination from "../../components/pagination/usePagination";
import TableComponent from "../../components/TableComponent";

import CategoryService from "../../services/CategoryService";

import type {
    Category,
    CreateCategoryRequest,
    TransactionType,
} from "../../types/FinanceTypes";

const EMPTY_FORM: CreateCategoryRequest = {
    name: "",
    type: "EXPENSE",
    color: "#64748b",
    description: "",
    icon: "",
};

export default function CategoriesPage() {
    const [categories, setCategories] = useState<Category[]>([]);

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);

    const [notifications, setNotifications] =
        useState<NotificationProps | null>(null);

    const [typeFilter, setTypeFilter] = useState("");
    const [searchQuery, setSearchQuery] = useState("");
    const [refreshCount, setRefreshCount] = useState(0);

    const [modalOpen, setModalOpen] = useState(false);
    const [form, setForm] = useState<CreateCategoryRequest>(EMPTY_FORM);

    useEffect(() => {
        let active = true;

        CategoryService.getCategories()
            .then((response) => {
                if (active) {
                    setCategories(response.data);
                }
            })
            .catch((error: ErrorResponse) => {
                if (!active) return;

                setNotifications({
                    type: "error",
                    messages:
                        error.errorCode === "500"
                            ? "Unable to load categories."
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
    }, [refreshCount]);

    const filteredCategories = useMemo(() => {
        return categories.filter(
            (category) =>
                (!typeFilter || category.type === typeFilter) &&
                (!searchQuery.trim() ||
                    category.name
                        .toLowerCase()
                        .includes(searchQuery.trim().toLowerCase())),
        );
    }, [categories, typeFilter, searchQuery]);

    const pagination = usePagination(filteredCategories, 10);

    const resetForm = () => {
        setForm(EMPTY_FORM);
    };

    const openCreateModal = () => {
        resetForm();
        setModalOpen(true);
    };

    const closeModal = () => {
        if (saving) return;

        setModalOpen(false);
        resetForm();
    };

    const handleSubmit = () => {
        const name = form.name.trim();

        if (!name) {
            setNotifications({
                type: "error",
                messages: "Category name is required.",
            });
            return;
        }

        setSaving(true);
        setNotifications(null);

        const payload: CreateCategoryRequest = {
            ...form,
            name,
            description: form.description?.trim() || undefined,
            icon: form.icon?.trim() || undefined,
        };

        CategoryService.createCategory(payload)
            .then(() => {
                setModalOpen(false);
                resetForm();

                setNotifications({
                    type: "success",
                    messages: "Category created successfully.",
                });

                setRefreshCount((count) => count + 1);
            })
            .catch((error: ErrorResponse) => {
                setNotifications({
                    type: "error",
                    messages:
                        error.errorCode === "500"
                            ? "Unable to create category."
                            : error,
                });
            })
            .finally(() => {
                setSaving(false);
            });
    };

    const handleDelete = (category: Category) => {
        if (category.isDefault) {
            setNotifications({
                type: "error",
                messages: "Default categories cannot be deleted.",
            });
            return;
        }

        const confirmed = window.confirm(
            `Are you sure you want to delete "${category.name}"?`,
        );

        if (!confirmed) return;

        setNotifications(null);

        CategoryService.deleteCategory(category.id)
            .then(() => {
                setNotifications({
                    type: "success",
                    messages: "Category deleted successfully.",
                });

                setRefreshCount((count) => count + 1);
            })
            .catch((error: ErrorResponse) => {
                setNotifications({
                    type: "error",
                    messages:
                        error.errorCode === "500"
                            ? "Unable to delete category."
                            : error,
                });
            });
    };

    const clearFilter = () => {
        setTypeFilter("");
        setSearchQuery("");
    };

    const hasFilters = Boolean(typeFilter) || Boolean(searchQuery);

    return (
        <>
            <SectionLayoutComponent
                title="Categories"
                description="Organize your income and expenses with categories."
                spinner={{
                    isLoading: loading && categories.length === 0,
                    text: "Loading categories...",
                }}
                actionButtons={[
                    {
                        icon: PlusIcon,
                        text: "Add Category",
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

                    {/* Category filters */}
                    <div className="rounded-xl border bg-theme p-4 shadow-sm dark:bg-background-dark">
                        <div className="mb-4 flex flex-wrap items-center justify-between gap-2">
                            <h2 className="font-semibold">Filter Categories</h2>

                            {hasFilters && (
                                <ActionButton
                                    text="Clear filter"
                                    icon={XCircleIcon}
                                    className="primary text-sm"
                                    onClick={clearFilter}
                                />
                            )}
                        </div>

                        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                            <SelectComponent
                                id="category-type-filter"
                                customise="w-full md:w-3/5"
                                label={{ text: "Category" }}
                                value={typeFilter}
                                onChange={(event) =>
                                    setTypeFilter(event.target.value)
                                }
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
                            />
                        </div>
                    </div>

                    {/* Categories table */}
                    <TableComponent
                        title="All Categories"
                        description={`${filteredCategories.length} categor${filteredCategories.length === 1 ? "y" : "ies"} found`}
                        emptyDataMessage={
                            loading
                                ? "No categories found for this type."
                                : hasFilters
                                  ? "Try changing your filters."
                                  : "No categories available."
                        }
                        spinner={{
                            isLoading: loading,
                            text: "Loading categories...",
                        }}
                        className="rounded-xl border"
                        body={pagination.currentItems}
                        columns={[
                            {
                                key: "name",
                                renderType: "html",
                                renderColumn(value, category) {
                                    const item = category as Category;

                                    return (
                                        <div className="flex items-center gap-3">
                                            <span
                                                className="size-3 shrink-0 rounded-full border"
                                                style={{
                                                    backgroundColor:
                                                        item.color || "#64748b",
                                                }}
                                            />
                                            <span className="font-medium">
                                                {String(value)}
                                            </span>
                                        </div>
                                    );
                                },
                            },
                            {
                                key: "type",
                                renderType: "badge",
                                renderColumn(value) {
                                    return {
                                        value,
                                        type:
                                            value === "INCOME"
                                                ? "success"
                                                : "danger",
                                        hideIcon: true,
                                    } as BadgeProps;
                                },
                            },
                            {
                                key: "description",
                                renderColumn(value) {
                                    return String(value || "—");
                                },
                            },
                            {
                                key: "isDefault",
                                alias: "category source",
                                renderColumn(value) {
                                    return value ? "Default" : "Custom";
                                },
                            },
                        ]}
                        actionEvents={[
                            {
                                title: "Actions",
                                className: "text-end justify-end",
                                clickEvents: [
                                    {
                                        icon: TrashIcon,
                                        className: "theme-danger",
                                        onClick(category) {
                                            handleDelete(category);
                                        },
                                    },
                                ],
                            },
                        ]}
                        pagination={pagination}
                        search={{
                            id: "category-search",
                            placeholder: "Search by Name",
                            label: { icon: MagnifyingGlassIcon },
                            onChange(e) {
                                setSearchQuery(e.target.value);
                            },
                            value: searchQuery,
                        }}
                    />
                </div>
            </SectionLayoutComponent>

            {/* Create Category Modal */}
            <ModalComponent
                isOpen={modalOpen}
                onClose={closeModal}
                title="Add Category"
                maxWidthClass="max-w-xl"
            >
                <form action={handleSubmit} className="space-y-4 py-1">
                    <SelectComponent
                        id="category-type"
                        label={{ icon: SwatchIcon, text: "Type:" }}
                        value={form.type}
                        onChange={(event) =>
                            setForm((previous) => ({
                                ...previous,
                                type: event.target.value as TransactionType,
                            }))
                        }
                        emptyOption="Select category type"
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
                        id="category-name"
                        label={{ icon: SwatchIcon, text: "Name:" }}
                        value={form.name}
                        onChange={(event) =>
                            setForm((previous) => ({
                                ...previous,
                                name: event.target.value,
                            }))
                        }
                        placeholder="e.g. Groceries"
                        maxLength={100}
                        required
                    />

                    <InputComponent
                        id="category-color"
                        label={{ icon: SwatchIcon }}
                        type="color"
                        value={form.color || "#64748b"}
                        onChange={(event) =>
                            setForm((previous) => ({
                                ...previous,
                                color: event.target.value,
                            }))
                        }
                    />

                    {/* <InputComponent
                        id="category-icon"
                        label={{ text: "Icon (Optional):" }}
                        value={form.icon || ""}
                        onChange={(event) =>
                            setForm((previous) => ({
                                ...previous,
                                icon: event.target.value,
                            }))
                        }
                        placeholder="e.g. shopping-cart"
                        maxLength={50}
                    /> */}

                    <div className="px-1">
                        <p className="mb-2 flex items-center gap-1 text-sm">
                            <InformationCircleIcon className="size-4" />
                            <span>Description (Optional)</span>
                        </p>

                        <TextareaComponent
                            id="category-description"
                            value={form.description || ""}
                            onChange={(event) =>
                                setForm((previous) => ({
                                    ...previous,
                                    description: event.target.value,
                                }))
                            }
                            rows={3}
                            maxLength={250}
                            placeholder="Describe this category..."
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
                            disabled={saving || !form.name.trim()}
                            className="primary"
                        >
                            {saving ? "Saving..." : "Save Category"}
                        </button>
                    </div>
                </form>
            </ModalComponent>
        </>
    );
}
