import { XMarkIcon } from "@heroicons/react/24/outline";
import React, { useEffect } from "react";
import { createPortal } from "react-dom";
import ActionButton from "./ActionButtonComponent";
import type { IconProps } from "./commons";

interface ModalProps {
    isOpen: boolean;
    onClose: () => void;
    title: string;
    icon?: IconProps;
    children: React.ReactNode;
    maxWidthClass?:
        | "max-w-md"
        | "max-w-lg"
        | "max-w-xl"
        | "max-w-2xl"
        | "max-w-4xl"
        | "max-w-6xl";
}

export default function ModalComponent({
    isOpen,
    onClose,
    title,
    icon: Icon,
    children,
    maxWidthClass = "max-w-lg",
}: ModalProps) {
    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            if (e.key === "Escape") onClose();
        };

        if (isOpen) {
            window.addEventListener("keydown", handleKeyDown);
            document.body.style.overflow = "hidden";
        }

        return () => {
            window.removeEventListener("keydown", handleKeyDown);
            document.body.style.overflow = "unset";
        };
    }, [isOpen, onClose]);

    if (!isOpen) return null;

    const modal = (
        <div
            className="fixed inset-0 z-49 flex items-center justify-center p-4 overflow-y-auto"
            role="dialog"
            aria-modal="true"
        >
            <div
                className="fixed inset-0 bg-background-dark/60 backdrop-blur-sm transition-opacity"
                onClick={onClose}
            />

            {/* Modal Body Container */}
            <div
                className={`relative w-full ${maxWidthClass} transform rounded-xl bg-white dark:bg-background-dark/60 border  p-6 text-left shadow-2xl transition-all duration-200 ease-out`}
            >
                {/* Header Block */}
                <div className="flex items-center justify-between pb-4 mb-4 border-b">
                    <h3 className="flex gap-1 items-center justify-start font-semibold text-current capitalize">
                        {Icon && <Icon className="size-4" />}
                        <span>{title}</span>
                    </h3>
                    <ActionButton
                        onClick={onClose}
                        icon={XMarkIcon}
                        aria-label="Close modal"
                        className="p-1 shadow-none"
                    />
                </div>

                <div className="max-h-[70vh] overflow-y-auto">{children}</div>
            </div>
        </div>
    );
    return <>{createPortal(modal, document.body)}</>;
}
