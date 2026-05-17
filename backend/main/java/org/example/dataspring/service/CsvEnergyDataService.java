package org.example.dataspring.service;

import jakarta.annotation.PostConstruct;
import org.example.dataspring.entity.EnergyMeasurement;
import org.example.dataspring.model.MeasurementRecord;
import org.example.dataspring.repository.EnergyMeasurementRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final EnergyMeasurementRepository energyMeasurementRepository;
    private final List<MeasurementRecord> cachedRecords = new ArrayList<>();
    private String currentFileName = "steel_industry_data.csv";

    public CsvEnergyDataService(EnergyMeasurementRepository energyMeasurementRepository) {
        this.energyMeasurementRepository = energyMeasurementRepository;
    }

    @PostConstruct
    public void init() {
        loadDataFromDatabaseOrDefaultCsv();
    }

    public List<MeasurementRecord> getRecords() {
        return List.copyOf(cachedRecords);
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    public void updateDataset(MultipartFile file) throws IOException {
        loadCsvFromStream(file.getInputStream(), file.getOriginalFilename(), true);
    }

    private void loadDataFromDatabaseOrDefaultCsv() {
        List<MeasurementRecord> databaseRecords = loadRecordsFromDatabase();
        if (!databaseRecords.isEmpty()) {
            synchronized (cachedRecords) {
                cachedRecords.clear();
                cachedRecords.addAll(databaseRecords);
                currentFileName = "mysql:mediciones_energia";
            }
            return;
        }

        loadDefaultCsv();
    }

    private void loadDefaultCsv() {
        try {
            ClassPathResource resource = new ClassPathResource(DEFAULT_FILE);
            if (!resource.exists()) {
                throw new IllegalStateException("No se encontró el archivo CSV en el classpath: " + DEFAULT_FILE);
            }
            loadCsvFromStream(resource.getInputStream(), "steel_industry_data.csv", true);
        } catch (Exception ex) {
            throw new IllegalStateException("Error cargando el CSV del dashboard: " + ex.getMessage(), ex);
        }
    }

    private void loadCsvFromStream(InputStream inputStream, String fileName, boolean persistInDatabase) throws IOException {
        List<MeasurementRecord> newRecords = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("El CSV está vacío");
            }

            String[] headers = headerLine.split(",");
            int[] mapping = findMapping(headers);

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                MeasurementRecord record = parseLineWithMapping(line, mapping);
                if (record != null) {
                    newRecords.add(record);
                }
            }
        }

        if (newRecords.isEmpty()) {
            throw new IOException("No se cargaron registros válidos del CSV");
        }

        if (persistInDatabase) {
            replaceDatasetInDatabase(newRecords);
        }

        synchronized (cachedRecords) {
            cachedRecords.clear();
            cachedRecords.addAll(newRecords);
            this.currentFileName = fileName;
        }
    }

    private List<MeasurementRecord> loadRecordsFromDatabase() {
        return energyMeasurementRepository.findAllByOrderByTimestampAsc()
                .stream()
                .map(this::toMeasurementRecord)
                .toList();
    }

    @Transactional
    protected void replaceDatasetInDatabase(List<MeasurementRecord> records) {
        energyMeasurementRepository.deleteAllInBatch();
        energyMeasurementRepository.saveAll(records.stream().map(this::toEntity).toList());
    }

    private int[] findMapping(String[] headers) {
        // [0]: timestamp, [1]: usage, [2]: co2, [3]: weekStatus, [4]: dayOfWeek, [5]: loadType
        int[] mapping = {-1, -1, -1, -1, -1, -1};
        for (int i = 0; i < headers.length; i++) {
            String h = headers[i].toLowerCase().trim().replace("\"", "");
            if (h.contains("date") || h.contains("time") || h.equals("timestamp")) mapping[0] = i;
            else if (h.contains("usage") || h.contains("kwh") || h.contains("consumo")) mapping[1] = i;
            else if (h.contains("co2") || h.contains("emissions")) mapping[2] = i;
            else if (h.contains("weekstatus") || h.contains("week_status")) mapping[3] = i;
            else if (h.contains("day") || h.contains("dia")) mapping[4] = i;
            else if (h.contains("load") || h.contains("carga")) mapping[5] = i;
        }
        return mapping;
    }

    private MeasurementRecord parseLineWithMapping(String line, int[] mapping) {
        String[] parts = line.split(",", -1);
        try {
            LocalDateTime timestamp = null;
            if (mapping[0] != -1) {
                String dateStr = parts[mapping[0]].trim().replace("\"", "");
                try {
                    timestamp = LocalDateTime.parse(dateStr, CSV_FORMATTER);
                } catch (Exception e) {
                    timestamp = LocalDateTime.parse(dateStr, ISO_FORMATTER);
                }
            } else {
                return null;
            }

            double usage = mapping[1] != -1 ? parseDouble(parts[mapping[1]]) : 0.0;
            double co2 = mapping[2] != -1 ? parseDouble(parts[mapping[2]]) : 0.0;
            // Estandarización de CO2: si parece estar en toneladas (valor pequeño), convertir a kg
            if (co2 < 1.0 && co2 > 0) co2 *= 1000.0;

            String weekStatus = mapping[3] != -1 ? parts[mapping[3]].trim() : "Unknown";
            String dayOfWeek = mapping[4] != -1 ? normalizeDay(parts[mapping[4]].trim()) : normalizeDay(timestamp.getDayOfWeek().name());
            String loadType = mapping[5] != -1 ? normalizeLoadType(parts[mapping[5]].trim()) : "Unknown";

            return new MeasurementRecord(timestamp, usage, co2, weekStatus, dayOfWeek, loadType, timestamp.getHour());
        } catch (Exception e) {
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

    private EnergyMeasurement toEntity(MeasurementRecord record) {
        EnergyMeasurement entity = new EnergyMeasurement();
        entity.setTimestamp(record.getTimestamp());
        entity.setUsageKwh(record.getUsageKwh());
        entity.setCo2(record.getCo2());
        entity.setWeekStatus(record.getWeekStatus());
        entity.setDayOfWeek(record.getDayOfWeek());
        entity.setLoadType(record.getLoadType());
        entity.setHour(record.getHour());
        return entity;
    }

    private MeasurementRecord toMeasurementRecord(EnergyMeasurement entity) {
        return new MeasurementRecord(
                entity.getTimestamp(),
                entity.getUsageKwh(),
                entity.getCo2(),
                entity.getWeekStatus(),
                entity.getDayOfWeek(),
                entity.getLoadType(),
                entity.getHour()
        );
    }
}
