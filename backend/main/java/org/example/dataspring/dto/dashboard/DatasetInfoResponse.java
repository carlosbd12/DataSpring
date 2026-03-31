package org.example.dataspring.dto.dashboard;

public class DatasetInfoResponse {

    private String fileName;
    private long totalMeasurements;
    private String firstAvailableDate;
    private String lastAvailableDate;

    public DatasetInfoResponse() {
    }

    public String getFileName() {
        return fileName;
    }

    public long getTotalMeasurements() {
        return totalMeasurements;
    }

    public String getFirstAvailableDate() {
        return firstAvailableDate;
    }

    public String getLastAvailableDate() {
        return lastAvailableDate;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setTotalMeasurements(long totalMeasurements) {
        this.totalMeasurements = totalMeasurements;
    }

    public void setFirstAvailableDate(String firstAvailableDate) {
        this.firstAvailableDate = firstAvailableDate;
    }

    public void setLastAvailableDate(String lastAvailableDate) {
        this.lastAvailableDate = lastAvailableDate;
    }
}