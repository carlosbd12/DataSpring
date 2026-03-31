import { useState } from "react";
import client from "../api/client";
import { useAuth } from "../context/AuthContext";

export default function ChangePasswordPage() {
    const { authUser } = useAuth();
    const [form, setForm] = useState({
        currentPassword: "",
        newPassword: ""
    });
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    const handleChange = (e) => {
        setForm((prev) => ({
            ...prev,
            [e.target.name]: e.target.value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setError("");

        if (!authUser?.userId) {
            setError("Debes iniciar sesión antes de cambiar la contraseña");
            return;
        }

        try {
            await client.put(`/auth/change-password/${authUser.userId}`, form);
            setMessage("Contraseña actualizada correctamente");
            setForm({
                currentPassword: "",
                newPassword: ""
            });
        } catch (err) {
            setError(err?.response?.data?.detail || "No se pudo cambiar la contraseña");
        }
    };

    return (
        <div className="card">
            <h2>Cambiar contraseña</h2>

            <form className="form" onSubmit={handleSubmit}>
                <input
                    name="currentPassword"
                    type="password"
                    placeholder="Contraseña actual"
                    value={form.currentPassword}
                    onChange={handleChange}
                />

                <input
                    name="newPassword"
                    type="password"
                    placeholder="Nueva contraseña"
                    value={form.newPassword}
                    onChange={handleChange}
                />

                <button type="submit">Actualizar</button>
            </form>

            {message && <p className="success">{message}</p>}
            {error && <p className="error">{error}</p>}
        </div>
    );
}