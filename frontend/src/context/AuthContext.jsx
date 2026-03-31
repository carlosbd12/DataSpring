import { createContext, useContext, useMemo, useState } from "react";

const AuthContext = createContext(null);

function mapRoleLabel(role) {
    switch (role) {
        case "ADMIN_PLATAFORMA":
            return "Administrador de la plataforma";
        case "GESTOR_EDIFICIO":
            return "Gestor de planta";
        case "RESPONSABLE_PLANTA":
            return "Responsable energético de planta";
        default:
            return role || "Sin rol";
    }
}

function loadSavedUser() {
    try {
        const saved = localStorage.getItem("authUser");
        if (!saved) {
            return null;
        }

        return JSON.parse(saved);
    } catch {
        localStorage.removeItem("authUser");
        return null;
    }
}

export function AuthProvider({ children }) {
    const [authUser, setAuthUser] = useState(() => loadSavedUser());

    const login = (userData) => {
        const normalizedUser = {
            ...userData,
            displayName: userData?.nombre || userData?.username || userData?.email || "Usuario",
            roleLabel: mapRoleLabel(userData?.role)
        };

        setAuthUser(normalizedUser);
        localStorage.setItem("authUser", JSON.stringify(normalizedUser));
    };

    const logout = () => {
        setAuthUser(null);
        localStorage.removeItem("authUser");
    };

    const value = useMemo(
        () => ({
            authUser,
            login,
            logout,
            isAuthenticated: !!authUser
        }),
        [authUser]
    );

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
    return useContext(AuthContext) || {
        authUser: null,
        login: () => {},
        logout: () => {},
        isAuthenticated: false
    };
}