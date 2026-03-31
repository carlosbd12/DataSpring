package org.example.dataspring.service;

import jakarta.annotation.PostConstruct;
import org.example.dataspring.model.MeasurementRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class CsvEnergyDataService {

    private static final String DEFAULT_FILE = "data/steel_industry_data.csv";
    private static final DateTimeFormatter CSV_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final List<MeasurementRecord> cachedRecords = new ArrayList<>();
    private String currentFileName = "steel_industry_data.csv";

    @PostConstruct
    public void init() {
        loadDefaultCsv();
    }

    public List<MeasurementRecord> getRecords() {
        return List.copyOf(cachedRecords);
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    private void loadDefaultCsv() {
        try {
            ClassPathResource resource = new ClassPathResource(DEFAULT_FILE);

            if (!resource.exists()) {
                throw new IllegalStateException("No se encontró el archivo CSV en el classpath: " + DEFAULT_FILE);
            }

            cachedRecords.clear();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line = reader.readLine(); // header
                if (line == null) {
                    throw new IllegalStateException("El CSV está vacío");
                }

                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }

                    MeasurementRecord record = parseLine(line);
                    if (record != null) {
                        cachedRecords.add(record);
                    }
                }
            }

            if (cachedRecords.isEmpty()) {
                throw new IllegalStateException("No se cargaron registros del CSV");
            }

        } catch (Exception ex) {
            throw new IllegalStateException("Error cargando el CSV del dashboard: " + ex.getMessage(), ex);
        }
    }

    private MeasurementRecord parseLine(String line) {
        String[] parts = line.split(",", -1);

        if (parts.length < 11) {
            return null;
        }

        try {
            LocalDateTime timestamp = LocalDateTime.parse(parts[0].trim(), CSV_FORMATTER);
            double usageKwh = parseDouble(parts[1]);
            double co2Ton = parseDouble(parts[4]);
            double co2Kg = co2Ton * 1000.0;

            String weekStatus = parts[8].trim();
            String dayOfWeek = normalizeDay(parts[9].trim());
            String loadType = normalizeLoadType(parts[10].trim());
            int hour = timestamp.getHour();

            return new MeasurementRecord(
                    timestamp,
                    usageKwh,
                    co2Kg,
                    weekStatus,
                    dayOfWeek,
                    loadType,
                    hour
            );
        } catch (Exception ex) {
            return null;
        }
    }

    private double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return 0.0;
        }
        return Double.parseDouble(value.trim());
    }

    private String normalizeDay(String day) {
        return switch (day.toLowerCase(Locale.ROOT)) {
            case "monday" -> "Lunes";
            case "tuesday" -> "Martes";
            case "wednesday" -> "Miércoles";
            case "thursday" -> "Jueves";
            case "friday" -> "Viernes";
            case "saturday" -> "Sábado";
            case "sunday" -> "Domingo";
            default -> day;
        };
    }

    private String normalizeLoadType(String loadType) {
        return switch (loadType) {
            case "Light_Load" -> "Light Load";
            case "Medium_Load" -> "Medium Load";
            case "Maximum_Load" -> "Maximum Load";
            default -> loadType;
        };
    }
}