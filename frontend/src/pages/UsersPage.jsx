import { useEffect, useState } from "react";
import {
    activateUser,
    createUser,
    deactivateUser,
    deleteUser,
    getUsers,
    updateUser
} from "../api/usersApi";
import UserForm from "../components/users/UserForm";
import UsersTable from "../components/users/UsersTable";
import UserModal from "../components/users/UserModal";

export default function UsersPage() {
    const [users, setUsers] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const [temporaryPassword, setTemporaryPassword] = useState("");

    const loadUsers = async () => {
        try {
            setLoading(true);
            setError("");
            const data = await getUsers();
            setUsers(data);
        } catch (err) {
            setError(readError(err, "No se pudieron cargar los usuarios"));
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadUsers();
    }, []);

    const handleCreate = async (payload) => {
        try {
            setError("");
            setMessage("");
            setTemporaryPassword("");

            const response = await createUser(payload);

            setMessage("Usuario creado correctamente");
            setTemporaryPassword(response?.temporaryPassword || "");
            await loadUsers();
        } catch (err) {
            setError(readError(err, "No se pudo crear el usuario"));
        }
    };

    const handleEdit = async (payload) => {
        try {
            setError("");
            setMessage("");
            await updateUser(selectedUser.id, payload);
            setMessage("Usuario actualizado correctamente");
            setSelectedUser(null);
            await loadUsers();
        } catch (err) {
            setError(readError(err, "No se pudo actualizar el usuario"));
        }
    };

    const handleActivate = async (id) => {
        try {
            setError("");
            setMessage("");
            await activateUser(id);
            setMessage("Usuario activado correctamente");
            await loadUsers();
        } catch (err) {
            setError(readError(err, "No se pudo activar el usuario"));
        }
    };

    const handleDeactivate = async (id) => {
        try {
            setError("");
            setMessage("");
            await deactivateUser(id);
            setMessage("Usuario desactivado correctamente");
            await loadUsers();
        } catch (err) {
            setError(readError(err, "No se pudo desactivar el usuario"));
        }
    };

    const handleDelete = async (id) => {
        const confirmed = window.confirm("¿Seguro que quieres eliminar este usuario?");
        if (!confirmed) return;

        try {
            setError("");
            setMessage("");
            await deleteUser(id);
            setMessage("Usuario eliminado correctamente");
            await loadUsers();
        } catch (err) {
            setError(readError(err, "No se pudo eliminar el usuario"));
        }
    };

    return (
        <div className="page-grid">
            <div className="card">
                <h2>Crear usuario</h2>
                <UserForm submitLabel="Crear usuario" onSubmit={handleCreate} />
            </div>

            {message && <div className="alert success-alert">{message}</div>}
            {temporaryPassword && (
                <div className="alert success-alert">
                    <strong>Contraseña temporal:</strong> {temporaryPassword}
                </div>
            )}
            {error && <div className="alert error-alert">{error}</div>}

            {loading ? (
                <div className="card">
                    <p>Cargando usuarios...</p>
                </div>
            ) : (
                <UsersTable
                    users={users}
                    onEdit={setSelectedUser}
                    onActivate={handleActivate}
                    onDeactivate={handleDeactivate}
                    onDelete={handleDelete}
                />
            )}

            {selectedUser && (
                <UserModal
                    title={`Editar usuario: ${selectedUser.username}`}
                    onClose={() => setSelectedUser(null)}
                >
                    <UserForm
                        initialValues={selectedUser}
                        isEdit={true}
                        submitLabel="Guardar cambios"
                        onSubmit={handleEdit}
                        onCancel={() => setSelectedUser(null)}
                    />
                </UserModal>
            )}
        </div>
    );
}

function readError(err, fallback) {
    return err?.response?.data?.detail || err?.response?.data?.message || fallback;
}