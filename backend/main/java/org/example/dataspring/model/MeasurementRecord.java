package org.example.dataspring.model;

import java.time.LocalDateTime;

public class MeasurementRecord {

    private LocalDateTime timestamp;
    private double usageKwh;
    private double co2;
    private String weekStatus;
    private String dayOfWeek;
    private String loadType;
    private int hour;

    public MeasurementRecord() {
    }

    public MeasurementRecord(LocalDateTime timestamp,
                             double usageKwh,
                             double co2,
                             String weekStatus,
                             String dayOfWeek,
                             String loadType,
                             int hour) {
        this.timestamp = timestamp;
        this.usageKwh = usageKwh;
        this.co2 = co2;
        this.weekStatus = weekStatus;
        this.dayOfWeek = dayOfWeek;
        this.loadType = loadType;
        this.hour = hour;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getUsageKwh() {
        return usageKwh;
    }

    public double getCo2() {
        return co2;
    }

    public String getWeekStatus() {
        return weekStatus;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getLoadType() {
        return loadType;
    }

    public int getHour() {
        return hour;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setUsageKwh(double usageKwh) {
        this.usageKwh = usageKwh;
    }

    public void setCo2(double co2) {
        this.co2 = co2;
    }

    public void setWeekStatus(String weekStatus) {
        this.weekStatus = weekStatus;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
}