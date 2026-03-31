import { useEffect, useState } from "react";

const DEFAULT_FORM = {
    username: "",
    email: "",
    nombre: "",
    role: "RESPONSABLE_PLANTA",
    activo: true
};

export default function UserForm({
                                     initialValues,
                                     onSubmit,
                                     onCancel,
                                     submitLabel,
                                     isEdit = false
                                 }) {
    const [form, setForm] = useState(DEFAULT_FORM);

    useEffect(() => {
        if (initialValues) {
            setForm({
                username: initialValues.username ?? "",
                email: initialValues.email ?? "",
                nombre: initialValues.nombre ?? "",
                role: initialValues.role ?? "RESPONSABLE_PLANTA",
                activo: initialValues.activo ?? true
            });
        } else {
            setForm(DEFAULT_FORM);
        }
    }, [initialValues]);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;

        setForm((prev) => ({
            ...prev,
            [name]: type === "checkbox" ? checked : value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const payload = {
            username: form.username,
            email: form.email,
            nombre: form.nombre,
            role: form.role,
            activo: form.activo
        };

        await onSubmit(payload);

        if (!isEdit) {
            setForm(DEFAULT_FORM);
        }
    };

    return (
        <form className="form" onSubmit={handleSubmit}>
            <div className="form-grid">
                <div>
                    <label>Username</label>
                    <input
                        name="username"
                        value={form.username}
                        onChange={handleChange}
                        placeholder="Username"
                    />
                </div>

                <div>
                    <label>Email</label>
                    <input
                        name="email"
                        type="email"
                        value={form.email}
                        onChange={handleChange}
                        placeholder="Correo"
                    />
                </div>

                <div>
                    <label>Nombre</label>
                    <input
                        name="nombre"
                        value={form.nombre}
                        onChange={handleChange}
                        placeholder="Nombre completo"
                    />
                </div>

                <div>
                    <label>Rol</label>
                    <select name="role" value={form.role} onChange={handleChange}>
                        <option value="RESPONSABLE_PLANTA">RESPONSABLE_PLANTA</option>
                        <option value="GESTOR_EDIFICIO">GESTOR_EDIFICIO</option>
                        <option value="ADMIN_PLATAFORMA">ADMIN_PLATAFORMA</option>
                    </select>
                </div>

                <div className="checkbox-field">
                    <label>
                        <input
                            name="activo"
                            type="checkbox"
                            checked={form.activo}
                            onChange={handleChange}
                        />
                        Usuario activo
                    </label>
                </div>
            </div>

            <div className="form-actions">
                <button type="submit">{submitLabel}</button>
                {onCancel && (
                    <button type="button" className="button-secondary" onClick={onCancel}>
                        Cancelar
                    </button>
                )}
            </div>
        </form>
    );
}