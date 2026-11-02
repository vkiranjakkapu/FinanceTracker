import type { LabelHTMLAttributes } from "react";
import type { IconProps } from "../commons";

export type LabelElementProps = LabelHTMLAttributes<HTMLLabelElement> & {
    icon?: IconProps;
    text?: string;
};
