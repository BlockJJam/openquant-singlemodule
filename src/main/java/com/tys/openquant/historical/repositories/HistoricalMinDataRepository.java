package com.tys.openquant.historical.repositories;

import com.tys.openquant.domain.price.DayData;
import com.tys.openquant.domain.price.MinData;
import com.tys.openquant.domain.price.compositekey.MinDataId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoricalMinDataRepository extends JpaRepository<MinData, MinDataId>{

    List<MinData> findMinDataByCodeAndDateTimeGreaterThanEqualAndDateTimeLessThanEqualOrderByDateTime(String symbolCode, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
