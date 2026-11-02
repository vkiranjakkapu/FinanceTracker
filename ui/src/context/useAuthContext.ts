import {
    createContext,
    useContext,
    type Dispatch,
    type SetStateAction,
} from "react";
import type { LoginResponse } from "../services/AuthService";

export type UserProfile = {
    id: string;
    email: string;
    name: string;
    gender: UserGender;
    firstName: string;
    lastName: string;
    vehicleNumber: string;
    phone: string;
    address: Address;
    dob: string;
    roles: RoleType[];
};

export const UserGender = {
    MALE: "MALE",
    FEMALE: "FEMALE",
    NON_DISCLOSED: "NON_DISCLOSED",
} as const;
export type UserGender = (typeof UserGender)[keyof typeof UserGender];

export const RoleType = {
    ADMIN: "ADMIN",
    USER: "USER",
} as const;
export type RoleType = (typeof RoleType)[keyof typeof RoleType];

export type Address = {
    id: number;
    street: string;
    city: string;
    pinCode: string;
    state: string;
    country: string;
    deleted: boolean;
};

export const AuthStatus = {
    INITIALIZING: "INITIALIZING",
    AUTHENTICATED: "AUTHENTICATED",
    UNAUTHENTICATED: "UNAUTHENTICATED",
} as const;
export type AuthStatus = (typeof AuthStatus)[keyof typeof AuthStatus];

type AuthContextHolder = {
    profile: UserProfile;
    status: AuthStatus;
    isLoggedIn: boolean;
    isAdmin: boolean;
    updateProfile: Dispatch<SetStateAction<UserProfile>>;
    login: (email: string, password: string) => Promise<LoginResponse>;
    logout: () => void;
};

export const AuthContext = createContext<AuthContextHolder | null>(null);

export default function useAuthContext() {
    const context = useContext(AuthContext);

    if (context == null) throw Error("Authentication Context was null.");

    return context;
}
