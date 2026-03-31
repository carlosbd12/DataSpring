package org.example.dataspring.dto.dashboard;

public class ChartPointResponse {

    private String label;
    private double value;

    public ChartPointResponse() {
    }

    public ChartPointResponse(String label, double value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public double getValue() {
        return value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(double value) {
        this.value = value;
    }
}