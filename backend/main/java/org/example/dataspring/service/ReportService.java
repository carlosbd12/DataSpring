package org.example.dataspring.service;

import org.example.dataspring.dto.reports.ReportResponse;
import org.example.dataspring.model.MeasurementRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReportService {

    private final CsvEnergyDataService csvEnergyDataService;
    private final AtomicLong sequence = new AtomicLong(1);
    private final List<ReportResponse> generatedReports = new ArrayList<>();

    public ReportService(CsvEnergyDataService csvEnergyDataService) {
        this.csvEnergyDataService = csvEnergyDataService;
    }

    public List<ReportResponse> getGeneratedReports() {
        if (generatedReports.isEmpty()) {
            rebuildDefaultReports();
        }
        return List.copyOf(generatedReports);
    }

    public ReportResponse getReportById(Long id) {
        return generatedReports.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public ReportResponse generateDailyReport(LocalDate date) {
        ReportResponse report = buildDailyReport(date);
        generatedReports.add(report);
        return report;
    }

    public ReportResponse generateWeeklyReport() {
        ReportResponse report = buildWeeklyReport();
        generatedReports.add(report);
        return report;
    }

    public ReportResponse generateMonthlyReport() {
        ReportResponse report = buildMonthlyReport();
        generatedReports.add(report);
        return report;
    }

    public ReportResponse generateEfficiencyReport() {
        ReportResponse report = buildEfficiencyReport();
        generatedReports.add(report);
        return report;
    }

    private void rebuildDefaultReports() {
        generatedReports.clear();

        List<MeasurementRecord> records = csvEnergyDataService.getRecords();
        if (records.isEmpty()) {
            return;
        }

        LocalDate latestDay = records.stream()
                .map(r -> r.getTimestamp().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        generatedReports.add(buildDailyReport(latestDay));
        generatedReports.add(buildWeeklyReport());
        generatedReports.add(buildMonthlyReport());
        generatedReports.add(buildEfficiencyReport());
    }

    private ReportResponse buildDailyReport(LocalDate date) {
        List<MeasurementRecord> filtered = csvEnergyDataService.getRecords().stream()
                .filter(r -> r.getTimestamp().toLocalDate().equals(date))
                .toList();

        double total = filtered.stream().mapToDouble(MeasurementRecord::getUsageKwh).sum();
        double avg = filtered.isEmpty() ? 0 : total / filtered.size();
        double co2 = filtered.stream().mapToDouble(MeasurementRecord::getCo2).sum();

        return baseReport(
                "Informe diario",
                "DAILY",
                date.toString(),
                filtered,
                total,
                avg,
                co2,
                "Registros del día: " + filtered.size()
        );
    }

    private ReportResponse buildWeeklyReport() {
        List<MeasurementRecord> records = csvEnergyDataService.getRecords();
        LocalDate maxDate = records.stream()
                .map(r -> r.getTimestamp().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        LocalDate from = maxDate.minusDays(6);

        List<MeasurementRecord> filtered = records.stream()
                .filter(r -> {
                    LocalDate d = r.getTimestamp().toLocalDate();
                    return !d.isBefore(from) && !d.isAfter(maxDate);
                })
                .toList();

        double total = filtered.stream().mapToDouble(MeasurementRecord::getUsageKwh).sum();
        double avg = filtered.isEmpty() ? 0 : total / filtered.size();
        double co2 = filtered.stream().mapToDouble(MeasurementRecord::getCo2).sum();

        return baseReport(
                "Informe semanal",
                "WEEKLY",
                from + " / " + maxDate,
                filtered,
                total,
                avg,
                co2,
                "Ventana analizada: últimos 7 días"
        );
    }

    private ReportResponse buildMonthlyReport() {
        List<MeasurementRecord> records = csvEnergyDataService.getRecords();
        LocalDate maxDate = records.stream()
                .map(r -> r.getTimestamp().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        LocalDate from = maxDate.withDayOfMonth(1);

        List<MeasurementRecord> filtered = records.stream()
                .filter(r -> {
                    LocalDate d = r.getTimestamp().toLocalDate();
                    return !d.isBefore(from) && !d.isAfter(maxDate);
                })
                .toList();

        double total = filtered.stream().mapToDouble(MeasurementRecord::getUsageKwh).sum();
        double avg = filtered.isEmpty() ? 0 : total / filtered.size();
        double co2 = filtered.stream().mapToDouble(MeasurementRecord::getCo2).sum();

        return baseReport(
                "Informe mensual",
                "MONTHLY",
                from.getMonth() + " " + from.getYear(),
                filtered,
                total,
                avg,
                co2,
                "Días analizados: " + filtered.stream()
                        .map(r -> r.getTimestamp().toLocalDate())
                        .distinct()
                        .count()
        );
    }

    private ReportResponse buildEfficiencyReport() {
        List<MeasurementRecord> records = csvEnergyDataService.getRecords();

        double total = records.stream().mapToDouble(MeasurementRecord::getUsageKwh).sum();
        double avg = records.isEmpty() ? 0 : total / records.size();
        double co2 = records.stream().mapToDouble(MeasurementRecord::getCo2).sum();

        MeasurementRecord best = records.stream()
                .min(Comparator.comparingDouble(MeasurementRecord::getUsageKwh))
                .orElse(null);

        String extra = best != null
                ? "Mejor registro: " + best.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                + " con " + round(best.getUsageKwh()) + " kWh"
                : "Sin registros disponibles";

        return baseReport(
                "Informe de eficiencia",
                "EFFICIENCY",
                "Global",
                records,
                total,
                avg,
                co2,
                extra
        );
    }

    private ReportResponse baseReport(
            String title,
            String type,
            String period,
            List<MeasurementRecord> records,
            double total,
            double avg,
            double co2,
            String extraSummary
    ) {
        Long id = sequence.getAndIncrement();

        StringBuilder content = new StringBuilder();
        content.append(title).append("\n");
        content.append("Período: ").append(period).append("\n");
        content.append("Generado: ").append(LocalDateTime.now()).append("\n\n");
        content.append("Resumen:\n");
        content.append("- Registros: ").append(records.size()).append("\n");
        content.append("- Consumo total: ").append(round(total)).append(" kWh\n");
        content.append("- Consumo promedio: ").append(round(avg)).append(" kWh\n");
        content.append("- CO₂ total: ").append(round(co2)).append(" kg\n");
        content.append("- ").append(extraSummary).append("\n");

        return new ReportResponse()
                .setId(id)
                .setTitle(title)
                .setType(type)
                .setPeriod(period)
                .setGeneratedAt(LocalDateTime.now())
                .setContent(content.toString())
                .setTotalConsumption(total)
                .setAverageConsumption(avg)
                .setTotalCo2(co2)
                .setExtraSummary(extraSummary);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}