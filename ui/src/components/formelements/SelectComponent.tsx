import type { SelectHTMLAttributes } from "react";
import type { LabelElementProps } from "./form-commons";

export type SelectComponentProps = Omit<
    SelectHTMLAttributes<HTMLSelectElement>,
    "id"
> & {
    id: string;
    label?: LabelElementProps;

    options: { value: string; text?: string }[];
    emptyOption?: string;

    customise?: string;
};

export default function SelectComponent({
    id,
    label,
    options,
    emptyOption,
    customise,
    ...props
}: SelectComponentProps) {
    return (
        <div className={`${label ? `form-group` : ``} ${customise ?? ""}`}>
            {label && (
                <label {...label} htmlFor={id} className="space-x-1">
                    {label.icon && <label.icon className="size-4" />}
                    {label.text && <span>{label.text}</span>}
                </label>
            )}
            <select {...props} id={id}>
                <option value="">{emptyOption}</option>
                {options.map((op, id) => (
                    <option key={id} value={op.value}>
                        {op.text ?? op.value}
                    </option>
                ))}
            </select>
        </div>
    );
}
