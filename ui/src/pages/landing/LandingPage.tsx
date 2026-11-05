import {
    ArrowRightIcon,
    BanknotesIcon,
    ChartBarIcon,
    CheckIcon,
    CircleStackIcon,
    EnvelopeIcon,
    KeyIcon,
    LockClosedIcon,
    ShieldCheckIcon,
    SwatchIcon,
    UserIcon,
    UserPlusIcon,
    WalletIcon,
} from "@heroicons/react/24/outline";
import { useState, type SubmitEvent } from "react";

import type { ErrorResponse } from "../../api/api";
import ActionButton from "../../components/ActionButtonComponent";
import InputComponent from "../../components/formelements/InputComponent";
import LoadingPortalComponent from "../../components/LoadingPortalComponent";
import ModalComponent from "../../components/ModalComponent";
import Notification from "../../components/notifications/Notification";
import { useNotifications } from "../../components/notifications/useNotifications";
import useAuthContext, {
    AuthStatus,
    type UserProfile,
} from "../../context/useAuthContext";

import DashboardLayout from "../../components/layout/DashboardLayout";
import UserService from "../../services/UserService";

const features = [
    {
        icon: BanknotesIcon,
        title: "Transaction Management",
        description:
            "Record, organize, and manage your income and expenses with complete transaction history.",
    },
    {
        icon: SwatchIcon,
        title: "Smart Categorization",
        description:
            "Organize financial activities into categories and understand where your money goes.",
    },
    {
        icon: ChartBarIcon,
        title: "Financial Insights",
        description:
            "Explore spending patterns, monthly trends, and income versus expenses through visual charts.",
    },
    {
        icon: CircleStackIcon,
        title: "Budget Planning",
        description:
            "Set monthly budgets, monitor spending, and keep track of your remaining balance.",
    },
];

const steps = [
    {
        number: "01",
        title: "Record",
        description: "Add your income and expenses as they happen.",
    },
    {
        number: "02",
        title: "Organize",
        description: "Categorize transactions for better visibility.",
    },
    {
        number: "03",
        title: "Analyze",
        description: "Understand your financial habits through insights.",
    },
    {
        number: "04",
        title: "Improve",
        description: "Manage budgets and make informed decisions.",
    },
];

export default function LandingPage() {
    const { status, login } = useAuthContext();

    const { notifications, setNotifications } = useNotifications(["login"]);
    const modalNotifications = notifications["login"] ?? null;

    const [loginModal, setLoginModal] = useState<"login" | "register" | null>(
        null,
    );

    const [isLoading, setIsLoading] = useState<boolean>(false);

    function authenticateUser(e: SubmitEvent<HTMLFormElement>) {
        e.preventDefault();
        setIsLoading(true);

        const formData = new FormData(e.currentTarget);

        login(String(formData.get("email")), String(formData.get("password")))
            .catch((e: ErrorResponse) => {
                if (e.errorCode === "500") {
                    setNotifications("login", {
                        type: "error",
                        messages: e,
                    });
                    return;
                }

                setNotifications("login", {
                    type: "error",
                    messages:
                        e.validationErrors.length > 0
                            ? e.validationErrors.map(
                                  (ve) => `${ve.field} ${ve.message}`,
                              )
                            : [e.errorMessage],
                });
            })
            .finally(() => {
                setIsLoading(false);
            });
    }

    function registerUser(e: SubmitEvent<HTMLFormElement>) {
        e.preventDefault();

        const formData = new FormData(e.currentTarget);

        setIsLoading(true);

        UserService.register<UserProfile>(formData)
            .then(() => {
                setLoginModal(null);

                login(
                    String(formData.get("email")),
                    String(formData.get("password")),
                );
            })
            .catch((e: ErrorResponse) => {
                if (e.errorCode === "500") {
                    setNotifications("login", {
                        type: "error",
                        messages: e,
                    });
                    return;
                }

                setNotifications("login", {
                    type: "error",
                    messages: e,
                });
            })
            .finally(() => {
                setIsLoading(false);
            });
    }

    return (
        <>
            <DashboardLayout
                handleLoginClick={() => setLoginModal("login")}
                handleRegisterClick={() => setLoginModal("register")}
            >
                <main className="space-y-10 pb-12 mx-4">
                    {/* Hero */}
                    <section className="relative grid items-center gap-10 py-12 md:grid-cols-2 md:py-20">
                        <div className="space-y-6">
                            <span className="inline-flex items-center gap-2 rounded-full border border-primary-accent/20 bg-primary/40 px-3 py-1.5 text-xs font-semibold uppercase tracking-wider dark:border-dark-accent/20 dark:bg-dark-accent/10">
                                <span className="h-2 w-2 rounded-full bg-emerald-500" />
                                Personal Finance Tracker
                            </span>

                            <h1 className="max-w-xl text-4xl font-bold leading-tight tracking-tight sm:text-5xl lg:text-6xl">
                                Take control of your{" "}
                                <span className="text-emerald-700 dark:text-dark-accent">
                                    financial future.
                                </span>
                            </h1>

                            <p className="max-w-xl text-base leading-8 opacity-70 sm:text-lg">
                                A simpler way to manage your money. Track
                                transactions, understand spending habits, and
                                plan your monthly budgets — all in one place.
                            </p>

                            <div className="flex flex-wrap gap-3 pt-2">
                                <ActionButton
                                    icon={ArrowRightIcon}
                                    text="Get statrted"
                                    className="primary px-5 py-2 rounded-full"
                                    onClick={() => setLoginModal("register")}
                                    iconAfter
                                />
                                <ActionButton
                                    icon={LockClosedIcon}
                                    text="Sign in"
                                    className="px-5 py-2 rounded-full"
                                    onClick={() => setLoginModal("login")}
                                />
                            </div>

                            <div className="flex flex-wrap gap-x-5 gap-y-3 pt-4 text-sm opacity-70">
                                <span className="flex items-center gap-2">
                                    <ShieldCheckIcon className="size-5 text-emerald-600 dark:text-dark-accent" />
                                    Secure access
                                </span>
                                <span className="flex items-center gap-2">
                                    <ChartBarIcon className="size-5 text-emerald-600 dark:text-dark-accent" />
                                    Visual insights
                                </span>
                                <span className="flex items-center gap-2">
                                    <WalletIcon className="size-5 text-emerald-600 dark:text-dark-accent" />
                                    Budget planning
                                </span>
                            </div>
                        </div>

                        {/* Dashboard preview */}
                        <div className="relative min-w-0">
                            <div className="absolute inset-5 rounded-full bg-primary/50 blur-3xl dark:bg-dark-accent/10" />

                            <div className="relative space-y-5 rounded-2xl border bg-theme p-5 shadow-xl sm:p-7">
                                <div className="flex items-center justify-between gap-3">
                                    <div>
                                        <p className="text-sm opacity-60">
                                            Financial overview
                                        </p>
                                        <h2 className="mt-1 text-xl font-bold">
                                            Your finances at a glance
                                        </h2>
                                    </div>

                                    <span className="rounded-full bg-primary/50 px-3 py-1 text-xs dark:bg-dark-accent/20">
                                        Overview
                                    </span>
                                </div>

                                <div className="grid grid-cols-2 gap-3">
                                    <div className="rounded-xl border bg-primary/20 p-4 dark:bg-dark-accent/10">
                                        <BanknotesIcon className="size-6 text-emerald-700 dark:text-dark-accent" />
                                        <p className="mt-3 text-sm opacity-60">
                                            Income
                                        </p>
                                        <p className="mt-1 font-semibold">
                                            Track earnings
                                        </p>
                                    </div>

                                    <div className="rounded-xl border bg-primary/20 p-4 dark:bg-dark-accent/10">
                                        <WalletIcon className="size-6 text-danger" />
                                        <p className="mt-3 text-sm opacity-60">
                                            Expenses
                                        </p>
                                        <p className="mt-1 font-semibold">
                                            Monitor spending
                                        </p>
                                    </div>
                                </div>

                                <div className="rounded-xl border p-4">
                                    <div className="flex items-center justify-between">
                                        <h3 className="font-semibold">
                                            Monthly trends
                                        </h3>
                                        <ChartBarIcon className="size-5 opacity-60" />
                                    </div>

                                    <div className="mt-6 flex h-36 items-end gap-3">
                                        {[38, 55, 44, 72, 50, 88, 65].map(
                                            (height, index) => (
                                                <div
                                                    key={index}
                                                    className="flex h-full flex-1 items-end"
                                                >
                                                    <div
                                                        className={`w-full rounded-t-md ${
                                                            index === 5
                                                                ? "bg-dark-primary dark:bg-dark-primary"
                                                                : "bg-primary dark:bg-dark-accent/40"
                                                        }`}
                                                        style={{
                                                            height: `${height}%`,
                                                        }}
                                                    />
                                                </div>
                                            ),
                                        )}
                                    </div>

                                    <p className="mt-3 text-center text-xs opacity-50">
                                        Illustrative financial chart
                                    </p>
                                </div>

                                <div className="rounded-xl border bg-primary/20 p-4 dark:bg-dark-accent/10">
                                    <div className="flex items-center justify-between gap-2">
                                        <span className="text-sm font-semibold">
                                            Monthly budget
                                        </span>
                                        <span className="text-xs opacity-60">
                                            Track utilization
                                        </span>
                                    </div>

                                    <div className="mt-3 h-2 overflow-hidden rounded-full bg-primary dark:bg-dark-accent/20">
                                        <div className="h-full w-3/5 rounded-full bg-dark-primary dark:bg-dark-primary" />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>

                    {/* Features */}
                    <section id="features" className="space-y-8">
                        <div className="mx-auto max-w-2xl space-y-3 text-center">
                            <p className="text-sm font-semibold uppercase tracking-widest opacity-60">
                                Everything in one place
                            </p>

                            <h2 className="text-3xl font-bold sm:text-4xl">
                                Make your money easier to manage.
                            </h2>

                            <p className="leading-7 opacity-65">
                                Get the clarity you need to make better everyday
                                financial decisions.
                            </p>
                        </div>

                        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
                            {features.map((feature) => {
                                const Icon = feature.icon;

                                return (
                                    <article
                                        key={feature.title}
                                        className="rounded-xl border bg-theme p-5 transition hover:-translate-y-1 hover:shadow-lg"
                                    >
                                        <div className="flex size-12 items-center justify-center rounded-xl bg-primary/40 dark:bg-dark-accent/10">
                                            <Icon className="size-6" />
                                        </div>

                                        <h3 className="mt-5 font-semibold">
                                            {feature.title}
                                        </h3>

                                        <p className="mt-3 text-sm leading-6 opacity-65">
                                            {feature.description}
                                        </p>
                                    </article>
                                );
                            })}
                        </div>
                    </section>

                    {/* How it works */}
                    <section className="space-y-8">
                        <div className="mx-auto max-w-2xl space-y-3 text-center">
                            <p className="text-sm font-semibold uppercase tracking-widest opacity-60">
                                A simple process
                            </p>

                            <h2 className="text-3xl font-bold sm:text-4xl">
                                From tracking to financial clarity.
                            </h2>
                        </div>

                        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
                            {steps.map((step) => (
                                <div
                                    key={step.number}
                                    className="rounded-xl border bg-theme p-5"
                                >
                                    <span className="text-3xl font-bold opacity-30">
                                        {step.number}
                                    </span>

                                    <h3 className="mt-4 font-semibold">
                                        {step.title}
                                    </h3>

                                    <p className="mt-2 text-sm leading-6 opacity-65">
                                        {step.description}
                                    </p>
                                </div>
                            ))}
                        </div>
                    </section>

                    {/* Call to action */}
                    <section className="rounded-2xl border bg-primary/30 px-6 py-12 text-center dark:bg-dark-accent/10 sm:px-12">
                        <div className="mx-auto max-w-2xl space-y-5">
                            <div className="mx-auto flex size-14 items-center justify-center rounded-2xl bg-theme">
                                <WalletIcon className="size-7" />
                            </div>

                            <h2 className="text-3xl font-bold sm:text-4xl">
                                Start building better financial habits.
                            </h2>

                            <p className="leading-7 opacity-70">
                                Take the first step towards understanding your
                                money and managing it with confidence.
                            </p>

                            <ActionButton
                                icon={ArrowRightIcon}
                                text="Create your account"
                                className="mx-auto primary px-5 py-2 rounded-full"
                                onClick={() => setLoginModal("register")}
                                iconAfter
                            />
                        </div>
                    </section>

                    {/* Footer */}
                    <footer className="flex flex-col items-center justify-between gap-3 border-t pt-6 text-center text-sm opacity-60 sm:flex-row sm:text-left">
                        <span>
                            © {new Date().getFullYear()} Finance Tracker
                        </span>
                        <span>Manage your money. Make informed decisions.</span>
                    </footer>
                </main>
            </DashboardLayout>

            {/* Existing authentication modal */}
            <ModalComponent
                title={loginModal + ""}
                isOpen={loginModal != null}
                onClose={() => setLoginModal(null)}
                icon={loginModal === "login" ? LockClosedIcon : UserPlusIcon}
            >
                <form
                    onSubmit={(e) => {
                        if (loginModal === "login") {
                            authenticateUser(e);
                        } else {
                            registerUser(e);
                        }
                    }}
                    className="space-y-3 p-1"
                >
                    {modalNotifications && (
                        <Notification {...modalNotifications} />
                    )}

                    {loginModal === "register" ? (
                        <>
                            <InputComponent
                                type="email"
                                label={{ icon: EnvelopeIcon }}
                                placeholder="Email"
                                name="email"
                                id="email"
                                required
                            />

                            <InputComponent
                                placeholder="First Name"
                                label={{ icon: UserIcon }}
                                name="firstName"
                                id="firstName"
                                required
                            />

                            <InputComponent
                                placeholder="Last Name"
                                label={{ icon: UserIcon }}
                                name="lastName"
                                id="lastName"
                                required
                            />

                            <hr className="mx-1 border-t" />

                            <InputComponent
                                type="password"
                                label={{ icon: KeyIcon }}
                                placeholder="Create Password"
                                name="password"
                                id="password"
                                required
                            />

                            <InputComponent
                                type="password"
                                label={{ icon: CheckIcon }}
                                placeholder="Confirm Password"
                                name="confirmPassword"
                                id="confirmPassword"
                                required
                            />
                        </>
                    ) : (
                        <>
                            <InputComponent
                                type="email"
                                placeholder="Email"
                                name="email"
                                id="email"
                                required
                            />

                            <InputComponent
                                type="password"
                                placeholder="Password"
                                name="password"
                                id="password"
                                required
                            />
                        </>
                    )}

                    <ActionButton
                        type="submit"
                        className="primary w-full justify-center rounded-full capitalize"
                        text={loginModal + ""}
                        spinner={{ isLoading }}
                        icon={loginModal === "login" ? KeyIcon : UserPlusIcon}
                        disabled={isLoading}
                    />

                    <p className="my-3 text-center text-xs opacity-60">OR</p>

                    <ActionButton
                        type="button"
                        className="w-full justify-center rounded-full capitalize"
                        icon={
                            loginModal === "login"
                                ? UserPlusIcon
                                : LockClosedIcon
                        }
                        text={loginModal === "login" ? "Register" : "Login"}
                        onClick={() => {
                            setLoginModal(
                                loginModal === "login" ? "register" : "login",
                            );
                        }}
                    />
                </form>
            </ModalComponent>

            <LoadingPortalComponent
                isLoading={status === AuthStatus.INITIALIZING}
                message="Preparing your session."
            />
        </>
    );
}
