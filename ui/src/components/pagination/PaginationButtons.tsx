import { ChevronLeftIcon, ChevronRightIcon } from "@heroicons/react/24/outline";
import ActionButton from "../ActionButtonComponent";
import type { PaginationProps } from "./usePagination";

export interface PaginationButtonsProps extends Omit<
    PaginationProps<void>,
    "currentItems"
> {
    className?: string;
}

export function PaginationButtons({
    className,
    currentPage,
    totalPages,
    goToNextPage,
    goToPrevPage,
}: PaginationButtonsProps) {
    if (totalPages <= 1) {
        return;
    }

    return (
        <div className={`w-fit ${className}`}>
            <div className="rounded-md overflow-hidden w-fit flex justify-self-end outline-1 outline-offset-1 outline-primary-accent dark:outline-dark-accent m-0.5">
                <ActionButton
                    icon={ChevronLeftIcon}
                    className="primary rounded-r-none p-2 outline-none border-0"
                    customise="m-0!"
                    onClick={goToPrevPage}
                    disabled={currentPage == 1}
                />
                <ActionButton
                    icon={ChevronRightIcon}
                    className="primary rounded-l-none p-2 outline-none border-0"
                    customise="m-0!"
                    onClick={goToNextPage}
                    disabled={totalPages == currentPage}
                />
            </div>
            <span className="block lowercase text-sm">
                Showing page {currentPage} of {totalPages}
            </span>
        </div>
    );
}
