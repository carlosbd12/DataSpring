package org.example.dataspring.repository;

import org.example.dataspring.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    @Query("""
            select count(m)
            from Measurement m
            where m.measurementType.code = :code
            """)
    long countByTypeCode(@Param("code") String code);

    @Query("""
            select coalesce(sum(m.value), 0)
            from Measurement m
            where m.measurementType.code = :code
            """)
    BigDecimal sumByTypeCode(@Param("code") String code);

    @Query("""
            select coalesce(avg(m.value), 0)
            from Measurement m
            where m.measurementType.code = :code
            """)
    Double averageByTypeCode(@Param("code") String code);

    @Query("""
            select coalesce(min(m.value), 0)
            from Measurement m
            where m.measurementType.code = :code
            """)
    BigDecimal minByTypeCode(@Param("code") String code);

    @Query("""
            select coalesce(max(m.value), 0)
            from Measurement m
            where m.measurementType.code = :code
            """)
    BigDecimal maxByTypeCode(@Param("code") String code);

    @Query("""
            select min(m.timestamp)
            from Measurement m
            """)
    LocalDateTime findFirstTimestamp();

    @Query("""
            select max(m.timestamp)
            from Measurement m
            """)
    LocalDateTime findLastTimestamp();

    @Query("""
            select hour(m.timestamp), coalesce(sum(m.value), 0)
            from Measurement m
            where m.measurementType.code = :code
            group by hour(m.timestamp)
            order by hour(m.timestamp)
            """)
    List<Object[]> sumByHour(@Param("code") String code);

    @Query("""
            select hour(m.timestamp), coalesce(sum(m.value), 0)
            from Measurement m
            where m.measurementType.code = :code
              and m.timestamp >= :from
              and m.timestamp < :to
            group by hour(m.timestamp)
            order by hour(m.timestamp)
            """)
    List<Object[]> sumByHourAndTimestampBetween(
            @Param("code") String code,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
            select function('dayofweek', m.timestamp), coalesce(sum(m.value), 0)
            from Measurement m
            where m.measurementType.code = :code
            group by function('dayofweek', m.timestamp)
            order by function('dayofweek', m.timestamp)
            """)
    List<Object[]> sumByDayOfWeek(@Param("code") String code);

    @Query("""
            select function('dayofweek', m.timestamp), coalesce(avg(m.value), 0)
            from Measurement m
            where m.measurementType.code = :code
            group by function('dayofweek', m.timestamp)
            order by function('dayofweek', m.timestamp)
            """)
    List<Object[]> averageByDayOfWeek(@Param("code") String code);

    @Query("""
            select
                case
                    when function('dayofweek', m.timestamp) in (1, 7) then 'Fin de semana'
                    else 'Laborable'
                end,
                coalesce(sum(m.value), 0)
            from Measurement m
            where m.measurementType.code = :code
            group by
                case
                    when function('dayofweek', m.timestamp) in (1, 7) then 'Fin de semana'
                    else 'Laborable'
                end
            """)
    List<Object[]> sumByWeekStatus(@Param("code") String code);

    @Query("""
            select
                case
                    when m.value < 3.5 then 'Light Load'
                    when m.value < 4.5 then 'Medium Load'
                    else 'Maximum Load'
                end,
                coalesce(sum(m.value), 0)
            from Measurement m
            where m.measurementType.code = :code
            group by
                case
                    when m.value < 3.5 then 'Light Load'
                    when m.value < 4.5 then 'Medium Load'
                    else 'Maximum Load'
                end
            """)
    List<Object[]> sumByEstimatedLoadType(@Param("code") String code);

    @Query("""
            select
                case
                    when m.value < 3.5 then 'Light Load'
                    when m.value < 4.5 then 'Medium Load'
                    else 'Maximum Load'
                end,
                coalesce(avg(m.value), 0)
            from Measurement m
            where m.measurementType.code = :code
            group by
                case
                    when m.value < 3.5 then 'Light Load'
                    when m.value < 4.5 then 'Medium Load'
                    else 'Maximum Load'
                end
            """)
    List<Object[]> averageByEstimatedLoadType(@Param("code") String code);
}
