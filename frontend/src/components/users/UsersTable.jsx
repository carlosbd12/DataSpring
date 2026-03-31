export default function UsersTable({
                                       users = [],
                                       onEdit,
                                       onActivate,
                                       onDeactivate,
                                       onDelete
                                   }) {
    const safeUsers = Array.isArray(users) ? users : [];

    return (
        <div className="card">
            <h2>Listado de usuarios</h2>

            <table className="table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Nombre</th>
                    <th>Rol</th>
                    <th>Activo</th>
                    <th>Fecha creación</th>
                    <th>Último acceso</th>
                    <th>Acciones</th>
                </tr>
                </thead>

                <tbody>
                {safeUsers.map((user) => (
                    <tr key={user.id}>
                        <td>{user.id}</td>
                        <td>{user.username}</td>
                        <td>{user.email}</td>
                        <td>{user.nombre}</td>
                        <td>{user.role}</td>
                        <td>
                <span className={user.activo ? "badge-active" : "badge-inactive"}>
                  {user.activo ? "Sí" : "No"}
                </span>
                        </td>
                        <td>{formatDate(user.fechaCreacion)}</td>
                        <td>{formatDate(user.ultimoAcceso)}</td>
                        <td>
                            <div className="table-actions">
                                <button onClick={() => onEdit(user)}>Editar</button>

                                {user.activo ? (
                                    <button
                                        className="button-warning"
                                        onClick={() => onDeactivate(user.id)}
                                    >
                                        Desactivar
                                    </button>
                                ) : (
                                    <button
                                        className="button-success"
                                        onClick={() => onActivate(user.id)}
                                    >
                                        Activar
                                    </button>
                                )}

                                <button
                                    className="button-danger"
                                    onClick={() => onDelete(user.id)}
                                >
                                    Eliminar
                                </button>
                            </div>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {safeUsers.length === 0 && <p>No hay usuarios todavía.</p>}
        </div>
    );
}

function formatDate(value) {
    if (!value) return "-";

    try {
        return new Date(value).toLocaleString();
    } catch {
        return value;
    }
}