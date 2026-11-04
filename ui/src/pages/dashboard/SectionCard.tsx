export default function SectionCard({
    title,
    children,
}: {
    title: string;
    children: React.ReactNode;
}) {
    return (
        <section className="bg-theme dark:bg-background-dark min-w-0 rounded-xl border p-5 hover:shadow-md">
            <h2 className="mb-5 font-semibold">{title}</h2>
            {children}
        </section>
    );
}
