import { AxiosError } from "axios";
import type { ErrorResponse } from "../api/api";

export function handleErrorResponse(er: unknown): ErrorResponse {
    const error = er as AxiosError;

    const serverData = (error.response?.data ?? {}) as Record<string, unknown>;

    const errorNameStr =
        typeof serverData.errorName === "string"
            ? serverData.errorName
            : "AXIOS_ERROR";
    const validationArr = Array.isArray(serverData.validationErrors)
        ? serverData.validationErrors
        : [];
    const timeStr =
        typeof serverData.timestamp === "string"
            ? serverData.timestamp
            : new Date().toISOString();
    const calculatedCode = String(
        serverData.status ?? serverData.errorCode ?? error.code ?? "500",
    );

    const standardizedError: ErrorResponse = {
        errorName: errorNameStr,
        errorCode: calculatedCode,
        errorMessage: "An unexpected error occurred.",
        validationErrors: validationArr,
        timestamp: timeStr,
    };

    if (
        "errorMessage" in serverData &&
        typeof serverData.errorMessage === "string"
    ) {
        standardizedError.errorMessage = serverData.errorMessage;
    } else if ("error" in serverData && typeof serverData.error === "string") {
        standardizedError.errorMessage = serverData.error;
    }

    return standardizedError;
}
