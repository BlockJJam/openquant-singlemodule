package com.tys.openquant.historical.repositories;

import com.tys.openquant.domain.price.DayData;
import com.tys.openquant.domain.price.compositekey.DayDataId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HistoricalDayDataRepository extends JpaRepository<DayData, DayDataId> {
    List<DayData> findDayDataByCodeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDate(String symbolCode, LocalDate startDate, LocalDate endDate);
}
