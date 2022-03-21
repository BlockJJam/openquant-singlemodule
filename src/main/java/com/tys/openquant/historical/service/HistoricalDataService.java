package com.tys.openquant.historical.service;

import com.tys.openquant.domain.price.DayData;
import com.tys.openquant.historical.dto.HistoricalDataDto;
import com.tys.openquant.historical.repositories.HistoricalDayDataRepository;
import com.tys.openquant.historical.repositories.HistoricalMinDataRepository;
import com.tys.openquant.historical.repositories.HistoricalSymbolsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j

public class HistoricalDataService {
    private final HistoricalSymbolsRepository historicalSymbolsRepository;
    private final HistoricalDayDataRepository historicalDayDataRepository;
    private final HistoricalMinDataRepository historicalMinDataRepository;

    // DTO 를 먼저 만들고, 채워라
    public HistoricalDataDto.StrategyDayDataListDto getDayDataClosePriceListForStrategy(String symbolCode, LocalDate startDate, LocalDate endDate) {
        HistoricalDataDto.StrategyDayDataListDto strategyDayDataListDto = toStrategyDayDataListDto(symbolCode, startDate, endDate);

        return strategyDayDataListDto;
    }


//    public HistoricalDataDto.StrategyMinDataListDto getMinDataClosePriceListForStrategy(String symbolCode, LocalDateTime startDateTime, LocalDateTime endDateTime) {
//
//        return new HistoricalDataDto.StrategyMinDataListDto(historicalMinDataRepository.findMinDataByCodeAndDateTimeGreaterThanEqualAndDateTimeLessThanEqualOrderByDateTime(symbolCode, startDateTime, endDateTime));
//
//    }
//
//    public HistoricalDataDto.ChartDayDataListDto getDayDataClosePriceListForChart(String symbolCode, LocalDate startDate, LocalDate endDate) {
//
//        return new HistoricalDataDto.ChartDayDataListDto(historicalDayDataRepository.findDayDataByCodeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDate(symbolCode, startDate, endDate));
//    }
//
//    public HistoricalDataDto.ChartMinDataListDto getMinDataClosePriceListForChart(String symbolCode, LocalDateTime startDateTime, LocalDateTime endDateTime) {
//
//        return new HistoricalDataDto.ChartMinDataListDto(historicalMinDataRepository.findMinDataByCodeAndDateTimeGreaterThanEqualAndDateTimeLessThanEqualOrderByDateTime(symbolCode, startDateTime, endDateTime));
//    }


    protected HistoricalDataDto.StrategyDayDataListDto toStrategyDayDataListDto(String symbolCode, LocalDate startDate, LocalDate endDate) {
        List<DayData> dayDataList = historicalDayDataRepository.findDayDataByCodeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDate(symbolCode, startDate, endDate);
        boolean hasSymbolCode = hasSymbol(symbolCode);
        boolean allowedDataRange = isAllowedDataRange(startDate, endDate);

        HistoricalDataDto.StrategyDayDataListDto strategyDayDataListDto = new HistoricalDataDto.StrategyDayDataListDto(
                dayDataList, hasSymbolCode, allowedDataRange);

        return strategyDayDataListDto;
    }

    protected boolean hasSymbol(String symbolCode) {
        if (historicalSymbolsRepository.findSymbolsByCode(symbolCode) == null) {
            return false;
        }
        return true;
    }

    protected boolean isAllowedDataRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate) || startDate.isEqual(endDate) ) {
            return false;
        }
        return true;
    }

    protected boolean isAllowedDataRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime.isAfter(endDateTime) || startDateTime.isEqual(endDateTime) ) {
            return false;
        }
        return true;
    }
}
