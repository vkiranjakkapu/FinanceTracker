import type { HTMLAttributes } from "react";
import type { IconProps } from "./commons";

export type DividerComponentProps = HTMLAttributes<HTMLDivElement> & {
    text: string;
    icon?: IconProps;
    iconAfter?: boolean;
    pingAnimation?: boolean;
};

export default function DividerComponent({
    text,
    icon: Icon,
    iconAfter = false,
    pingAnimation = false,
    ...props
}: DividerComponentProps) {
    return (
        <div
            {...props}
            className={`uppercase text-xs font-semibold flex items-center gap-1.5 tracking-wider opacity-70 ${props.className}`}
        >
            {pingAnimation && (
                <div className="relative size-2">
                    <div className="absolute inset-0 bg-primary-accent dark:bg-dark-accent rounded-full animate-ping"></div>
                    <div className="size-full bg-primary-accent dark:bg-dark-accent rounded-full"></div>
                </div>
            )}
            {!iconAfter && Icon && <Icon className="size-4" />}
            <span>{text}</span>
            {iconAfter && Icon && <Icon className="size-4" />}
        </div>
    );
}
