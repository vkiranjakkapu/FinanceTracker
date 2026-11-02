import {
    CheckCircleIcon,
    ExclamationCircleIcon,
    ExclamationTriangleIcon,
    InformationCircleIcon,
} from "@heroicons/react/24/outline";
import type { ErrorResponse } from "../../api/api";

export type NoticeTheme = "info" | "success" | "warn" | "error";

export type NotificationProps = {
    type?: NoticeTheme;
    messages: string | string[] | ErrorResponse;
    hideIcon?: boolean;
    className?: string;
};

export default function Notification({
    type = "info",
    messages,
    hideIcon = false,
    className,
}: NotificationProps) {
    const normalizedMessages = (() => {
        if (typeof messages === "string") {
            return [messages];
        }
        if (Array.isArray(messages)) {
            return messages;
        }

        if (messages.validationErrors && messages.validationErrors.length > 0) {
            return messages.validationErrors.map(
                (ve) => `${ve.field} ${ve.message}`,
            );
        }
        return [messages.errorMessage];
    })();

    return (
        <>
            {normalizedMessages.length > 0 && (
                <div
                    className={`p-2.5 col-span-full flex flex-row gap-2 items-center dark:text-white text-sm rounded notification ${type} ${className ?? ""}`}
                >
                    {!hideIcon &&
                        (type == "error" ? (
                            <ExclamationCircleIcon className={`size-4`} />
                        ) : type == "warn" ? (
                            <ExclamationTriangleIcon className="size-4" />
                        ) : type == "success" ? (
                            <CheckCircleIcon className="size-4" />
                        ) : (
                            <InformationCircleIcon className="size-4" />
                        ))}
                    <span>{normalizedMessages.join(", ")}</span>
                </div>
            )}
        </>
    );
}
