import { Route, Routes } from "react-router-dom";
import ProtectedLayout from "../layouts/ProtectedLayout";
import PublicLayout from "../layouts/PublicLayout";
import BudgetsPage from "../pages/budgets/BudgetsPage";
import CategoriesPage from "../pages/categories/CategoriesPage";
import DashboardPage from "../pages/dashboard/DashboardPage";
import LandingPage from "../pages/landing/LandingPage";
import ProfilePage from "../pages/profile/ProfilePage";
import TransactionsPage from "../pages/transactions/TransactionsPage";
import { RoutePaths } from "./RoutePaths";

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
                    path={RoutePaths.TRANSACTIONS}
                    element={<TransactionsPage />}
                />

                <Route
                    path={RoutePaths.CATEGORIES}
                    element={<CategoriesPage />}
                />

                <Route path={RoutePaths.BUDGETS} element={<BudgetsPage />} />

                <Route
                    path={RoutePaths.MANAGE}
                    element={<ProfilePage />}
                ></Route>
            </Route>
        </Routes>
    );
}
