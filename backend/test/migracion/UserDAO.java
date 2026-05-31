package org.example.datasensefx.dao;

import org.example.datasensefx.model.Rol;
import org.example.datasensefx.model.User;
import org.example.datasensefx.utils.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la gestión de usuarios en la base de datos.
 */
public class UserDAO {

    /**
     * Busca un usuario por su username o email.
     *
     * @param usernameOrEmail Username o email del usuario
     * @return Usuario encontrado o null si no existe
     */
    public User findByUsernameOrEmail(String usernameOrEmail) {
        String sql = "SELECT id, username, email, nombre, password_hash, rol, activo, fecha_creacion, ultimo_acceso " +
                     "FROM usuarios WHERE username = ? OR email = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca un usuario por su email.
     *
     * @param email Email del usuario
     * @return Usuario encontrado o null si no existe
     */
    public User findByEmail(String email) {
        String sql = "SELECT id, username, email, nombre, password_hash, rol, activo, fecha_creacion, ultimo_acceso " +
                     "FROM usuarios WHERE email = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id ID del usuario
     * @return Usuario encontrado o null si no existe
     */
    public User findById(int id) {
        String sql = "SELECT id, username, email, nombre, password_hash, rol, activo, fecha_creacion, ultimo_acceso " +
                     "FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene todos los usuarios del sistema.
     * 
     * @return Lista de usuarios
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, email, nombre, password_hash, rol, activo, fecha_creacion, ultimo_acceso " +
                     "FROM usuarios ORDER BY fecha_creacion DESC";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     *
     * @param user Usuario a crear
     * @return true si se creó correctamente, false en caso contrario
     */
    public boolean create(User user) {
        String sql = "INSERT INTO usuarios (username, email, nombre, password_hash, rol, activo, fecha_creacion) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getNombre());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getRol().name());
            stmt.setBoolean(6, user.isActivo());
            stmt.setTimestamp(7, Timestamp.valueOf(user.getFechaCreacion()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Obtener el ID generado
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
                System.out.println("✅ Usuario creado: " + user.getUsername());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param user Usuario con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean update(User user) {
        String sql = "UPDATE usuarios SET email = ?, nombre = ?, rol = ?, activo = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getNombre());
            stmt.setString(3, user.getRol().name());
            stmt.setBoolean(4, user.isActivo());
            stmt.setInt(5, user.getId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Elimina un usuario de la base de datos.
     *
     * @param id ID del usuario a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     * No se usa actualmente, únicamente se deshabilita el usuario, no se elimina.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Autentica un usuario verificando username/email y contraseña.
     *
     * @param usernameOrEmail Username o email del usuario
     * @param plainPassword Contraseña en texto plano
     * @return Usuario autenticado o null si las credenciales son incorrectas
     */
    public User authenticate(String usernameOrEmail, String plainPassword) {
        User user = findByUsernameOrEmail(usernameOrEmail);

        if (user == null) {
            System.out.println("❌ Usuario no encontrado: " + usernameOrEmail);
            return null;
        }

        if (!user.isActivo()) {
            System.out.println("❌ Usuario inactivo: " + usernameOrEmail);
            return null;
        }

        // Comparación directa de contraseñas en texto plano
        if (plainPassword.equals(user.getPasswordHash())) {
            System.out.println("✅ Autenticación exitosa: " + user.getUsername() + " (" + user.getEmail() + ")");
            updateLastAccess(user.getId());
            return user;
        }

        System.out.println("❌ Contraseña incorrecta para: " + usernameOrEmail);
        return null;
    }

    /**
     * Cambia la contraseña de un usuario.
     *
     * @param userId ID del usuario
     * @param newPassword Nueva contraseña en texto plano
     * @return true si se cambió correctamente, false en caso contrario
     */
    public boolean changePassword(int userId, String newPassword) {
        String sql = "UPDATE usuarios SET password_hash = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Guardar contraseña en texto plano
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar contraseña: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Actualiza la fecha de último acceso del usuario.
     *
     * @param userId ID del usuario
     */
    public void updateLastAccess(int userId) {
        String sql = "UPDATE usuarios SET ultimo_acceso = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar último acceso: " + e.getMessage());
        }
    }

    /**
     * Activa un usuario.
     *
     * @param userId ID del usuario
     * @return true si se activó correctamente, false en caso contrario
     */
    public boolean activateUser(int userId) {
        return setUserActiveStatus(userId, true);
    }

    /**
     * Desactiva un usuario.
     *
     * @param userId ID del usuario
     * @return true si se desactivó correctamente, false en caso contrario
     */
    public boolean deactivateUser(int userId) {
        return setUserActiveStatus(userId, false);
    }

    /**
     * Cambia el estado activo de un usuario.
     *
     * @param userId ID del usuario
     * @param active Estado activo
     * @return true si se cambió correctamente, false en caso contrario
     */
    private boolean setUserActiveStatus(int userId, boolean active) {
        String sql = "UPDATE usuarios SET activo = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, active);
            stmt.setInt(2, userId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado del usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mapea un ResultSet a un objeto User.
     *
     * @param rs ResultSet con los datos del usuario
     * @return Objeto User
     * @throws SQLException Si hay error al leer el ResultSet
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setNombre(rs.getString("nombre"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRol(Rol.valueOf(rs.getString("rol")));
        user.setActivo(rs.getBoolean("activo"));

        Timestamp fechaCreacion = rs.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            user.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        Timestamp ultimoAcceso = rs.getTimestamp("ultimo_acceso");
        if (ultimoAcceso != null) {
            user.setUltimoAcceso(ultimoAcceso.toLocalDateTime());
        }

        return user;
    }
}

