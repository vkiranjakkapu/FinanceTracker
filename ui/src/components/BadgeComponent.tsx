import {
    CheckBadgeIcon,
    ClockIcon,
    ExclamationCircleIcon,
} from "@heroicons/react/24/outline";
import type {
    ForwardRefExoticComponent,
    PropsWithoutRef,
    SVGProps,
} from "react";

export type BadgeProps = {
    value: string;
    icon?: ForwardRefExoticComponent<
        PropsWithoutRef<SVGProps<SVGSVGElement>> & {
            title?: string;
            titleId?: string;
        }
    >;
    type?: "success" | "info" | "warning" | "danger";
    customize?: string;
};

export default function BadgeComponent({
    value,
    icon: Icon,
    type,
    customize,
}: BadgeProps) {
    if (type === "success") {
        return (
            <span
                className={`capitalize inline-flex items-center gap-1.5 rounded-full bg-emerald-500/10 px-2.5 py-1 text-xs font-medium text-emerald-700 dark:text-emerald-400 transition-all duration-100 ${customize}`}
            >
                {Icon ? (
                    <Icon className="size-3.5 text-emerald-500" />
                ) : (
                    <CheckBadgeIcon className="size-3.5 text-emerald-500" />
                )}
                {value}
            </span>
        );
    }
    if (type === "info") {
        return (
            <span
                className={`capitalize inline-flex items-center gap-1.5 rounded-full bg-cyan-500/10 text-cyan-700 dark:text-cyan-400 px-2.5 py-1 text-xs font-medium ${customize}`}
            >
                {Icon ? (
                    <Icon className="size-3.5 text-cyan-500" />
                ) : (
                    <ClockIcon className="size-3.5 text-cyan-500" />
                )}
                {value}
            </span>
        );
    }
    if (type === "warning") {
        return (
            <span
                className={`capitalize inline-flex items-center gap-1.5 rounded-full bg-amber-500/10 px-2.5 py-1 text-xs font-medium text-amber-700 dark:text-amber-400 ${customize}`}
            >
                {Icon ? (
                    <Icon className="size-3.5 text-amber-500" />
                ) : (
                    <ClockIcon className="size-3.5 text-amber-500" />
                )}
                {value}
            </span>
        );
    }
    if (type === "danger") {
        return (
            <span
                className={`capitalize inline-flex items-center gap-1.5 rounded-full bg-rose-500/10 px-2.5 py-1 text-xs font-medium text-rose-700 dark:text-rose-400 ${customize}`}
            >
                {Icon ? (
                    <Icon className="size-3.5 text-rose-500" />
                ) : (
                    <ExclamationCircleIcon className="size-3.5 text-rose-500" />
                )}
                {value}
            </span>
        );
    }

    return value;
}
