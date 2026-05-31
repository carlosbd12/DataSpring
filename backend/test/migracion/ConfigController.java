package org.example.datasensefx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.datasensefx.dao.UserDAO;
import org.example.datasensefx.model.Rol;
import org.example.datasensefx.model.User;
import org.example.datasensefx.utils.PasswordGenerator;
import org.example.datasensefx.utils.SceneManager;
import org.example.datasensefx.utils.UserSession;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfigController {

    // SIDEBAR
    @FXML
    private Button btnInicio;
    @FXML
    private Button btnDispositivos;
    @FXML
    private Button btnInformes;
    @FXML
    private Button btnConfiguracion;
    @FXML
    private Label lblUserName;

    // FORMULARIO
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<Rol> cbRol;
    @FXML
    private Button btnAñadir;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnCancelar;

    // TABLA
    @FXML
    private TableView<User> tableUsuarios;
    @FXML
    private TableColumn<User, String> colUsername;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, String> colNombre;
    @FXML
    private TableColumn<User, Rol> colRol;
    @FXML
    private TableColumn<User, Boolean> colEstado;
    @FXML
    private TableColumn<User, java.time.LocalDateTime> colFechaCreacion;
    @FXML
    private TableColumn<User, Void> colAcciones;

    // SERVICIOS
    private UserDAO userDAO;
    private ObservableList<User> usuariosObservableList;

    // ESTADO
    private User usuarioEnEdicion = null;

    @FXML
    public void initialize() {
        // Inicializar servicios
        userDAO = new UserDAO();
        usuariosObservableList = FXCollections.observableArrayList();

        // Configurar sidebar
        configurarSidebar();

        // Validar acceso ADMIN
        validarAccesoAdmin();

        // Configurar formulario
        configurarFormulario();

        // Configurar tabla
        configurarTabla();

        // Cargar usuarios
        cargarUsuariosEnTabla();
    }

    private void configurarSidebar() {
        btnInicio.setOnAction(event -> handleInicio());
        btnDispositivos.setOnAction(event -> handleDispositivos());
        btnInformes.setOnAction(event -> handleInformes());
        btnConfiguracion.setOnAction(event -> handleConfiguracion());

        UserSession session = UserSession.getInstance();
        String email = session.getUserEmail();
        lblUserName.setText(email != null ? email + " ▼" : "Invitado ▼");
    }

    private void validarAccesoAdmin() {
        Rol rol = UserSession.getInstance().getRol();
        if (rol != Rol.ADMIN_PLATAFORMA) {
            showErrorAlert("Permiso denegado",
                "Solo el administrador de la plataforma puede acceder al panel de configuración.");
            try {
                SceneManager.switchScene("/org/example/datasensefx/views/dashboard-view.fxml",
                    "DataSense - Dashboard", 1000, 700);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void configurarFormulario() {
        // Configurar ComboBox de roles
        cbRol.setItems(FXCollections.observableArrayList(
            Rol.RESPONSABLE_PLANTA,
            Rol.GESTOR_EDIFICIO,
            Rol.ADMIN_PLATAFORMA
        ));

        // Estado inicial: modo añadir
        btnActualizar.setVisible(false);
        btnActualizar.setManaged(false);
        btnCancelar.setVisible(false);
        btnCancelar.setManaged(false);

        // Listeners para validación en tiempo real
        txtUsername.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.isEmpty() && newVal.length() < 3) {
                txtUsername.setStyle("-fx-border-color: red; -fx-border-width: 1;");
            } else {
                txtUsername.setStyle("");
            }
        });

        txtEmail.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.isEmpty() && !isValidEmail(newVal)) {
                txtEmail.setStyle("-fx-border-color: red; -fx-border-width: 1;");
            } else {
                txtEmail.setStyle("");
            }
        });
    }

    private void configurarTabla() {
        // Configurar columnas
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        // Columna Estado con estilo visual
        colEstado.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colEstado.setCellFactory(column -> new TableCell<User, Boolean>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty || activo == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label(activo ? "Activo" : "Inactivo");
                    label.setStyle(activo
                        ? "-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                          "-fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 11;"
                        : "-fx-background-color: #9E9E9E; -fx-text-fill: white; " +
                          "-fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 11;");
                    setGraphic(label);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Columna Fecha Creación
        colFechaCreacion.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
        colFechaCreacion.setCellFactory(column -> new TableCell<User, java.time.LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(java.time.LocalDateTime fecha, boolean empty) {
                super.updateItem(fecha, empty);
                if (empty || fecha == null) {
                    setText(null);
                } else {
                    setText(formatter.format(fecha));
                }
            }
        });

        // Columna Acciones con botones
        colAcciones.setCellFactory(column -> new TableCell<User, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnToggle = new Button();
            private final HBox hbox = new HBox(8, btnEditar, btnToggle);

            {
                hbox.setAlignment(Pos.CENTER);

                btnEditar.setStyle(
                    "-fx-background-color: #2196F3; -fx-text-fill: white; " +
                    "-fx-cursor: hand; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 11;");

                btnEditar.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    cargarUsuarioEnFormulario(user);
                });

                btnToggle.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    toggleEstadoUsuario(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    btnToggle.setText(user.isActivo() ? "Desactivar" : "Activar");
                    btnToggle.setStyle(user.isActivo()
                        ? "-fx-background-color: #FF9800; -fx-text-fill: white; " +
                          "-fx-cursor: hand; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 11;"
                        : "-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                          "-fx-cursor: hand; -fx-padding: 4 8; -fx-background-radius: 4; -fx-font-size: 11;");
                    setGraphic(hbox);
                }
            }
        });

        tableUsuarios.setItems(usuariosObservableList);
    }

    private void cargarUsuariosEnTabla() {
        List<User> usuarios = userDAO.findAll();
        usuariosObservableList.clear();
        usuariosObservableList.addAll(usuarios);
        System.out.println("✓ Cargados " + usuarios.size() + " usuarios en la tabla");
    }

    @FXML
    private void handleAñadirUsuario() {
        // Validar formulario
        ValidationResult validation = validarFormulario(false);
        if (!validation.isValid()) {
            showErrorAlert("Errores de validación",
                "Por favor corrige los siguientes errores:\n\n- " + validation.getErrorMessage());
            return;
        }

        // Generar contraseña temporal
        String tempPassword = PasswordGenerator.generateTemporaryPassword();

        // Crear usuario
        User newUser = new User(
            txtUsername.getText().trim(),
            txtEmail.getText().trim(),
            txtNombre.getText().trim(),
            tempPassword,
            cbRol.getValue()
        );

        boolean created = userDAO.create(newUser);

        if (created) {
            // Mostrar contraseña temporal
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Usuario creado exitosamente");
            alert.setHeaderText("Usuario: " + newUser.getUsername());

            TextArea textArea = new TextArea(
                "Usuario: " + newUser.getUsername() + "\n" +
                "Email: " + newUser.getEmail() + "\n" +
                "Contraseña temporal: " + tempPassword + "\n\n" +
                "⚠️ Importante: Comparte esta contraseña de forma segura con el usuario."
            );
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefHeight(150);

            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();

            limpiarFormulario();
            cargarUsuariosEnTabla();
        } else {
            showErrorAlert("Error", "No se pudo crear el usuario. Intenta nuevamente.");
        }
    }

    @FXML
    private void handleActualizarUsuario() {
        if (usuarioEnEdicion == null) {
            showErrorAlert("Error", "No hay usuario seleccionado para editar.");
            return;
        }

        // Validar formulario
        ValidationResult validation = validarFormulario(true);
        if (!validation.isValid()) {
            showErrorAlert("Errores de validación",
                "Por favor corrige los siguientes errores:\n\n- " + validation.getErrorMessage());
            return;
        }

        // Actualizar usuario
        usuarioEnEdicion.setEmail(txtEmail.getText().trim());
        usuarioEnEdicion.setNombre(txtNombre.getText().trim());
        usuarioEnEdicion.setRol(cbRol.getValue());

        boolean updated = userDAO.update(usuarioEnEdicion);

        if (updated) {
            showInfoAlert("Éxito", "Usuario actualizado correctamente.");
            limpiarFormulario();
            cargarUsuariosEnTabla();
        } else {
            showErrorAlert("Error", "No se pudo actualizar el usuario. Intenta nuevamente.");
        }
    }

    @FXML
    private void handleCancelar() {
        limpiarFormulario();
    }

    private void cargarUsuarioEnFormulario(User user) {
        usuarioEnEdicion = user;

        txtUsername.setText(user.getUsername());
        txtUsername.setDisable(true);
        txtEmail.setText(user.getEmail());
        txtNombre.setText(user.getNombre());
        cbRol.setValue(user.getRol());

        // Cambiar modo a edición
        btnAñadir.setVisible(false);
        btnAñadir.setManaged(false);
        btnActualizar.setVisible(true);
        btnActualizar.setManaged(true);
        btnCancelar.setVisible(true);
        btnCancelar.setManaged(true);
    }

    private void toggleEstadoUsuario(User user) {
        // Validación: No puede desactivar su propia cuenta
        if (user.getId() == UserSession.getInstance().getCurrentUser().getId()) {
            showErrorAlert("Acción no permitida",
                "No puedes desactivar tu propia cuenta mientras estás conectado.");
            return;
        }

        // Validación: No puede desactivar el último admin
        if (user.getRol() == Rol.ADMIN_PLATAFORMA && user.isActivo()) {
            long adminActivos = userDAO.findAll().stream()
                .filter(u -> u.getRol() == Rol.ADMIN_PLATAFORMA && u.isActivo())
                .count();

            if (adminActivos <= 1) {
                showErrorAlert("Acción no permitida",
                    "No puedes desactivar el último administrador activo del sistema.");
                return;
            }
        }

        // Confirmación
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar acción");
        confirmation.setHeaderText(user.isActivo() ? "Desactivar usuario" : "Activar usuario");
        confirmation.setContentText("¿Estás seguro de " +
            (user.isActivo() ? "desactivar" : "activar") +
            " al usuario " + user.getUsername() + "?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = user.isActivo()
                ? userDAO.deactivateUser(user.getId())
                : userDAO.activateUser(user.getId());

            if (success) {
                cargarUsuariosEnTabla();
                showInfoAlert("Éxito",
                    "Usuario " + (user.isActivo() ? "desactivado" : "activado") + " correctamente.");
            } else {
                showErrorAlert("Error", "No se pudo cambiar el estado del usuario.");
            }
        }
    }

    private void limpiarFormulario() {
        usuarioEnEdicion = null;

        txtUsername.clear();
        txtUsername.setDisable(false);
        txtEmail.clear();
        txtNombre.clear();
        cbRol.setValue(null);

        txtUsername.setStyle("");
        txtEmail.setStyle("");

        // Volver a modo añadir
        btnAñadir.setVisible(true);
        btnAñadir.setManaged(true);
        btnActualizar.setVisible(false);
        btnActualizar.setManaged(false);
        btnCancelar.setVisible(false);
        btnCancelar.setManaged(false);
    }

    private ValidationResult validarFormulario(boolean isEdit) {
        ValidationResult result = new ValidationResult();

        // Username (solo en creación)
        if (!isEdit) {
            String username = txtUsername.getText().trim();
            if (username.isEmpty()) {
                result.addError("El nombre de usuario es obligatorio");
            } else if (username.length() < 3) {
                result.addError("El nombre de usuario debe tener al menos 3 caracteres");
            } else {
                User existing = userDAO.findByUsernameOrEmail(username);
                if (existing != null) {
                    result.addError("El nombre de usuario ya está en uso");
                }
            }
        }

        // Email
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            result.addError("El email es obligatorio");
        } else if (!isValidEmail(email)) {
            result.addError("El formato del email no es válido");
        } else {
            User existing = userDAO.findByEmail(email);
            if (existing != null && (!isEdit || existing.getId() != usuarioEnEdicion.getId())) {
                result.addError("El email ya está registrado");
            }
        }

        // Nombre
        if (txtNombre.getText().trim().isEmpty()) {
            result.addError("El nombre completo es obligatorio");
        }

        // Rol
        if (cbRol.getValue() == null) {
            result.addError("Debes seleccionar un rol");
        }

        return result;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // NAVEGACIÓN
    @FXML
    private void handleInicio() {
        try {
            SceneManager.switchScene("/org/example/datasensefx/views/dashboard-view.fxml",
                "DataSense - Dashboard", 1000, 700);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "No se pudo cargar el dashboard");
        }
    }

    @FXML
    private void handleDispositivos() {
        try {
            SceneManager.switchScene("/org/example/datasensefx/views/devices-view.fxml",
                "DataSense - Dispositivos", 1000, 700);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "No se pudo cargar la vista de dispositivos");
        }
    }

    @FXML
    private void handleInformes() {
        try {
            SceneManager.switchScene("/org/example/datasensefx/views/reports-view.fxml",
                "DataSense - Informes", 1000, 700);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "No se pudo cargar la vista de informes");
        }
    }

    @FXML
    private void handleConfiguracion() {
        System.out.println("Ya estás en Configuración");
    }

    @FXML
    private void handleLogout() {
        try {
            UserSession.getInstance().clearSession();
            SceneManager.switchScene("/org/example/datasensefx/views/login-view.fxml",
                "DataSense - Iniciar Sesión", 500, 650);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "No se pudo cerrar sesión");
        }
    }

    // UTILIDADES
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // CLASE INTERNA
    private static class ValidationResult {
        private final List<String> errors = new ArrayList<>();

        public void addError(String error) {
            errors.add(error);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public String getErrorMessage() {
            return String.join("\n- ", errors);
        }
    }
}
