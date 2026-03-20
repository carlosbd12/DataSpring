import { useState } from "react";
import client from "../api/client";

export default function UserForm({ onCreated }) {
    const [form, setForm] = useState({
        username: "",
        email: "",
        password: ""
    });
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

        try {
            await client.post("/users", form);
            setForm({ username: "", email: "", password: "" });
            onCreated();
        } catch (err) {
            setError(err?.response?.data?.detail || "No se pudo crear el usuario");
        }
    };

    return (
        <form className="card form" onSubmit={handleSubmit}>
            <h3>Crear usuario</h3>

            <input
                name="username"
                placeholder="Nombre de usuario"
                value={form.username}
                onChange={handleChange}
            />

            <input
                name="email"
                type="email"
                placeholder="Correo"
                value={form.email}
                onChange={handleChange}
            />

            <input
                name="password"
                type="password"
                placeholder="Contraseña"
                value={form.password}
                onChange={handleChange}
            />

            {error && <p className="error">{error}</p>}

            <button type="submit">Guardar</button>
        </form>
    );
}