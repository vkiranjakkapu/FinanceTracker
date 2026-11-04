import {
    AcademicCapIcon,
    ArrowTrendingUpIcon,
    BanknotesIcon,
    Battery50Icon,
    BriefcaseIcon,
    BuildingOfficeIcon,
    CircleStackIcon,
    FilmIcon,
    GlobeAltIcon,
    GlobeAsiaAustraliaIcon,
    LifebuoyIcon,
    ListBulletIcon,
    PercentBadgeIcon,
    PlusCircleIcon,
    ShoppingCartIcon
} from "@heroicons/react/24/outline";
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

export const CategoryType = {
    Salary: "Salary",
    Business: "Business",
    Investments: "Investments",
    Freelance: "Freelance",
    OtherIncome: "Other Income",
    Food: "Food",
    Groceries: "Groceries",
    Rent: "Rent",
    Travel: "Travel",
    Utilities: "Utilities",
    Healthcare: "Healthcare",
    Shopping: "Shopping",
    Entertainment: "Entertainment",
    Education: "Education",
    OtherExpense: "Other Expense",
} as const;

export type CategoryType = (typeof CategoryType)[keyof typeof CategoryType];

export const SocialIconMap: Record<CategoryType, IconProps> = {
    [CategoryType.Salary]: BanknotesIcon,
    [CategoryType.Business]: BriefcaseIcon,
    [CategoryType.Investments]: ArrowTrendingUpIcon,
    [CategoryType.Freelance]: GlobeAsiaAustraliaIcon,
    [CategoryType.OtherIncome]: PlusCircleIcon,

    [CategoryType.Food]: Battery50Icon,
    [CategoryType.Groceries]: ShoppingCartIcon,
    [CategoryType.Rent]: BuildingOfficeIcon,
    [CategoryType.Travel]: GlobeAltIcon,
    [CategoryType.Utilities]: ListBulletIcon,
    [CategoryType.Healthcare]: LifebuoyIcon,
    [CategoryType.Shopping]: PercentBadgeIcon,
    [CategoryType.Entertainment]: FilmIcon,
    [CategoryType.Education]: AcademicCapIcon,
    [CategoryType.OtherExpense]: CircleStackIcon,
};
