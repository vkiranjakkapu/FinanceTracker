import { Navigate, Outlet } from "react-router-dom";
import useAuthContext, { AuthStatus } from "../context/useAuthContext";
import { RoutePaths } from "../routes/RoutePaths";
import DashboardLayout from "../components/layout/DashboardLayout";

export default function ProtectedLayout() {
    const { isLoggedIn, status } = useAuthContext();

    if (status === AuthStatus.INITIALIZING) {
        return;
    }

    if (!isLoggedIn) {
        return <Navigate to={RoutePaths.LANDING} />;
    }

    return (
        <DashboardLayout>
            <Outlet />
        </DashboardLayout>
    );
}
