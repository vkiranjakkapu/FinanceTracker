export class DateFormatter {
    // Regex matching Spring Boot standard timestamp formats (ISO-8601 or SQL format)
    private static TIMESTAMP_REGEX =
        /^\d{4}-\d{2}-\d{2}[T ]\d{2}:\d{2}:\d{2}(\.\d+)?(Z|[+-]\d{2}:\d{2})?$/;

    /**
     * Checks whether a given string matches the standard backend timestamp format
     */
    public static isTimestampFormat(value: unknown): value is string {
        return (
            typeof value === "string" && this.TIMESTAMP_REGEX.test(value.trim())
        );
    }

    /**
     * Converts a valid timestamp into a relative human-readable string (e.g., "5 mins ago", "Yesterday")
     * Fallback to formatted string if date is older than maxRelativeDays.
     */
    public static toRelativeTime(
        value: string | Date,
        maxRelativeDays: number = 7,
    ): string {
        const date =
            typeof value === "string"
                ? new Date(value.replace(" ", "T"))
                : value;

        if (isNaN(date.getTime())) {
            return String(value); // Return original string if parsing fails
        }

        const now = new Date();
        const diffInSeconds = Math.floor(
            (now.getTime() - date.getTime()) / 1000,
        );

        // Future dates or less than 5 seconds ago
        if (diffInSeconds < 5 && diffInSeconds >= -5) return "Just now";
        if (diffInSeconds < 0) return this.toFormattedDate(date); // Fallback for future dates

        const minutes = Math.floor(diffInSeconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);

        if (diffInSeconds < 60) return `${diffInSeconds}s ago`;
        if (minutes < 60) return `${minutes}m ago`;
        if (hours < 24) return `${hours}h ago`;
        if (days === 1) return "Yesterday";
        if (days < maxRelativeDays) return `${days}d ago`;

        // Default standard formatted date for older timestamps
        return this.toFormattedDate(date);
    }

    /**
     * Default: "Aug 19, 2026, 10:56 PM"
     * formatIsoDate(rawDate);
     *
     * Custom options for Date Only: "August 19, 2026"
     * formatIsoDate(rawDate, { dateStyle: "long" });
     *
     * Custom options for Relative/Short: "08/19/2026"
     * formatIsoDate(rawDate, { month: "2-digit", day: "2-digit", year: "numeric" });
     */
    public static toFormattedDate(
        isoString?: string | Date,
        options: Intl.DateTimeFormatOptions = {
            month: "short",
            day: "numeric",
            year: "numeric",
            hour: "numeric",
            minute: "2-digit",
        },
    ): string {
        if (!isoString) return "";

        const date = new Date(isoString);

        if (isNaN(date.getTime())) return "";

        return new Intl.DateTimeFormat("en-US", options).format(date);
    }

    public static toFormattedMonthYear(isoString?: string | Date): string {
        return this.toFormattedDate(isoString, {
            month: "short",
            year: "2-digit",
        }).replace(" ", "'");
    }

    /**
     * Safely formats value if it matches the timestamp regex, otherwise returns original value.
     */
    public static formatIfTimestamp(
        value: unknown,
        relative: boolean = true,
    ): unknown {
        if (this.isTimestampFormat(value)) {
            return relative
                ? this.toRelativeTime(value)
                : this.toFormattedDate(value);
        }
        return value;
    }
}
