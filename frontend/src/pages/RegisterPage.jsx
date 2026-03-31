import { useState } from "react";
import client from "../api/client";

export default function RegisterPage() {
  const [form, setForm] = useState({
    username: "",
    email: "",
    nombre: "",
    password: "",
    role: "USER"
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
    setError("");
    setMessage("");

    try {
      const response = await client.post("/auth/register", form);
      setMessage(`Usuario creado correctamente: ${response.data.username}`);
      setForm({
        username: "",
        email: "",
        nombre: "",
        password: "",
        role: "USER"
      });
    } catch (err) {
      setError(err?.response?.data?.detail || "No se pudo registrar el usuario");
    }
  };

  return (
    <div className="card">
      <h2>Registro</h2>

      <form className="form" onSubmit={handleSubmit}>
        <input
          name="username"
          placeholder="Username"
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
          name="nombre"
          placeholder="Nombre completo"
          value={form.nombre}
          onChange={handleChange}
        />

        <input
          name="password"
          type="password"
          placeholder="Contraseña"
          value={form.password}
          onChange={handleChange}
        />

        <select name="role" value={form.role} onChange={handleChange}>
          <option value="USER">USER</option>
          <option value="ADMIN">ADMIN</option>
        </select>

        <button type="submit">Registrar</button>
      </form>

      {message && <p className="success">{message}</p>}
      {error && <p className="error">{error}</p>}
    </div>
  );
}