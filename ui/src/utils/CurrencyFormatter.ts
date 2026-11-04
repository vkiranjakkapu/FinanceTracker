const CURRENCY_FORMATTER = new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
});

export function formatCurrency(amount: number): string {
    return CURRENCY_FORMATTER.format(amount ?? 0);
}
