package org.example.dataspring.repository;

import org.example.dataspring.entity.EnergyMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnergyMeasurementRepository extends JpaRepository<EnergyMeasurement, Long> {

    List<EnergyMeasurement> findAllByOrderByTimestampAsc();
}
