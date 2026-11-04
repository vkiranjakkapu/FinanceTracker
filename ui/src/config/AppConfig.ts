export const AppConfig = {
    API_BASE_URL: import.meta.env.VITE_API_BASE_URL,
    IDENTITY_AUTH_URL: "/identity/api/v1/auth",
    IDENTITY_PROFILE_URL: "/identity/api/v1/users",

    FINANCE_SERVICE_URL: "/finance/api/v1",

    LOCAL_AUTH_KEY: "finance",
    PUBLIC_ENDPOINTS: ["/auth", "/register"],
} as const;
