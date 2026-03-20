package org.example.dataspring.service;

import org.example.dataspring.dashboard.ChartPointResponse;
import org.example.dataspring.model.MeasurementRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EnergyAnalyticsService {

    private static final List<String> DAY_ORDER = List.of(
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
    );

    private final CsvEnergyDataService csvEnergyDataService;

    public EnergyAnalyticsService(CsvEnergyDataService csvEnergyDataService) {
        this.csvEnergyDataService = csvEnergyDataService;
    }

    public long getTotalMeasurements() {
        return records().size();
    }

    public double getAverageConsumption() {
        return records().stream()
                .mapToDouble(MeasurementRecord::getUsageKwh)
                .average()
                .orElse(0.0);
    }

    public double getMinConsumption() {
        return records().stream()
                .mapToDouble(MeasurementRecord::getUsageKwh)
                .min()
                .orElse(0.0);
    }

    public double getMaxConsumption() {
        return records().stream()
                .mapToDouble(MeasurementRecord::getUsageKwh)
                .max()
                .orElse(0.0);
    }

    public double getTotalCo2() {
        return records().stream()
                .mapToDouble(MeasurementRecord::getCo2)
                .sum();
    }

    public double getPeakConsumption() {
        return getMaxConsumption();
    }

    public LocalDate getFirstAvailableDate() {
        return records().stream()
                .map(record -> record.getTimestamp().toLocalDate())
                .min(LocalDate::compareTo)
                .orElse(null);
    }

    public LocalDate getLastAvailableDate() {
        return records().stream()
                .map(record -> record.getTimestamp().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    public String getDatasetInfoText() {
        LocalDate first = getFirstAvailableDate();
        LocalDate last = getLastAvailableDate();

        return "%,d mediciones (%s a %s)".formatted(
                getTotalMeasurements(),
                first != null ? first : "-",
                last != null ? last : "-"
        );
    }

    public String getCurrentFileName() {
        return csvEnergyDataService.getCurrentFileName();
    }

    public List<ChartPointResponse> getHourlyChart(LocalDate date) {
        Map<Integer, Double> usageByHour;

        if (date == null) {
            usageByHour = records().stream()
                    .collect(Collectors.groupingBy(
                            MeasurementRecord::getHour,
                            TreeMap::new,
                            Collectors.summingDouble(MeasurementRecord::getUsageKwh)
                    ));
        } else {
            usageByHour = records().stream()
                    .filter(record -> record.getTimestamp().toLocalDate().equals(date))
                    .collect(Collectors.groupingBy(
                            MeasurementRecord::getHour,
                            TreeMap::new,
                            Collectors.summingDouble(MeasurementRecord::getUsageKwh)
                    ));
        }

        List<ChartPointResponse> result = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            result.add(new ChartPointResponse(String.format("%02d:00", hour), usageByHour.getOrDefault(hour, 0.0)));
        }
        return result;
    }

    public List<ChartPointResponse> getLoadTypeChart(String mode) {
        return buildGroupedChart(
                MeasurementRecord::getLoadType,
                MeasurementRecord::getUsageKwh,
                mode,
                List.of("Light Load", "Medium Load", "Maximum Load")
        );
    }

    public List<ChartPointResponse> getDayOfWeekChart(String mode) {
        return buildGroupedChart(
                MeasurementRecord::getDayOfWeek,
                MeasurementRecord::getUsageKwh,
                mode,
                DAY_ORDER
        );
    }

    public List<ChartPointResponse> getCo2Chart(String mode) {
        return buildGroupedChart(
                MeasurementRecord::getDayOfWeek,
                MeasurementRecord::getCo2,
                mode,
                DAY_ORDER
        );
    }

    public List<ChartPointResponse> getWeekStatusChart() {
        Map<String, Double> grouped = records().stream()
                .collect(Collectors.groupingBy(
                        record -> normalizeWeekStatus(record.getWeekStatus()),
                        LinkedHashMap::new,
                        Collectors.summingDouble(MeasurementRecord::getUsageKwh)
                ));

        return List.of(
                new ChartPointResponse("Laborable", grouped.getOrDefault("Laborable", 0.0)),
                new ChartPointResponse("Fin de semana", grouped.getOrDefault("Fin de semana", 0.0))
        );
    }

    private List<ChartPointResponse> buildGroupedChart(Function<MeasurementRecord, String> labelExtractor,
                                                       Function<MeasurementRecord, Double> valueExtractor,
                                                       String mode,
                                                       List<String> order) {
        boolean average = "average".equalsIgnoreCase(mode);

        Map<String, Double> aggregated;
        if (average) {
            aggregated = records().stream()
                    .collect(Collectors.groupingBy(
                            labelExtractor,
                            Collectors.averagingDouble(record -> valueExtractor.apply(record))
                    ));
        } else {
            aggregated = records().stream()
                    .collect(Collectors.groupingBy(
                            labelExtractor,
                            Collectors.summingDouble(record -> valueExtractor.apply(record))
                    ));
        }

        List<ChartPointResponse> result = new ArrayList<>();
        for (String label : order) {
            result.add(new ChartPointResponse(label, aggregated.getOrDefault(label, 0.0)));
        }
        return result;
    }

    private String normalizeWeekStatus(String weekStatus) {
        if (weekStatus == null) {
            return "Desconocido";
        }

        return switch (weekStatus.trim().toLowerCase(Locale.ROOT)) {
            case "weekday" -> "Laborable";
            case "weekend" -> "Fin de semana";
            default -> weekStatus;
        };
    }

    private List<MeasurementRecord> records() {
        return csvEnergyDataService.getRecords();
    }
}