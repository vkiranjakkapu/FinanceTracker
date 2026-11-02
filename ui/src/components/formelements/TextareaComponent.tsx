import type { TextareaHTMLAttributes } from "react";
import type { LabelElementProps } from "./form-commons";

export type TextareaComponentProps = Omit<
    TextareaHTMLAttributes<HTMLTextAreaElement>,
    "id"
> & {
    id: string;
    label?: LabelElementProps;
    customise?: string;
};

export default function TextareaComponent({
    id,
    label,
    customise,
    ...props
}: TextareaComponentProps) {
    return (
        <div className={`${label ? `form-group` : ``} ${customise ?? ""}`}>
            {label && (
                <label {...label} htmlFor={id} className="space-x-1">
                    {label.icon && <label.icon className="size-4" />}
                    {label.text && <span>{label.text}</span>}
                </label>
            )}
            <textarea {...props} id={id} />
        </div>
    );
}
