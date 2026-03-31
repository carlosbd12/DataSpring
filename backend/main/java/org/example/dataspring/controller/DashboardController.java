package org.example.dataspring.controller;

import org.example.dataspring.dto.dashboard.ChartPointResponse;
import org.example.dataspring.dto.dashboard.DashboardSummaryResponse;
import org.example.dataspring.dto.dashboard.DatasetInfoResponse;
import org.example.dataspring.entity.User;
import org.example.dataspring.repository.UserRepository;
import org.example.dataspring.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    public DashboardController(DashboardService dashboardService, UserRepository userRepository) {
        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
    }

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary(Authentication authentication) {
        User currentUser = null;

        if (authentication != null && authentication.isAuthenticated()) {
            currentUser = userRepository.findByUsername(authentication.getName()).orElse(null);
        }

        return dashboardService.getSummary(currentUser);
    }

    @GetMapping("/hourly")
    public List<ChartPointResponse> getHourlyChart(
            @RequestParam(required = false) LocalDate date
    ) {
        return dashboardService.getHourlyChart(date);
    }

    @GetMapping("/load-type")
    public List<ChartPointResponse> getLoadTypeChart(
            @RequestParam(defaultValue = "total") String mode
    ) {
        return dashboardService.getLoadTypeChart(mode);
    }

    @GetMapping("/day-of-week")
    public List<ChartPointResponse> getDayOfWeekChart(
            @RequestParam(defaultValue = "total") String mode
    ) {
        return dashboardService.getDayOfWeekChart(mode);
    }

    @GetMapping("/co2")
    public List<ChartPointResponse> getCo2Chart(
            @RequestParam(defaultValue = "total") String mode
    ) {
        return dashboardService.getCo2Chart(mode);
    }

    @GetMapping("/week-status")
    public List<ChartPointResponse> getWeekStatusChart() {
        return dashboardService.getWeekStatusChart();
    }

    @GetMapping("/dataset")
    public DatasetInfoResponse getDatasetInfo() {
        return dashboardService.getDatasetInfo();
    }
}