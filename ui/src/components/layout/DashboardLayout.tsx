import {
    Bars3Icon,
    ComputerDesktopIcon,
    MoonIcon,
    SunIcon,
    XMarkIcon,
} from "@heroicons/react/24/outline";
import { useEffect, useState, type ReactNode } from "react";
import ActionButton from "../ActionButtonComponent";
import NavbarComponent from "./NavbarComponent";

type DashboardLayoutProps = {
    children: ReactNode;
    handleLoginClick?: () => void;
    handleRegisterClick?: () => void;
};

export default function DashboardLayout({
    children,
    handleLoginClick,
    handleRegisterClick,
}: DashboardLayoutProps) {
    const [openMenu, setOpenMenu] = useState<boolean>(false);

    const [theme, setTheme] = useState<"dark" | "light" | "system">(() => {
        const theme = localStorage.getItem("theme") as
            | "dark"
            | "light"
            | "system"
            | null;
        if (theme === null) {
            return "system";
        }
        return theme;
    });

    useEffect(() => {
        const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");

        const applyTheme = () => {
            if (theme === "system") {
                document.documentElement.classList.toggle(
                    "dark",
                    mediaQuery.matches,
                );
            } else {
                document.documentElement.classList.toggle(
                    "dark",
                    theme === "dark",
                );
            }
        };

        applyTheme();

        if (theme === "system") {
            mediaQuery.addEventListener("change", applyTheme);

            return () => {
                mediaQuery.removeEventListener("change", applyTheme);
            };
        }
    }, [theme]);

    function toggleTheme(theme: "dark" | "light" | "system") {
        localStorage.setItem("theme", theme);
        setTheme(theme);
    }

    return (
        <main
            className={`relative flex flex-col gap-3 h-screen ${openMenu ? "[&>.sidebar]:translate-x-0" : "[&>.sidebar]:-translate-x-full lg:[&>.sidebar]:translate-x-0"}`}
        >
            <nav className="bg-theme shadow-lg *:p-4 *:lg:p-5">
                <div className="section-width flex flex-wrap justify-between gap-3">
                    <div className="flex items-center gap-2 font-semibold capitalize">
                        <div className="logo flex items-center gap-2">
                            <img
                                src="/favicon.png"
                                alt="FinanceTracker"
                                className="size-12 object-cover"
                            />
                            <div className="">
                                <span className="block text-lg">Finance Tracker</span>
                                <span className="block text-xs">
                                    Start Managing your personal finances.
                                </span>
                            </div>
                        </div>
                    </div>
                    <div className="flex flex-wrap items-center ml-auto gap-4">
                        <NavbarComponent
                            className="hidden md:flex"
                            handleLoginClick={handleLoginClick}
                            handleRegisterClick={handleRegisterClick}
                        />
                        <div className="flex items-center gap-0.5">
                            <ActionButton
                                icon={theme !== "light" ? MoonIcon : SunIcon}
                                className={`p-1.5 rounded-full ${theme !== "system" && "primary"}`}
                                onClick={() => {
                                    toggleTheme(
                                        theme == "dark" ? "light" : "dark",
                                    );
                                }}
                                title={`Switch to ${theme !== "light" && theme !== "system" ? "Light" : "Dark"} Mode`}
                            />
                            <ActionButton
                                icon={ComputerDesktopIcon}
                                className={`p-1.5 rounded-full ${theme === "system" && "primary"}`}
                                onClick={() => {
                                    toggleTheme("system");
                                }}
                                title="Follow System Theme"
                            />
                        </div>
                        <ActionButton
                            icon={openMenu ? XMarkIcon : Bars3Icon}
                            className="p-1.5 rounded md:hidden"
                            onClick={() => {
                                setOpenMenu(!openMenu);
                            }}
                        />
                    </div>
                    <NavbarComponent
                        className={`w-full border-t mt-3 pt-3 md:hidden ${openMenu ? `` : `hidden`}`}
                        handleLoginClick={handleLoginClick}
                        handleRegisterClick={handleRegisterClick}
                    />
                </div>
            </nav>
            <div className="section-width">{children}</div>
        </main>
    );
}
