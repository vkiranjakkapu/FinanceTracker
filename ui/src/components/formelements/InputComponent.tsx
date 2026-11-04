import type { InputHTMLAttributes, ReactNode, Ref } from "react";
import type { LabelElementProps } from "./form-commons";

export type InputComponentProps = Omit<
    InputHTMLAttributes<HTMLInputElement>,
    "id"
> & {
    id: string;
    label?: LabelElementProps;
    customise?: string;
    children?: ReactNode;
    resultsLabel?: string;
    ref?: Ref<HTMLInputElement>;
};

export default function InputComponent({
    id,
    label,
    customise,
    children,
    ref,
    ...props
}: InputComponentProps) {
    return (
        <div className={`${label ? `form-group` : ``} ${customise ?? ""}`}>
            {label &&
                (() => {
                    return (
                        <label
                            {...label}
                            htmlFor={id}
                            className={`space-x-1 ${label.className}`}
                        >
                            {label.icon && <label.icon className="size-4" />}
                            {label.text && <span>{label.text}</span>}
                        </label>
                    );
                })()}
            <input {...props} id={id} ref={ref} />
            {children || ""}
        </div>
    );
}
