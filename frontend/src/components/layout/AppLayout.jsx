import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const navItems = [
    { to: "/dashboard", label: "Inicio" },
    { to: "/devices", label: "Dispositivos" },
    { to: "/reports", label: "Informes" },
    { to: "/config", label: "Configuración" }
];

export default function AppLayout({ children }) {
    const navigate = useNavigate();
    const { authUser, logout } = useAuth();

    const handleLogout = () => {
        logout();
        navigate("/login", { replace: true });
    };

    return (
        <div className="app-shell">
            <aside className="sidebar">
                <div className="sidebar-brand">
                    <div className="sidebar-logo">DS</div>
                    <div>
                        <strong>DataSpring</strong>
                        <p>Panel de control</p>
                    </div>
                </div>

                <nav className="sidebar-nav">
                    {navItems.map((item) => (
                        <NavLink
                            key={item.to}
                            to={item.to}
                            className={({ isActive }) =>
                                `sidebar-link ${isActive ? "active" : ""}`
                            }
                        >
                            {item.label}
                        </NavLink>
                    ))}
                </nav>

                <div className="sidebar-footer">
                    <span>{authUser?.username || authUser?.email || "Usuario"}</span>
                    <button type="button" onClick={handleLogout}>
                        Cerrar sesión
                    </button>
                </div>
            </aside>

            <div className="main-panel">
                <header className="app-topbar">
                    <strong>DataSpring</strong>
                    <span className="topbar-user">
                        {authUser?.username || authUser?.email || "Usuario"}
                    </span>
                </header>

                <main className="app-content">{children}</main>
            </div>
        </div>
    );
}