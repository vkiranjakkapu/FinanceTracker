export type SpinnerComponentProps = {
    /**
     * To handle conditional display of spinner
     */
    isLoading?: boolean;

    /**
     * To show custom message after the spinner wheel.
     */
    text?: string;

    /**
     * to change the size of the wheel. Also can be used to customise other css props of the wheel.
     */
    size?: string;

    /**
     * to customize the spinner div as a whole.
     */
    customize?: string;

    /**
     * For spinner text animation. Also can be used to customize the spinner text.
     */
    animate?: string;
};

export default function SpinnerComponent({
    text,
    size,
    customize,
    animate,
}: SpinnerComponentProps) {
    return (
        <div
            className={`w-full flex items-center gap-1.5 justify-center ${customize}`}
        >
            <div
                className={`border-2 border-slate-300 border-t-primary-accent animate-spin rounded-full ${size ?? "size-4"}`}
            ></div>
            {text && (
                <span className={`capitalize ${animate}`}>
                    {text ?? "Loading..."}
                </span>
            )}
        </div>
    );
}
