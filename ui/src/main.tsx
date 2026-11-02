import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import AuthenticationContextProvider from "./context/AuthenticationContextProvider.tsx";
import { BrowserRouter } from "react-router-dom";

createRoot(document.getElementById("root")!).render(
    <StrictMode>
        <AuthenticationContextProvider>
            <BrowserRouter>
                <App />
            </BrowserRouter>
        </AuthenticationContextProvider>
    </StrictMode>,
);
