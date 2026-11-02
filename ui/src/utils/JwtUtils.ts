import { jwtDecode } from "jwt-decode";

type JwtPayload = {
    uid: number;
    name: string;
    sub: string;
    roles: string[];
    exp: number;
};

export function decodedToken(token: string): JwtPayload {
    return jwtDecode<JwtPayload>(token);
}
