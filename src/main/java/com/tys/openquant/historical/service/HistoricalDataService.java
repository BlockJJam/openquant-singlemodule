package com.tys.openquant.historical.service;

import com.tys.openquant.domain.symbol.Symbols;
import com.tys.openquant.historical.dto.HistoricalDataDto;
import com.tys.openquant.historical.exception.HistoricalSymbolException;
import com.tys.openquant.historical.repositories.HistoricalDayDataRepository;
import com.tys.openquant.historical.repositories.HistoricalMinDataRepository;
import com.tys.openquant.historical.repositories.HistoricalSymbolsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j

public class HistoricalDataService {
    private final HistoricalSymbolsRepository historicalSymbolsRepository;
    private final HistoricalDayDataRepository historicalDayDataRepository;
    private final HistoricalMinDataRepository historicalMinDataRepository;


    public HistoricalDataDto.StrategyDayDataPriceListDto getDayDataClosePriceListForStrategy(String symbolCode, LocalDate startDate, LocalDate endDate) {
        getSymbolBySymbolCode(symbolCode);
        return new HistoricalDataDto.StrategyDayDataPriceListDto(historicalDayDataRepository.findDayDataByCodeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDate(symbolCode, startDate, endDate));
    }

    public HistoricalDataDto.StrategyMinDataPriceListDto getMinDataClosePriceListForStrategy(String symbolCode, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        getSymbolBySymbolCode(symbolCode);
        return new HistoricalDataDto.StrategyMinDataPriceListDto(historicalMinDataRepository.findMinDataByCodeAndDateTimeGreaterThanEqualAndDateTimeLessThanEqualOrderByDateTime(symbolCode, startDateTime, endDateTime));

    }

    public HistoricalDataDto.ChartDayDataPriceListDto getDayDataClosePriceListForChart(String symbolCode, LocalDate startDate, LocalDate endDate) {
        getSymbolBySymbolCode(symbolCode);
        return new HistoricalDataDto.ChartDayDataPriceListDto(historicalDayDataRepository.findDayDataByCodeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDate(symbolCode, startDate, endDate));
    }

    public HistoricalDataDto.ChartMinDataPriceListDto getMinDataClosePriceListForChart(String symbolCode, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        getSymbolBySymbolCode(symbolCode);
        return new HistoricalDataDto.ChartMinDataPriceListDto(historicalMinDataRepository.findMinDataByCodeAndDateTimeGreaterThanEqualAndDateTimeLessThanEqualOrderByDateTime(symbolCode, startDateTime, endDateTime));
    }

    public void getSymbolBySymbolCode(String symbolCode) {
        Symbols findSymbol = historicalSymbolsRepository.findSymbolsByCode(symbolCode);
        if (findSymbol == null){
            throw new HistoricalSymbolException("해당 종목은 없습니다");
        }
    }
}
