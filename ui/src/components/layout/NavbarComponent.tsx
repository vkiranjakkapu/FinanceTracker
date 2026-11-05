import {
    ArrowLeftStartOnRectangleIcon,
    BanknotesIcon,
    ChartBarIcon,
    CircleStackIcon,
    SwatchIcon,
    UserIcon,
    UserPlusIcon,
} from "@heroicons/react/24/outline";
import { useLocation, useNavigate } from "react-router-dom";
import useAuthContext, { RoleType } from "../../context/useAuthContext";
import { RoutePaths } from "../../routes/RoutePaths";
import ActionButton from "../ActionButtonComponent";
import type { IconProps } from "../commons";

type NavbarComponentProps = {
    className?: string;
    handleLoginClick?: () => void;
    handleRegisterClick?: () => void;
};

export default function NavbarComponent({
    className,
    handleLoginClick,
    handleRegisterClick,
}: NavbarComponentProps) {
    const { profile: user, isLoggedIn, logout } = useAuthContext();

    const navigate = useNavigate();
    const location = useLocation();

    const navItems: {
        page: string;
        uri: string;
        icon: IconProps;
        variants?: string[];
        roles?: RoleType[];
    }[] = [
        {
            page: "My Finance",
            uri: RoutePaths.DASHBOARD,
            icon: ChartBarIcon,
        },
        {
            page: "Transactions",
            uri: RoutePaths.TRANSACTIONS,
            icon: BanknotesIcon,
        },
        {
            page: "Categories",
            uri: RoutePaths.CATEGORIES,
            icon: SwatchIcon,
        },
        {
            page: "Budgets",
            uri: RoutePaths.BUDGETS,
            icon: CircleStackIcon,
        },
        {
            page: user.name ?? "Profile",
            uri: RoutePaths.MANAGE,
            icon: UserIcon,
        },
    ];

    return (
        <nav
            className={`flex flex-row justify-center flex-wrap gap-3 ${className}`}
        >
            {isLoggedIn ? (
                <>
                    {navItems.map((item, idx) => {
                        if (item.roles) {
                            if (!item.roles.includes(user.roles[0])) {
                                return null;
                            }
                        }

                        const isCurrentPath =
                            location.pathname === item.uri ||
                            location.pathname.startsWith(`${item.uri}/`);

                        const hasMatchingVariant = item.variants?.some((vr) => {
                            const regexPattern =
                                "^" + vr.replace(/:\w+/g, "[^/]+") + "$";
                            return new RegExp(regexPattern).test(
                                location.pathname,
                            );
                        });

                        const active = isCurrentPath || hasMatchingVariant;

                        return (
                            <ActionButton
                                key={idx}
                                icon={item.icon}
                                text={item.page}
                                onClick={() => navigate(item.uri)}
                                className={`rounded-full text-sm ${active ? `primary` : ``}`}
                            />
                        );
                    })}
                    <ActionButton
                        icon={ArrowLeftStartOnRectangleIcon}
                        onClick={logout}
                        className={`p-1.5 rounded-full theme-danger`}
                        title="Click to Logout"
                    />
                </>
            ) : (
                <>
                    <ActionButton
                        icon={UserPlusIcon}
                        text={"Login"}
                        onClick={handleLoginClick}
                        className={`rounded-full text-sm`}
                    />
                    <ActionButton
                        icon={UserPlusIcon}
                        text={"Register"}
                        onClick={handleRegisterClick}
                        className={`rounded-full text-sm primary`}
                    />
                </>
            )}
        </nav>
    );
}
