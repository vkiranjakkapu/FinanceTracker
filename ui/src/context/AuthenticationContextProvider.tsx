import { useCallback, useEffect, useState, type ReactNode } from "react";
import type { ErrorResponse } from "../api/api";
import AuthService, { type LoginResponse } from "../services/AuthService";
import TokenStorage from "../storage/TokenStorage";
import { AuthContext, AuthStatus, type UserProfile } from "./useAuthContext";

type AuthenticationContextProviderProps = {
    children: ReactNode;
};

export default function AuthenticationContextProvider({
    children,
}: AuthenticationContextProviderProps) {
    const [userProfile, setProfile] = useState<UserProfile>({} as UserProfile);

    const [status, setStatus] = useState<AuthStatus>(() => {
        return TokenStorage.getAccessToken() !== null
            ? AuthStatus.INITIALIZING
            : AuthStatus.UNAUTHENTICATED;
    });

    const loadProfile = useCallback(() => {
        AuthService.getProfile<UserProfile>()
            .then((resp) => {
                setProfile({
                    ...resp.data,
                    name: `${resp.data.firstName} ${resp.data.lastName}`,
                });
                setStatus(AuthStatus.AUTHENTICATED);
            })
            .catch((e: ErrorResponse) => {
                setStatus(AuthStatus.UNAUTHENTICATED);
                console.log(e);
            });
    }, []);

    useEffect(() => {
        const token = TokenStorage.getAccessToken();
        if (token != null) {
            loadProfile();
        }
    }, [loadProfile]);

    async function login(
        email: string,
        password: string,
    ): Promise<LoginResponse> {
        setStatus(AuthStatus.INITIALIZING);
        try {
            const resp = await AuthService.authenticate<LoginResponse>({
                email,
                password,
            });
            TokenStorage.save(resp.data.accessToken, resp.data.refreshToken);
            loadProfile();
            return resp.data;
        } catch (e) {
            setStatus(AuthStatus.UNAUTHENTICATED);
            throw e;
        }
    }

    function logout() {
        try {
            AuthService.logout<void>({
                refreshToken: TokenStorage.getRefreshToken(),
            });
            TokenStorage.clear();
            setStatus(AuthStatus.UNAUTHENTICATED);
            setProfile({} as UserProfile);
        } catch (error) {
            console.log(error);
            window.alert((error as ErrorResponse).errorMessage);
        }
    }

    const isLoggedIn = status === AuthStatus.AUTHENTICATED;

    const isAdmin = isLoggedIn && userProfile.roles.includes("ADMIN");

    return (
        <AuthContext
            value={{
                profile: userProfile,
                updateProfile: setProfile,
                status,
                isLoggedIn,
                isAdmin,
                login,
                logout,
            }}
        >
            {children}
        </AuthContext>
    );
}
