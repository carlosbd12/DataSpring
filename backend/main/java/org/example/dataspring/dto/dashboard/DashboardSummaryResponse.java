package org.example.dataspring.dto.dashboard;

public class DashboardSummaryResponse {

    private String userDisplayName;
    private String userRoleLabel;

    private long totalMeasurements;
    private double averageConsumption;
    private double minConsumption;
    private double maxConsumption;
    private double totalCo2;
    private double peakConsumption;

    private String datasetInfo;
    private String firstAvailableDate;
    private String lastAvailableDate;

    public DashboardSummaryResponse() {
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserRoleLabel() {
        return userRoleLabel;
    }

    public void setUserRoleLabel(String userRoleLabel) {
        this.userRoleLabel = userRoleLabel;
    }

    public long getTotalMeasurements() {
        return totalMeasurements;
    }

    public void setTotalMeasurements(long totalMeasurements) {
        this.totalMeasurements = totalMeasurements;
    }

    public double getAverageConsumption() {
        return averageConsumption;
    }

    public void setAverageConsumption(double averageConsumption) {
        this.averageConsumption = averageConsumption;
    }

    public double getMinConsumption() {
        return minConsumption;
    }

    public void setMinConsumption(double minConsumption) {
        this.minConsumption = minConsumption;
    }

    public double getMaxConsumption() {
        return maxConsumption;
    }

    public void setMaxConsumption(double maxConsumption) {
        this.maxConsumption = maxConsumption;
    }

    public double getTotalCo2() {
        return totalCo2;
    }

    public void setTotalCo2(double totalCo2) {
        this.totalCo2 = totalCo2;
    }

    public double getPeakConsumption() {
        return peakConsumption;
    }

    public void setPeakConsumption(double peakConsumption) {
        this.peakConsumption = peakConsumption;
    }

    public String getDatasetInfo() {
        return datasetInfo;
    }

    public void setDatasetInfo(String datasetInfo) {
        this.datasetInfo = datasetInfo;
    }

    public String getFirstAvailableDate() {
        return firstAvailableDate;
    }

    public void setFirstAvailableDate(String firstAvailableDate) {
        this.firstAvailableDate = firstAvailableDate;
    }

    public String getLastAvailableDate() {
        return lastAvailableDate;
    }

    public void setLastAvailableDate(String lastAvailableDate) {
        this.lastAvailableDate = lastAvailableDate;
    }
}