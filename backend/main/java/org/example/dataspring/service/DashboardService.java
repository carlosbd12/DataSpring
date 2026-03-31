package org.example.dataspring.service;

import org.example.dataspring.dto.dashboard.ChartPointResponse;
import org.example.dataspring.dto.dashboard.DashboardSummaryResponse;
import org.example.dataspring.dto.dashboard.DatasetInfoResponse;
import org.example.dataspring.entity.Role;
import org.example.dataspring.entity.User;
import org.example.dataspring.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final EnergyAnalyticsService energyAnalyticsService;
    private final UserRepository userRepository;

    public DashboardService(EnergyAnalyticsService energyAnalyticsService, UserRepository userRepository) {
        this.energyAnalyticsService = energyAnalyticsService;
        this.userRepository = userRepository;
    }

    public DashboardSummaryResponse getSummary(User user) {
        DashboardSummaryResponse response = new DashboardSummaryResponse();

        response.setUserDisplayName(user != null
                ? (user.getNombre() != null && !user.getNombre().isBlank() ? user.getNombre() : user.getUsername())
                : "Invitado");
        response.setUserRoleLabel(user != null ? mapRoleLabel(user.getRole()) : "Sin rol");

        response.setTotalMeasurements(energyAnalyticsService.getTotalMeasurements());
        response.setAverageConsumption(energyAnalyticsService.getAverageConsumption());
        response.setMinConsumption(energyAnalyticsService.getMinConsumption());
        response.setMaxConsumption(energyAnalyticsService.getMaxConsumption());
        response.setTotalCo2(energyAnalyticsService.getTotalCo2());
        response.setPeakConsumption(energyAnalyticsService.getPeakConsumption());

        response.setDatasetInfo(energyAnalyticsService.getDatasetInfoText());
        response.setFirstAvailableDate(stringify(energyAnalyticsService.getFirstAvailableDate()));
        response.setLastAvailableDate(stringify(energyAnalyticsService.getLastAvailableDate()));

        return response;
    }

    public List<ChartPointResponse> getHourlyChart(LocalDate date) {
        return energyAnalyticsService.getHourlyChart(date);
    }

    public List<ChartPointResponse> getLoadTypeChart(String mode) {
        return energyAnalyticsService.getLoadTypeChart(mode);
    }

    public List<ChartPointResponse> getDayOfWeekChart(String mode) {
        return energyAnalyticsService.getDayOfWeekChart(mode);
    }

    public List<ChartPointResponse> getCo2Chart(String mode) {
        return energyAnalyticsService.getCo2Chart(mode);
    }

    public List<ChartPointResponse> getWeekStatusChart() {
        return energyAnalyticsService.getWeekStatusChart();
    }

    public DatasetInfoResponse getDatasetInfo() {
        DatasetInfoResponse response = new DatasetInfoResponse();
        response.setFileName(energyAnalyticsService.getCurrentFileName());
        response.setTotalMeasurements(energyAnalyticsService.getTotalMeasurements());
        response.setFirstAvailableDate(stringify(energyAnalyticsService.getFirstAvailableDate()));
        response.setLastAvailableDate(stringify(energyAnalyticsService.getLastAvailableDate()));
        return response;
    }

    private String mapRoleLabel(Role role) {
        if (role == null) {
            return "Sin rol";
        }

        return switch (role) {
            case RESPONSABLE_PLANTA -> "Responsable energético de planta";
            case GESTOR_EDIFICIO -> "Gestor de planta";
            case ADMIN_PLATAFORMA -> "Administrador de la plataforma";
        };
    }

    private String stringify(LocalDate date) {
        return date != null ? date.toString() : null;
    }
}