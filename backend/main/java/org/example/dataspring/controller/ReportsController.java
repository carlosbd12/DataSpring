package org.example.dataspring.controller;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.example.dataspring.dto.reports.ReportResponse;
import org.example.dataspring.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final ReportService reportService;

    public ReportsController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public List<ReportResponse> getReports() {
        return reportService.getGeneratedReports();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReportById(@PathVariable Long id) {
        ReportResponse report = reportService.getReportById(id);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    @PostMapping("/generate/daily")
    public ReportResponse generateDaily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return reportService.generateDailyReport(date);
    }

    @PostMapping("/generate/weekly")
    public ReportResponse generateWeekly() {
        return reportService.generateWeeklyReport();
    }

    @PostMapping("/generate/monthly")
    public ReportResponse generateMonthly() {
        return reportService.generateMonthlyReport();
    }

    @PostMapping("/generate/efficiency")
    public ReportResponse generateEfficiency() {
        return reportService.generateEfficiencyReport();
    }

    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Long id) {
        ReportResponse report = reportService.getReportById(id);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfBytes = buildPdfBytes(report);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    private byte[] buildPdfBytes(ReportResponse report) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);

            document.open();
            document.add(new Paragraph(report.getTitle()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Tipo: " + report.getType()));
            document.add(new Paragraph("Período: " + report.getPeriod()));
            document.add(new Paragraph("Generado: " + report.getGeneratedAt()));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Resumen:"));
            document.add(new Paragraph("Registros: " + (report.getContent() != null ? report.getContent() : "")));
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el PDF del informe", e);
        }
    }
}