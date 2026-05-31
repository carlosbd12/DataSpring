package org.example.dataspring.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mediciones_energia")
public class EnergyMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "consumo_kwh", nullable = false)
    private double usageKwh;

    @Column(name = "co2_kg", nullable = false)
    private double co2;

    @Column(name = "estado_semana", nullable = false, length = 30)
    private String weekStatus;

    @Column(name = "dia_semana", nullable = false, length = 20)
    private String dayOfWeek;

    @Column(name = "tipo_carga", nullable = false, length = 30)
    private String loadType;

    @Column(name = "hora", nullable = false)
    private int hour;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getUsageKwh() {
        return usageKwh;
    }

    public void setUsageKwh(double usageKwh) {
        this.usageKwh = usageKwh;
    }

    public double getCo2() {
        return co2;
    }

    public void setCo2(double co2) {
        this.co2 = co2;
    }

    public String getWeekStatus() {
        return weekStatus;
    }

    public void setWeekStatus(String weekStatus) {
        this.weekStatus = weekStatus;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getLoadType() {
        return loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
}
