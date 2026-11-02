import { Navigate, Outlet } from "react-router-dom";
import useAuthContext from "../context/useAuthContext";
import { RoutePaths } from "../routes/RoutePaths";

export default function PublicLayout() {
    const { isLoggedIn } = useAuthContext();

    if (isLoggedIn) {
        return <Navigate to={RoutePaths.DASHBOARD} />;
    }

    return <Outlet />;
}
