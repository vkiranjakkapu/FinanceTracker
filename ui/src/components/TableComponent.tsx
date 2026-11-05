import {
    type ReactNode,
    type TableHTMLAttributes,
    type TdHTMLAttributes,
} from "react";

import type { ActionButtonProps } from "./ActionButtonComponent";
import ActionButton from "./ActionButtonComponent";
import type { BadgeProps } from "./BadgeComponent";
import BadgeComponent from "./BadgeComponent";
import type { InputComponentProps } from "./formelements/InputComponent";
import InputComponent from "./formelements/InputComponent";
import {
    PaginationButtons,
    type PaginationButtonsProps,
} from "./pagination/PaginationButtons";
import SpinnerComponent, {
    type SpinnerComponentProps,
} from "./SpinnerComponent";

/**
 * Configuration properties for the generic {@link TableComponent}.
 *
 * @template T
 * The type of each row in the table.
 *
 * @example
 * ```tsx
 * type User = {
 *     id: number;
 *     name: string;
 *     email: string;
 * };
 *
 * const users: User[] = [...];
 *
 * <TableComponent<User>
 *     title="Users"
 *     body={users}
 * />
 * ```
 */
export type TableComponentProps<T> = TableHTMLAttributes<HTMLTableElement> & {
    /**
     * Optional title displayed above the table.
     *
     * @example
     * ```tsx
     * title="Users"
     * ```
     */
    title?: string;

    /**
     * Optional description displayed below the table title.
     */
    description?: string;

    /**
     * Defines the columns to display and their aliases.
     *
     * When omitted, the component automatically derives the columns
     * from the row containing the highest number of properties.
     *
     * @example
     * ```tsx
     * headers={[
     *     { key: "name", alias: "User Name" },
     *     { key: "email", alias: "Email Address" },
     * ]}
     * ```
     */
    columns?: HeaderAlias<T>[];

    /**
     * Controls whether the table header should be disabled.
     *
     * When `true`, the `<thead>` is not rendered.
     *
     * @default false
     */
    enableHeader?: boolean;

    /**
     * Data rows rendered by the table.
     *
     * Each object represents one table row.
     */
    body: T[];

    tableClassName?: string;
    actionButtons?: ActionButtonProps[];
    emptyDataMessage?: string;

    /**
     * Defines additional action columns displayed at the end
     * of each table row.
     *
     * An action can optionally provide a navigation event that
     * receives the complete row item when clicked.
     *
     * @example
     * ```tsx
     * actionEvents={[
     *     {
     *         title: "Actions",
     *         navigation: {
     *             text: "View",
     *             onClick: (user) => {
     *                 console.log(user);
     *             },
     *         },
     *     },
     * ]}
     * ```
     */
    actionEvents?: {
        /**
         * Header text displayed for the action column.
         */
        title: string;

        useText?: boolean;

        /**
         * Optional actions configuration.
         */
        clickEvents: (Omit<ActionButtonProps, "onClick"> & {
            onClick?: (item: T) => void;
        })[];

        className?: string;
    }[];

    /**
     * Optional loading element to display before the table data got prepared.
     *
     */
    spinner?: SpinnerComponentProps;

    /**
     * Optional search configuration passed to the
     * {@link SectionLayoutComponent}.
     *
     * The table itself does not perform filtering; search behavior
     * is expected to be handled by the consumer.
     */
    search?: InputComponentProps;

    /**
     * Optional pagination configuration passed to the
     * {@link SectionLayoutComponent}.
     *
     * Pagination behavior is controlled by the supplied
     * pagination component configuration.
     */
    pagination?: PaginationButtonsProps;
};

/**
 * Defines the relationship between a property of a table row and
 * the label displayed for that property in the table header.
 *
 * @template T
 * The type of the table row.
 *
 * @example
 * ```tsx
 * type User = {
 *     id: number;
 *     name: string;
 * };
 *
 * const headers: HeaderAlias<User>[] = [
 *     {
 *         key: "id",
 *         alias: "User ID",
 *     },
 *     {
 *         key: "name",
 *         alias: "User Name",
 *     },
 * ];
 * ```
 */
export type HeaderAlias<T> = {
    /**
     * Property from the row object that should be rendered.
     */
    key: keyof T;

    /**
     * Optional human-readable column heading.
     *
     * If omitted, the property name is used as the heading.
     */
    alias?: string;

    /**
     * Optional Column content customisation.
     *
     * Values suplied is used as 'className' for the column value cells
     */
    className?: string;

    props?: (row?: T) => TdHTMLAttributes<HTMLElement>;
    renderColumn?: (val: unknown, row?: T) => string | BadgeProps | ReactNode;
    renderType?: "string" | "badge" | "html";
};

/**
 * Generic and reusable table component.
 *
 * The component supports:
 *
 * - Generic row types through `T`
 * - Automatic column discovery
 * - Explicit column configuration
 * - Column aliases
 * - Optional table header
 * - Row-level actions
 * - Search configuration
 * - Pagination configuration
 * - Horizontal scrolling for wide tables
 * - Generic cell-value rendering
 * - Empty-state handling
 *
 * When `headers` are not supplied, the component determines the table
 * columns automatically by inspecting the row containing the greatest
 * number of properties. This allows the component to handle data where
 * individual rows may contain different properties.
 *
 * @template T
 * The type of each row in the table.
 *
 * @param props
 * Component configuration and standard HTML table attributes.
 *
 * @returns A reusable table wrapped inside {@link SectionLayoutComponent}.
 *
 * @example
 * Basic usage with automatic headers:
 *
 * ```tsx
 * type User = {
 *     id: number;
 *     name: string;
 *     email: string;
 * };
 *
 * const users: User[] = [
 *     {
 *         id: 1,
 *         name: "Venkat",
 *         email: "venkat@example.com",
 *     },
 * ];
 *
 * <TableComponent<User>
 *     title="Users"
 *     description="Registered users"
 *     body={users}
 * />
 * ```
 *
 * @example
 * Usage with explicit headers:
 *
 * ```tsx
 * <TableComponent<User>
 *     title="Users"
 *     body={users}
 *     headers={[
 *         { key: "id", alias: "ID" },
 *         { key: "name", alias: "Name" },
 *         { key: "email", alias: "Email" },
 *     ]}
 * />
 * ```
 *
 * @example
 * Usage with row actions:
 *
 * ```tsx
 * <TableComponent<User>
 *     title="Users"
 *     body={users}
 *     actionEvents={[
 *         {
 *             title: "Actions",
 *             navigation: {
 *                 text: "View",
 *                 onClick: (user) => {
 *                     console.log("Selected user:", user);
 *                 },
 *             },
 *         },
 *     ]}
 * />
 * ```
 */
export default function TableComponent<T>({
    title,
    description,
    columns: headers,
    enableHeader = false,
    body,
    actionButtons,
    actionEvents,
    spinner,
    search,
    pagination,
    tableClassName,
    emptyDataMessage,
    ...props
}: TableComponentProps<T>) {
    /**
     * Determines the columns that should be rendered.
     *
     * If explicit headers are provided, they take precedence.
     *
     * Otherwise, the component examines the row with the greatest
     * number of properties and derives the column definitions from
     * that row.
     *
     * An empty data set results in an empty column definition.
     */
    const cols =
        body.length != 0
            ? !headers || headers.length == 0
                ? Object.keys(
                      body.reduce(
                          (maxRow, currentRow) =>
                              Object.keys(currentRow as object).length >
                              Object.keys(maxRow as object).length
                                  ? currentRow
                                  : maxRow,
                          body[0],
                      ) as object,
                  ).map((key) => ({ key }) as HeaderAlias<T>)
                : headers
            : [];

    return (
        <section
            className={`rounded divide-y dark:bg-background-dark ${props.className}`}
        >
            {(title || description || actionButtons) && (
                <div className="p-5! flex flex-wrap items-center justify-between">
                    <div className="space-y-1">
                        {(title || description) && (
                            <>
                                <h1 className="text-lg font-semibold">
                                    {title}
                                </h1>
                                <p className="text-sm text-current/70">
                                    {description}
                                </p>
                            </>
                        )}
                    </div>
                    {actionButtons && (
                        <div>
                            {actionButtons.map((btn, idx) => (
                                <ActionButton key={idx} {...btn} />
                            ))}
                        </div>
                    )}
                </div>
            )}
            {((pagination && pagination.totalPages > 1) || search) && (
                <div className="px-5! py-2 flex flex-wrap items-center justify-between">
                    <div className="flex-1">
                        {search &&
                            (() => {
                                const { resultsLabel, ...props } = search;
                                return (
                                    <div className="flex flex-col gap-2 w-full md:w-3/5">
                                        <InputComponent
                                            {...props}
                                            className={`mx-0 ${props.className}`}
                                        />
                                        {resultsLabel && (
                                            <span className="text-sm">
                                                {resultsLabel}
                                            </span>
                                        )}
                                    </div>
                                );
                            })()}
                    </div>
                    <div className="flex-1">
                        {pagination && (
                            <PaginationButtons
                                {...pagination}
                                className={`ml-auto ${pagination.className}`}
                            />
                        )}
                    </div>
                </div>
            )}
            {spinner && spinner.isLoading ? (
                <SpinnerComponent
                    {...spinner}
                    customize={`my-2 ${spinner.customize}`}
                />
            ) : (
                <div className="overflow-x-auto overflow-y-clip ">
                    {body.length > 0 ? (
                        <table
                            {...props}
                            className={`w-full min-w-max text-left text-sm
                                [&_th,td]:px-5 [&_th]:py-3 [&_td]:py-4
                                ${tableClassName}
                            `}
                        >
                            {!enableHeader && body.length > 0 && (
                                <thead className="bg-primary/40 text-xs uppercase dark:bg-background-dark-secondary/80 text-current/70">
                                    <tr>
                                        {cols.map((column, idx) => (
                                            <th scope="col" key={"th" + idx}>
                                                {String(
                                                    column.alias ?? column.key,
                                                )}
                                            </th>
                                        ))}
                                        {actionEvents &&
                                            actionEvents.map((action, idx) => (
                                                <th
                                                    key={"ae" + idx}
                                                    className={action.className}
                                                >
                                                    {action.title}
                                                </th>
                                            ))}
                                    </tr>
                                </thead>
                            )}
                            <tbody
                                className={`
                                divide-y divide-current/12 transition-colors
                                *:transition-colors
                            `}
                            >
                                {body.map((item, idx) => (
                                    <tr
                                        key={"tb" + idx}
                                        className="group text-current"
                                    >
                                        {cols.map((column) => {
                                            const hasLineClamp =
                                                column.className?.includes(
                                                    "line-clamp",
                                                );
                                            const value = item[column.key];
                                            const tdVal = column.renderColumn
                                                ? column.renderColumn(
                                                      value,
                                                      item as T,
                                                  )
                                                : value;
                                            return (
                                                <td
                                                    key={String(column.key)}
                                                    className={`relative overflow-hidden ${column.className}`}
                                                    {...(() => {
                                                        if (column.props) {
                                                            return column.props(
                                                                column as T,
                                                            );
                                                        }
                                                    })()}
                                                >
                                                    {hasLineClamp ? (
                                                        <>
                                                            <div
                                                                className={
                                                                    column.className
                                                                }
                                                            >
                                                                {String(tdVal)}
                                                            </div>
                                                            <div className="absolute inset-x-0 bottom-0 h-5 bg-linear-to-t from-white dark:from-transparent group-hover:from-transparent to-transparent pointer-events-none transition-colors" />
                                                        </>
                                                    ) : (
                                                        (() => {
                                                            const renderType =
                                                                column.renderType;
                                                            if (renderType) {
                                                                if (
                                                                    renderType ===
                                                                    "badge"
                                                                ) {
                                                                    return (
                                                                        <BadgeComponent
                                                                            {...(tdVal as BadgeProps)}
                                                                        />
                                                                    );
                                                                } else if (
                                                                    renderType ===
                                                                    "html"
                                                                ) {
                                                                    return tdVal as ReactNode;
                                                                }
                                                            }
                                                            return String(
                                                                tdVal,
                                                            );
                                                        })()
                                                    )}
                                                </td>
                                            );
                                        })}
                                        {actionEvents &&
                                            actionEvents.map(
                                                (action, actionIdx) => {
                                                    const clickEvents =
                                                        action.clickEvents;
                                                    return (
                                                        <td
                                                            key={
                                                                "act" +
                                                                actionIdx
                                                            }
                                                            className={`flex gap-2 items-center ${action.className}`}
                                                        >
                                                            {clickEvents &&
                                                                clickEvents.map(
                                                                    (
                                                                        ev,
                                                                        idx,
                                                                    ) => {
                                                                        return action.useText ? (
                                                                            <span
                                                                                key={
                                                                                    "clickEvent-" +
                                                                                    idx
                                                                                }
                                                                                onClick={() => {
                                                                                    ev.onClick?.(
                                                                                        item,
                                                                                    );
                                                                                }}
                                                                                className={`cursor-pointer flex flex-row items-center gap-1 ${ev.className}`}
                                                                            >
                                                                                {
                                                                                    ev.text
                                                                                }
                                                                                {ev.icon && (
                                                                                    <ev.icon
                                                                                        className={`${ev.text != undefined ? "size-4" : "size-5 mx-auto"}`}
                                                                                    />
                                                                                )}
                                                                            </span>
                                                                        ) : (
                                                                            <ActionButton
                                                                                {...ev}
                                                                                key={
                                                                                    "clickEvent-" +
                                                                                    idx
                                                                                }
                                                                                className={`p-2 rounded-lg ${ev.className}`}
                                                                                onClick={() => {
                                                                                    ev.onClick?.(
                                                                                        item,
                                                                                    );
                                                                                }}
                                                                            />
                                                                        );
                                                                    },
                                                                )}
                                                        </td>
                                                    );
                                                },
                                            )}
                                    </tr>
                                ))}
                                {body.length == 0 && (
                                    <tr>
                                        <td className="fullSpan capitalize text-center">
                                            No Data to display
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    ) : (
                        <p className="text-sm p-5!">
                            {emptyDataMessage ?? `No Data to display`}
                        </p>
                    )}
                </div>
            )}
        </section>
    );
}
