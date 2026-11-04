import { formatCurrency } from "../../utils/CurrencyFormatter";

export default function SummaryCard({
    title,
    amount,
    description,
    color,
}: {
    title: string;
    amount: number;
    description: string;
    color: string;
}) {
    return (
        <div className="bg-theme dark:bg-background-dark rounded-xl border p-5 hover:shadow-md">
            <p className="text-sm">
                {title}
            </p>

            <p className={`mt-3 text-2xl font-bold ${color}`}>
                {formatCurrency(amount)}
            </p>

            <p className="mt-2 text-xs">
                {description}
            </p>
        </div>
    );
}
