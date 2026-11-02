import { useMemo, useState } from "react";

export interface PaginationProps<T> {
    currentPage: number;
    totalPages: number;
    currentItems: T[];
    goToNextPage: () => void;
    goToPrevPage: () => void;
    changePage: (pageNumber: number) => void;
}

export default function usePagination<T>(
    items: T[],
    itemsPerPage: number = 10,
): PaginationProps<T> {
    const [rawPage, setRawPage] = useState<number>(1);

    // Compute total pages on the fly
    const totalPages = Math.ceil(items.length / itemsPerPage) || 1;

    // Clamp the page index strictly within valid boundaries
    const currentPage = Math.min(Math.max(1, rawPage), totalPages);

    // Slice the data window safely
    const currentItems = useMemo(() => {
        const indexOfLastItem = currentPage * itemsPerPage;
        const indexOfFirstItem = indexOfLastItem - itemsPerPage;
        return items.slice(indexOfFirstItem, indexOfLastItem);
    }, [currentPage, items, itemsPerPage]);

    const goToNextPage = () =>
        setRawPage((prev) => Math.min(prev + 1, totalPages));
    const goToPrevPage = () => setRawPage((prev) => Math.max(prev - 1, 1));
    const changePage = (pageNumber: number) => setRawPage(pageNumber);

    return {
        currentPage,
        totalPages,
        currentItems,
        goToNextPage,
        goToPrevPage,
        changePage,
    };
}