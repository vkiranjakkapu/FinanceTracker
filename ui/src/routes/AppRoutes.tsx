import { Route, Routes } from "react-router-dom";
import ProtectedLayout from "../layouts/ProtectedLayout";
import PublicLayout from "../layouts/PublicLayout";
import DashboardPage from "../pages/dashboard/DashboardPage";
import LandingPage from "../pages/landing/LandingPage";
import { RoutePaths } from "./RoutePaths";
import ProfilePage from "../pages/profile/ProfilePage";

export default function AppRoutes() {
    return (
        <Routes>
            <Route element={<PublicLayout />}>
                <Route path={RoutePaths.LANDING} element={<LandingPage />} />
            </Route>
            <Route element={<ProtectedLayout />}>
                <Route
                    path={RoutePaths.DASHBOARD}
                    element={<DashboardPage />}
                />
                <Route
                    path={RoutePaths.MANAGE}
                    element={<ProfilePage />}
                ></Route>
            </Route>
        </Routes>
    );
}
