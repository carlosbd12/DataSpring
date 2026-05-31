package org.example.datasensefx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.datasensefx.model.*;
import org.example.datasensefx.services.DataLoader;
import org.example.datasensefx.services.ReportGenerator;
import org.example.datasensefx.utils.SceneManager;
import org.example.datasensefx.utils.UserSession;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class ReportsController {

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

    @FXML
    private Button btnDailyReport;

    @FXML
    private Button btnWeeklyReport;

    @FXML
    private Button btnMonthlyReport;

    @FXML
    private Button btnEfficiencyReport;

    @FXML
    private DatePicker dpDailyReport;

    private List<Measurement> energyData;
    private ReportGenerator reportGenerator;
    private LocalDate minDate;
    private LocalDate maxDate;

    /**
     * Método que se ejecuta automáticamente después de cargar el FXML
     */
    @FXML
    public void initialize() {
        // Configurar eventos de los botones del sidebar
        btnInicio.setOnAction(event -> handleInicio());
        btnDispositivos.setOnAction(event -> handleDispositivos());
        btnInformes.setOnAction(event -> handleInformes());
        btnConfiguracion.setOnAction(event -> handleConfiguracion());

        // Datos de sesión
        UserSession session = UserSession.getInstance();

        // Mostrar email del usuario arriba a la derecha
        String email = session.getUserEmail();
        if (email == null || email.isEmpty()) {
            lblUserName.setText("Invitado ▼");
        } else {
            lblUserName.setText(email + " ▼");
        }

        // Ajustar botones según el rol
        Rol rol = session.getRol();
        if (rol != null) {
            if (rol == Rol.GESTOR_EDIFICIO) {
                // El gestor solo debería moverse por informes
                btnDispositivos.setDisable(true);
            }
            if (rol != Rol.ADMIN_PLATAFORMA) {
                // Solo el admin puede tocar Configuración
                btnConfiguracion.setDisable(true);
            }
        } else {
            // Sin rol → por seguridad, sin configuración
            btnConfiguracion.setDisable(true);
        }

        // Cargar datos energéticos
        loadEnergyData();

        // Configurar DatePicker después de cargar datos
        configureDatePickers();
    }

    /**
     * Carga los datos energéticos desde CSV
     */
    private void loadEnergyData() {
        try {
            System.out.println("🔄 Cargando datos para informes...");

            // Intentar cargar desde classpath primero
            try {
                energyData = DataLoader.loadFromClasspath("/data/steel_industry_data.csv");
                System.out.println("✓ Datos cargados desde classpath para informes");
            } catch (Exception e1) {
                // Fallback a sistema de archivos
                Path csvPath = Path.of("src/main/resources/data/steel_industry_data.csv");

                if (!csvPath.toFile().exists()) {
                    csvPath = Path.of("DataSenseFX/src/main/resources/data/steel_industry_data.csv");
                }

                if (csvPath.toFile().exists()) {
                    energyData = DataLoader.loadFromCsv(csvPath);
                    System.out.println("✓ Datos cargados desde sistema de archivos para informes");
                }
            }

            if (energyData != null && !energyData.isEmpty()) {
                reportGenerator = new ReportGenerator(energyData);
                System.out.println("✓ ReportGenerator inicializado con " + energyData.size() + " mediciones");

                // Obtener rango de fechas disponibles
                minDate = energyData.stream()
                    .map(m -> m.getDate().toLocalDate())
                    .min(LocalDate::compareTo)
                    .orElse(LocalDate.now().minusYears(1));

                maxDate = energyData.stream()
                    .map(m -> m.getDate().toLocalDate())
                    .max(LocalDate::compareTo)
                    .orElse(LocalDate.now());

                System.out.println("📅 Rango de fechas: " + minDate + " a " + maxDate);
            } else {
                System.err.println("⚠ No se pudieron cargar datos para informes");
            }

        } catch (Exception e) {
            System.err.println("✗ Error cargando datos para informes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configura los DatePickers con las fechas disponibles
     */
    private void configureDatePickers() {
        if (dpDailyReport != null && maxDate != null) {
            // Establecer la fecha más reciente como valor por defecto
            dpDailyReport.setValue(maxDate);

            // Deshabilitar fechas fuera del rango disponible
            dpDailyReport.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);

                    if (date != null && (date.isBefore(minDate) || date.isAfter(maxDate))) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            });

            System.out.println("✓ DatePicker configurado con fecha por defecto: " + maxDate);
        }
    }

    /**
     * Maneja el click en el botón "Inicio"
     * Navega al dashboard
     */
    @FXML
    private void handleInicio() {
        try {
            SceneManager.switchScene("/org/example/datasensefx/views/dashboard-view.fxml",
                    "DataSense - Dashboard", 1000, 700);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo cargar el dashboard");
        }
    }

    /**
     * Maneja el click en el botón "Dispositivos"
     * Navega a la vista de dispositivos
     */
    @FXML
    private void handleDispositivos() {
        try {
            SceneManager.switchScene("/org/example/datasensefx/views/devices-view.fxml",
                    "DataSense - Dispositivos", 1000, 700);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo cargar la vista de dispositivos");
        }
    }

    /**
     * Maneja el click en el botón "Informes"
     * Ya estamos en informes
     */
    @FXML
    private void handleInformes() {
        System.out.println("Ya estás en Informes");
    }

    /**
     * Maneja el click en el botón "Configuración"
     * Solo el ADMIN puede acceder
     */
    @FXML
    private void handleConfiguracion() {
        Rol rol = UserSession.getInstance().getRol();

        if (rol != Rol.ADMIN_PLATAFORMA) {
            showAlert("Permiso denegado",
                    "Solo el administrador de la plataforma puede acceder a la configuración.");
            return;
        }

        try {
            SceneManager.switchScene("/org/example/datasensefx/views/config-view.fxml",
                    "DataSense - Configuración", 1000, 700);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo cargar la vista de configuración");
        }
    }

    /**
     * Maneja el click en el botón "Ver informe" del informe diario
     */
    @FXML
    private void handleVerInformeDiario() {
        if (reportGenerator == null) {
            showAlert("Error", "No hay datos disponibles para generar el informe.");
            return;
        }

        // Obtener la fecha seleccionada
        LocalDate selectedDate = dpDailyReport.getValue();

        if (selectedDate == null) {
            showAlert("Fecha requerida", "Por favor, seleccione una fecha para generar el informe diario.");
            return;
        }

        // Validar que la fecha esté en el rango disponible
        if (selectedDate.isBefore(minDate) || selectedDate.isAfter(maxDate)) {
            showAlert("Fecha inválida",
                String.format("La fecha seleccionada debe estar entre %s y %s",
                    minDate, maxDate));
            return;
        }

        try {
            System.out.println("📊 Generando informe diario para: " + selectedDate);
            DailyReport report = reportGenerator.generateDailyReport(selectedDate);

            if (report == null) {
                showAlert("Sin datos",
                    "No hay datos disponibles para la fecha seleccionada: " + selectedDate);
                return;
            }

            showReportDetail(report);

        } catch (Exception e) {
            System.err.println("✗ Error generando informe diario: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error al generar el informe: " + e.getMessage());
        }
    }

    /**
     * Maneja el click en el botón "Ver informe" del informe semanal
     */
    @FXML
    private void handleVerInformeSemanal() {
        if (reportGenerator == null) {
            showAlert("Error", "No hay datos disponibles para generar el informe.");
            return;
        }

        try {
            System.out.println("📊 Generando informe semanal...");
            WeeklyReport report = reportGenerator.generateWeeklyReport();

            if (report == null) {
                showAlert("Error", "No se pudo generar el informe semanal.");
                return;
            }

            showReportDetail(report);

        } catch (Exception e) {
            System.err.println("✗ Error generando informe semanal: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error al generar el informe: " + e.getMessage());
        }
    }

    /**
     * Maneja el click en el botón "Ver informe" del informe mensual
     */
    @FXML
    private void handleVerInformeMensual() {
        if (reportGenerator == null) {
            showAlert("Error", "No hay datos disponibles para generar el informe.");
            return;
        }

        try {
            System.out.println("📊 Generando informe mensual...");
            MonthlyReport report = reportGenerator.generateMonthlyReport();

            if (report == null) {
                showAlert("Error", "No se pudo generar el informe mensual.");
                return;
            }

            showReportDetail(report);

        } catch (Exception e) {
            System.err.println("✗ Error generando informe mensual: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error al generar el informe: " + e.getMessage());
        }
    }

    /**
     * Maneja el click en el botón "Ver informe" del informe de eficiencia
     */
    @FXML
    private void handleVerInformeEficiencia() {
        if (reportGenerator == null) {
            showAlert("Error", "No hay datos disponibles para generar el informe.");
            return;
        }

        try {
            System.out.println("📊 Generando informe de eficiencia...");
            EfficiencyReport report = reportGenerator.generateEfficiencyReport();

            if (report == null) {
                showAlert("Error", "No se pudo generar el informe de eficiencia.");
                return;
            }

            showReportDetail(report);

        } catch (Exception e) {
            System.err.println("✗ Error generando informe de eficiencia: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Error al generar el informe: " + e.getMessage());
        }
    }

    /**
     * Muestra la vista de detalle del informe
     */
    private void showReportDetail(Report report) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/datasensefx/views/report-detail-view.fxml")
            );
            Parent root = loader.load();

            // Obtener el controlador y pasarle el informe
            ReportDetailController controller = loader.getController();
            controller.setReport(report);

            // Cambiar la escena
            Stage stage = (Stage) btnInformes.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.setTitle("DataSense - " + report.getTitle());

            System.out.println("✓ Informe mostrado correctamente");

        } catch (Exception e) {
            System.err.println("✗ Error mostrando detalle del informe: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "No se pudo mostrar el informe: " + e.getMessage());
        }
    }

    /**
     * Maneja el click en el botón "Cerrar Sesión"
     * Regresa a la pantalla de login
     */
    @FXML
    private void handleLogout() {
        try {
            UserSession.getInstance().clearSession();

            SceneManager.switchScene("/org/example/datasensefx/views/login-view.fxml",
                    "DataSense - Iniciar Sesión", 500, 650);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo cerrar sesión");
        }
    }

    /**
     * Método auxiliar para mostrar alertas de error
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

