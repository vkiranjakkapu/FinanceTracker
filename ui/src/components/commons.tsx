import type {
    ForwardRefExoticComponent,
    PropsWithoutRef,
    SVGProps,
} from "react";
import { DateFormatter } from "../utils/DateFormatter";

export function renderCellValue(value: unknown, dateType?: boolean) {
    if (DateFormatter.isTimestampFormat(value)) {
        return dateType
            ? DateFormatter.toFormattedDate(value)
            : DateFormatter.toRelativeTime(value);
    }

    return value instanceof Boolean ? (value ? "Yes" : "No") : String(value);
}

export type IconProps = ForwardRefExoticComponent<
    PropsWithoutRef<SVGProps<SVGSVGElement>>
>;
