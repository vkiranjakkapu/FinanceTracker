import {
    useEffect,
    type ForwardRefExoticComponent,
    type PropsWithoutRef,
    type SVGProps,
} from "react";
import { createPortal } from "react-dom";
import Logo from "/favicon.png";

interface LoadingPortalProps {
    isLoading: boolean;
    icon?: ForwardRefExoticComponent<
        PropsWithoutRef<SVGProps<SVGSVGElement>> & {
            title?: string;
            titleId?: string;
        }
    >;
    message?: string;
    subMessage?: string;
}

export default function LoadingPortalComponent({
    isLoading,
    icon: Icon,
    message = "Processing Request...",
    subMessage = "Please wait while we process your request.",
}: LoadingPortalProps) {
    useEffect(() => {
        if (isLoading) {
            document.body.style.overflow = "hidden";
        } else {
            document.body.style.overflow = "";
        }

        return () => {
            document.body.style.overflow = "";
        };
    }, [isLoading]);

    if (!isLoading) return null;

    return createPortal(
        <div
            className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm transition-opacity animate-in fade-in duration-200"
            aria-busy="true"
            aria-live="polite"
        >
            <div className="relative flex w-full max-w-sm flex-col items-center rounded-2xl border border-slate-200 bg-white p-6 text-center shadow-2xl transition-all dark:border-slate-800 dark:bg-slate-900">
                <div className="relative flex items-center justify-center">
                    <div className="absolute size-16 animate-ping rounded-full bg-primary-accent/50 dark:bg-primary-accent/50" />

                    <div className="size-16 animate-spin rounded-full border-4 border-slate-100 border-t-primary-accent dark:border-slate-800 dark:border-t-primary-accent" />

                    <div className="absolute flex items-center justify-center">
                        {Icon ? (
                            <Icon className="size-7 text-primary-accent dark:text-primary-accent" />
                        ) : (
                            <img
                                src={Logo}
                                alt="FinanceTracker"
                                className="size-10 rounded-lg object-cover"
                            />
                        )}
                    </div>
                </div>

                <div className="mt-5 space-y-1">
                    <h4 className="text-base font-semibold text-slate-900 dark:text-slate-100">
                        {message}
                    </h4>
                    <p className="text-xs text-slate-500 dark:text-slate-400">
                        {subMessage}
                    </p>
                </div>

                <div className="mt-5 h-1 w-full overflow-hidden rounded-full bg-slate-100 dark:bg-slate-800">
                    <div className="h-full w-1/3 animate-pulse rounded-full bg-primary-accent dark:bg-primary-accent" />
                </div>
            </div>
        </div>,
        document.body,
    );
}
