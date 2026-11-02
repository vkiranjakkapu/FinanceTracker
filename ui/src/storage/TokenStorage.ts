import { AppConfig } from "../config/AppConfig";

class TokenStorage {
    save(access: string, refresh: string) {
        localStorage.setItem(
            AppConfig.LOCAL_AUTH_KEY,
            JSON.stringify({
                access,
                refresh,
            }),
        );
    }

    private getAuthStore() {
        const json = localStorage.getItem(AppConfig.LOCAL_AUTH_KEY);
        if (!json) {
            return null;
        }
        return JSON.parse(json);
    }

    getAccessToken() {
        const auth = this.getAuthStore();
        return auth?.access ?? null;
    }

    getRefreshToken() {
        const auth = this.getAuthStore();
        return auth?.refresh ?? null;
    }

    clear() {
        localStorage.removeItem(AppConfig.LOCAL_AUTH_KEY);
    }
}

export default new TokenStorage();
