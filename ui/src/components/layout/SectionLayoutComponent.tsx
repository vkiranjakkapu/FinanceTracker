import type { HTMLAttributes, ReactNode } from "react";
import type { ActionButtonProps } from "../ActionButtonComponent";
import ActionButton from "../ActionButtonComponent";
import InputComponent, {
    type InputComponentProps,
} from "../formelements/InputComponent";
import {
    PaginationButtons,
    type PaginationButtonsProps,
} from "../pagination/PaginationButtons";
import type { SpinnerComponentProps } from "../SpinnerComponent";
import SpinnerComponent from "../SpinnerComponent";

export type SectionLayoutComponentProps = HTMLAttributes<HTMLDivElement> & {
    children: ReactNode;
    title?: string;
    description?: string;
    actionButtons?: ActionButtonProps[];
    pagination?: PaginationButtonsProps;
    search?: InputComponentProps;
    spinner?: SpinnerComponentProps;
};

export default function SectionLayoutComponent({
    children,
    title,
    description,
    actionButtons,
    pagination,
    search,
    spinner,
    ...props
}: SectionLayoutComponentProps) {
    return (
        <section
            {...props}
            className={`space-y-3 p-4 lg:p-5 bg-theme rounded divide-y *:not-last:pb-3 ${props.className}`}
        >
            {(title || description || actionButtons) && (
                <div className="flex flex-wrap items-center justify-between">
                    <div>
                        {(title || description) && (
                            <>
                                <h1 className="text-xl font-semibold">
                                    {title}
                                </h1>
                                <p className="text-sm capitalize">
                                    {description}
                                </p>
                            </>
                        )}
                    </div>
                    {actionButtons && (
                        <div className="flex items-center gap-3">
                            {actionButtons.map((btn, idx) => (
                                <ActionButton key={idx} {...btn} />
                            ))}
                        </div>
                    )}
                </div>
            )}
            {((pagination && pagination.totalPages > 1) || search) && (
                <div className="flex flex-wrap items-center justify-between">
                    <div className="flex flex-wrap items-center gap-2">
                        {search &&
                            (() => {
                                const { resultsLabel, ...props } = search;
                                return (
                                    <>
                                        <InputComponent {...props} />
                                        {resultsLabel && (
                                            <span className="text-sm">
                                                {resultsLabel}
                                            </span>
                                        )}
                                    </>
                                );
                            })()}
                    </div>
                    <div>
                        {pagination && <PaginationButtons {...pagination} />}
                    </div>
                </div>
            )}
            {spinner && spinner.isLoading ? (
                <SpinnerComponent {...spinner} />
            ) : (
                children || ""
            )}
        </section>
    );
}
