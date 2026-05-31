package org.example.datasensefx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.example.datasensefx.model.Report;
import org.example.datasensefx.model.Rol;
import org.example.datasensefx.services.PDFExportService;
import org.example.datasensefx.utils.SceneManager;
import org.example.datasensefx.utils.UserSession;

import java.io.File;
import java.time.format.DateTimeFormatter;

/**
 * Controlador para la vista de detalle de un informe
 */
public class ReportDetailController {
    
    @FXML
    private Button btnInicio;
    
    @FXML
    private Button btnDispositivos;
    
    @FXML
    private Button btnInformes;
    
    @FXML
    private Button btnConfiguracion;
    
    @FXML
    private Button btnVolver;
    
    @FXML
    private Button btnExportPDF;
    
    @FXML
    private Label lblUserName;
    
    @FXML
    private Label lblReportTitle;
    
    @FXML
    private Label lblReportType;
    
    @FXML
    private Label lblReportPeriod;
    
    @FXML
    private Label lblGeneratedAt;
    
    @FXML
    private TextArea txtReportContent;
    
    private Report currentReport;
    
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

        // Vincular explícitamente el botón de exportar si no se hace solo por FXML
        if (btnExportPDF != null) {
            btnExportPDF.setOnAction(event -> handleExportPDF());
        }

        // Datos de sesión
        UserSession session = UserSession.getInstance();
        
        // Mostrar email del usuario
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
                btnDispositivos.setDisable(true);
            }
            if (rol != Rol.ADMIN_PLATAFORMA) {
                btnConfiguracion.setDisable(true);
            }
        } else {
            btnConfiguracion.setDisable(true);
        }
    }
    
    /**
     * Establece el informe a mostrar
     */
    public void setReport(Report report) {
        this.currentReport = report;
        displayReport();
    }
    
    /**
     * Muestra el contenido del informe en la interfaz
     */
    private void displayReport() {
        if (currentReport == null) {
            txtReportContent.setText("No hay informe para mostrar.");
            return;
        }
        
        // Actualizar labels
        lblReportTitle.setText(currentReport.getTitle());
        lblReportType.setText(currentReport.getType().getDisplayName());
        lblReportPeriod.setText("Período: " + currentReport.getPeriod());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblGeneratedAt.setText("Generado: " + currentReport.getGeneratedAt().format(formatter));
        
        // Mostrar contenido del informe
        String content = currentReport.generateContent();
        txtReportContent.setText(content);
    }
    
    /**
     * Maneja el click en el botón "Volver"
     */
    @FXML
    private void handleVolver() {
        try {
            SceneManager.switchScene("/org/example/datasensefx/views/reports-view.fxml",
                    "DataSense - Informes", 1000, 700);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo volver a la vista de informes");
        }
    }
    
    /**
     * Maneja el click en el botón "Exportar PDF"
     */
    @FXML
    private void handleExportPDF() {
        if (currentReport == null) {
            showAlert("Error", "No hay ningún informe para exportar");
            return;
        }

        try {
            // FileChooser para seleccionar ubicación
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar informe como PDF");

            // Nombre sugerido del archivo
            String suggestedFileName = generateFileName(currentReport);
            fileChooser.setInitialFileName(suggestedFileName);

            // Filtro de extensión
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(extFilter);

            // Mostrar diálogo
            File file = fileChooser.showSaveDialog(btnExportPDF.getScene().getWindow());

            if (file != null) {
                // Asegurar extensión .pdf
                if (!file.getName().toLowerCase().endsWith(".pdf")) {
                    file = new File(file.getAbsolutePath() + ".pdf");
                }

                // Exportar usando el servicio
                PDFExportService pdfService = new PDFExportService();
                boolean success = pdfService.exportToPDF(currentReport, file);

                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Exportación exitosa");
                    alert.setHeaderText(null);
                    alert.setContentText("El informe se ha exportado correctamente a:\n" + file.getAbsolutePath());
                    alert.showAndWait();
                } else {
                    showAlert("Error al exportar",
                            "No se pudo generar el contenido del PDF. Verifique los datos del informe.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error Crítico", "Ocurrió un error inesperado al exportar: " + e.getMessage());
        }
    }

    /**
     * Genera un nombre de archivo basado en el tipo de informe y fecha
     */
    private String generateFileName(Report report) {
        String prefix = "";

        switch (report.getType()) {
            case DAILY:
                prefix = "Informe_Diario";
                break;
            case WEEKLY:
                prefix = "Informe_Semanal";
                break;
            case MONTHLY:
                prefix = "Informe_Mensual";
                break;
            case EFFICIENCY:
                prefix = "Informe_Eficiencia";
                break;
        }

        String period = report.getPeriod().replaceAll("[/\\s:]", "_");
        return prefix + "_" + period + ".pdf";
    }
    
    /**
     * Navega al Dashboard
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
     * Navega a Dispositivos
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
     * Navega a Informes
     */
    @FXML
    private void handleInformes() {
        handleVolver();
    }
    
    /**
     * Navega a Configuración
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
     * Cierra sesión
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
     * Muestra una alerta de error
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

