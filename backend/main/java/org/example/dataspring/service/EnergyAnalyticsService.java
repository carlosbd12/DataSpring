package org.example.dataspring.service;

import org.example.dataspring.dto.dashboard.ChartPointResponse;
import org.example.dataspring.repository.MeasurementRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EnergyAnalyticsService {

    private static final String USAGE_KWH = "USAGE_KWH";
    private static final String CO2 = "CO2";

    private static final List<String> DAY_ORDER = List.of(
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
    );

    private static final List<String> LOAD_TYPE_ORDER = List.of(
            "Light Load", "Medium Load", "Maximum Load"
    );

    private final MeasurementRepository measurementRepository;

    public EnergyAnalyticsService(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    public long getTotalMeasurements() {
        return measurementRepository.countByTypeCode(USAGE_KWH);
    }

    public double getAverageConsumption() {
        return nullSafe(measurementRepository.averageByTypeCode(USAGE_KWH));
    }

    public double getMinConsumption() {
        return toDouble(measurementRepository.minByTypeCode(USAGE_KWH));
    }

    public double getMaxConsumption() {
        return toDouble(measurementRepository.maxByTypeCode(USAGE_KWH));
    }

    public double getTotalCo2() {
        return toDouble(measurementRepository.sumByTypeCode(CO2));
    }

    public double getPeakConsumption() {
        return getMaxConsumption();
    }

    public LocalDate getFirstAvailableDate() {
        LocalDateTime firstTimestamp = measurementRepository.findFirstTimestamp();
        return firstTimestamp != null ? firstTimestamp.toLocalDate() : null;
    }

    public LocalDate getLastAvailableDate() {
        LocalDateTime lastTimestamp = measurementRepository.findLastTimestamp();
        return lastTimestamp != null ? lastTimestamp.toLocalDate() : null;
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
        return "Base de datos MySQL";
    }

    public void updateDataset(MultipartFile file) throws IOException {
        throw new UnsupportedOperationException("La carga de CSV no esta disponible usando MySQL como fuente principal");
    }

    public List<ChartPointResponse> getHourlyChart(LocalDate date) {
        List<Object[]> rows = date == null
                ? measurementRepository.sumByHour(USAGE_KWH)
                : measurementRepository.sumByHourAndTimestampBetween(
                        USAGE_KWH,
                        date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay()
                );
        Map<Integer, Double> usageByHour = numberKeyedMap(rows);

        List<ChartPointResponse> result = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            result.add(new ChartPointResponse(String.format("%02d:00", hour), usageByHour.getOrDefault(hour, 0.0)));
        }
        return result;
    }

    public List<ChartPointResponse> getLoadTypeChart(String mode) {
        List<Object[]> rows = isAverageMode(mode)
                ? measurementRepository.averageByEstimatedLoadType(USAGE_KWH)
                : measurementRepository.sumByEstimatedLoadType(USAGE_KWH);
        return orderedLabelChart(rows, LOAD_TYPE_ORDER);
    }

    public List<ChartPointResponse> getDayOfWeekChart(String mode) {
        List<Object[]> rows = isAverageMode(mode)
                ? measurementRepository.averageByDayOfWeek(USAGE_KWH)
                : measurementRepository.sumByDayOfWeek(USAGE_KWH);
        return orderedDayOfWeekChart(rows);
    }

    public List<ChartPointResponse> getCo2Chart(String mode) {
        List<Object[]> rows = isAverageMode(mode)
                ? measurementRepository.averageByDayOfWeek(CO2)
                : measurementRepository.sumByDayOfWeek(CO2);
        return orderedDayOfWeekChart(rows);
    }

    public List<ChartPointResponse> getWeekStatusChart() {
        Map<String, Double> grouped = labelKeyedMap(measurementRepository.sumByWeekStatus(USAGE_KWH));

        return List.of(
                new ChartPointResponse("Laborable", grouped.getOrDefault("Laborable", 0.0)),
                new ChartPointResponse("Fin de semana", grouped.getOrDefault("Fin de semana", 0.0))
        );
    }

    private List<ChartPointResponse> orderedLabelChart(List<Object[]> rows, List<String> order) {
        Map<String, Double> grouped = labelKeyedMap(rows);
        List<ChartPointResponse> result = new ArrayList<>();
        for (String label : order) {
            result.add(new ChartPointResponse(label, grouped.getOrDefault(label, 0.0)));
        }
        return result;
    }

    private List<ChartPointResponse> orderedDayOfWeekChart(List<Object[]> rows) {
        Map<Integer, Double> grouped = numberKeyedMap(rows);
        List<ChartPointResponse> result = new ArrayList<>();
        for (int index = 0; index < DAY_ORDER.size(); index++) {
            int mysqlDayOfWeek = index == 6 ? 1 : index + 2;
            result.add(new ChartPointResponse(DAY_ORDER.get(index), grouped.getOrDefault(mysqlDayOfWeek, 0.0)));
        }
        return result;
    }

    private Map<Integer, Double> numberKeyedMap(List<Object[]> rows) {
        Map<Integer, Double> result = new HashMap<>();
        for (Object[] row : rows) {
            result.put(((Number) row[0]).intValue(), toDouble(row[1]));
        }
        return result;
    }

    private Map<String, Double> labelKeyedMap(List<Object[]> rows) {
        Map<String, Double> result = new HashMap<>();
        for (Object[] row : rows) {
            result.put(String.valueOf(row[0]), toDouble(row[1]));
        }
        return result;
    }

    private boolean isAverageMode(String mode) {
        return "average".equalsIgnoreCase(mode);
    }

    private double nullSafe(Double value) {
        return value != null ? value : 0.0;
    }

    private double toDouble(Object value) {
        return value instanceof Number number ? number.doubleValue() : 0.0;
    }

    private double toDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : 0.0;
    }
}
