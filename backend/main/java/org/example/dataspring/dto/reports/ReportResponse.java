package org.example.dataspring.dto.reports;

import java.time.LocalDateTime;

public class ReportResponse {
    private Long id;
    private String title;
    private String type;
    private String period;
    private LocalDateTime generatedAt;
    private String content;
    private Double totalConsumption;
    private Double averageConsumption;
    private Double totalCo2;
    private String extraSummary;

    public ReportResponse() {
    }

    public Long getId() {
        return id;
    }

    public ReportResponse setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ReportResponse setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getType() {
        return type;
    }

    public ReportResponse setType(String type) {
        this.type = type;
        return this;
    }

    public String getPeriod() {
        return period;
    }

    public ReportResponse setPeriod(String period) {
        this.period = period;
        return this;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public ReportResponse setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ReportResponse setContent(String content) {
        this.content = content;
        return this;
    }

    public Double getTotalConsumption() {
        return totalConsumption;
    }

    public ReportResponse setTotalConsumption(Double totalConsumption) {
        this.totalConsumption = totalConsumption;
        return this;
    }

    public Double getAverageConsumption() {
        return averageConsumption;
    }

    public ReportResponse setAverageConsumption(Double averageConsumption) {
        this.averageConsumption = averageConsumption;
        return this;
    }

    public Double getTotalCo2() {
        return totalCo2;
    }

    public ReportResponse setTotalCo2(Double totalCo2) {
        this.totalCo2 = totalCo2;
        return this;
    }

    public String getExtraSummary() {
        return extraSummary;
    }

    public ReportResponse setExtraSummary(String extraSummary) {
        this.extraSummary = extraSummary;
        return this;
    }
}