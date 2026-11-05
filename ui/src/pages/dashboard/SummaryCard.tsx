import SpinnerComponent, {
    type SpinnerComponentProps,
} from "../../components/SpinnerComponent";
import { formatCurrency } from "../../utils/CurrencyFormatter";

export default function SummaryCard({
    title,
    amount,
    description,
    spinner,
    color,
}: {
    title: string;
    amount: number;
    description: string;
    spinner?: SpinnerComponentProps;
    color?: string;
}) {
    return (
        <div className="bg-theme dark:bg-background-dark rounded-xl border p-5 hover:shadow-md">
            <p className="text-sm">{title}</p>

            <div className={`min-h-9 flex items-center mt-3 text-2xl font-bold ${color ?? ""}`}>
                {spinner && spinner.isLoading ? (
                    <SpinnerComponent
                        customize={`w-fit! ${spinner.customize}`}
                        size={`size-6 border-3! ${spinner.size}`}
                        {...spinner}
                    />
                ) : (
                    formatCurrency(amount)
                )}
            </div>

            <p className="mt-2 text-xs">{description}</p>
        </div>
    );
}
