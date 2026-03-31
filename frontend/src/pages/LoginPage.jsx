import { useState } from "react";
import { useNavigate } from "react-router-dom";
import client from "../api/client";
import { useAuth } from "../context/AuthContext";
import logo from "../styles/logo.png";

export default function LoginPage() {
    const { login } = useAuth();
    const navigate = useNavigate();

    const [form, setForm] = useState({
        usernameOrEmail: "",
        password: ""
    });
    const [result, setResult] = useState(null);
    const [error, setError] = useState("");

    const handleChange = (e) => {
        setForm((prev) => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setResult(null);

        try {
            const response = await client.post("/auth/login", form);
            const data = response.data;

            setResult(data);

            if (data?.authenticated) {
                login(data);
                navigate("/dashboard", { replace: true });
            } else {
                setError(data?.message || "Credenciales incorrectas");
            }
        } catch (err) {
            setError(
                err?.response?.data?.detail ||
                err?.response?.data?.message ||
                err?.message ||
                "No se pudo iniciar sesión"
            );
        }
    };

    return (
        <div className="login-page-classic">
            <div className="login-shell-classic">
                <div className="login-visual-classic">
                    <div className="login-logo-frame">
                        <img src={logo} alt="DataSense" className="login-logo-classic" />
                    </div>
                    <h1>DataSense</h1>
                    <p>Plataforma de control y análisis energético</p>
                </div>

                <div className="login-card-classic">
                    <div className="login-header-classic">
                        <h2>Iniciar sesión</h2>
                        <p>Accede con tu usuario o correo y tu contraseña.</p>
                    </div>

                    <form className="login-form-classic" onSubmit={handleSubmit}>
                        <div className="login-field-classic">
                            <label htmlFor="usernameOrEmail">Usuario o email</label>
                            <input
                                id="usernameOrEmail"
                                name="usernameOrEmail"
                                placeholder="usuario@ejemplo.com"
                                value={form.usernameOrEmail}
                                onChange={handleChange}
                                autoComplete="username"
                            />
                        </div>

                        <div className="login-field-classic">
                            <label htmlFor="password">Contraseña</label>
                            <input
                                id="password"
                                name="password"
                                type="password"
                                placeholder="Tu contraseña"
                                value={form.password}
                                onChange={handleChange}
                                autoComplete="current-password"
                            />
                        </div>

                        <button type="submit" className="login-button-classic">
                            Entrar
                        </button>
                    </form>

                    {error && <div className="login-alert-classic login-alert-error-classic">{error}</div>}

                    {result && (
                        <div className="login-result-classic">
                            <p>
                                <strong>Estado:</strong>{" "}
                                {result.authenticated ? "Autenticado" : "No autenticado"}
                            </p>
                            <p>
                                <strong>Mensaje:</strong> {result.message}
                            </p>

                            {result.authenticated && (
                                <>
                                    <p>
                                        <strong>Usuario:</strong> {result.username}
                                    </p>
                                    <p>
                                        <strong>Email:</strong> {result.email}
                                    </p>
                                    <p>
                                        <strong>Rol:</strong> {result.role}
                                    </p>
                                </>
                            )}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}