export const AppConfig = {
    API_BASE_URL: import.meta.env.VITE_API_BASE_URL,
    IDENTITY_AUTH_URL: "/identity/api/v1/auth",
    IDENTITY_PROFILE_URL: "/identity/api/v1/users",
    BOOKINGS_SERVICE_URL: "/bookings/api/v1",
    BOOKING_RIDES_URL: "/bookings/api/v1/rides",

    BOOKINGS_WS_URL: "/bookings/api/v1/ws",

    LOCAL_AUTH_KEY: "finance",
    PUBLIC_ENDPOINTS: ["/auth", "/register"],
} as const;
