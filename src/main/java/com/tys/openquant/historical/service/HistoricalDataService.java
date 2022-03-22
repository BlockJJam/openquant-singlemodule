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
import java.time.chrono.ChronoLocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j

public class HistoricalDataService {
    private final HistoricalSymbolsRepository historicalSymbolsRepository;
    private final HistoricalDayDataRepository historicalDayDataRepository;
    private final HistoricalMinDataRepository historicalMinDataRepository;

    /**
     * 전략 시뮬레이션 목적의 과거 특정 기간 일봉 데이터를 DTO 로 변환해서 반환하는 서비스
     * 해당 코드의 종목 존재 여부 및 기간 입력 유효성 검사 결과까지 리턴.
     * @param symbolCode: 시세를 요청하는 종목 코드
     * @param startDate: 시작 날짜
     * @param endDate: 종료 날짜
     * @return HistoricalDataDto.StrategyDayDataListDto
     * @author kyung.kim
     */
    public HistoricalDataDto.StrategyDayDataListDto getDayDataClosePriceListForStrategy(String symbolCode, LocalDate startDate, LocalDate endDate) {
        HistoricalDataDto.StrategyDayDataListDto strategyDayDataListDto = new HistoricalDataDto.StrategyDayDataListDto();

        strategyDayDataListDto.fillAuthForHistoricalData(
                historicalSymbolsRepository.findByCode(symbolCode).isPresent(),
                isOrderlyRightStartAndEndTime(startDate, endDate)
        );

        //@TODO 아래 메서드는 service 로 옮길것
        if (hasAuthForReservingHistoricalData(strategyDayDataListDto.isHasSymbolCode(), strategyDayDataListDto.isAllowedDateRange())) {
            strategyDayDataListDto.toStrategyDayDataListDto(
                    historicalDayDataRepository.findDayDataByCodeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDate(symbolCode, startDate, endDate)
            );
        }

        return strategyDayDataListDto;
    }

    /**
     * 전략 시뮬레이션 목적의 과거 특정 기간 분봉 데이터를 DTO 로 변환해서 반환하는 서비스
     * 해당 코드의 종목 존재 여부 및 기간 입력 유효성 검사 결과까지 리턴.
     * @param symbolCode: 시세를 요청하는 종목 코드
     * @param startDateTime: 시작 날짜-시간, iso format
     * @param endDateTime: 종료 날짜-시간, iso format
     * @return HistoricalDataDto.StrategyMinDataListDto
     * @author kyung.kim
     */
    public HistoricalDataDto.StrategyMinDataListDto getMinDataClosePriceListForStrategy(String symbolCode, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        HistoricalDataDto.StrategyMinDataListDto strategyMinDataListDto = new HistoricalDataDto.StrategyMinDataListDto();

        strategyMinDataListDto.fillAuthForHistoricalData(
                historicalSymbolsRepository.findByCode(symbolCode).isPresent(),
                isOrderlyRightStartAndEndTime(startDateTime, endDateTime)
        );

        if (hasAuthForReservingHistoricalData(strategyMinDataListDto.isHasSymbolCode(), strategyMinDataListDto.isAllowedDateRange())) {
            strategyMinDataListDto.toStrategyMinDataListDto(
                    historicalMinDataRepository.findMinDataByCodeAndDateTimeGreaterThanEqualAndDateTimeLessThanEqualOrderByDateTime(symbolCode, startDateTime, endDateTime)
            );
        }
        return strategyMinDataListDto;
    }

    /**
     * 차트 플로팅 목적의 과거 특정 기간 일봉 데이터를 DTO 로 변환해서 반환하는 서비스
     * 해당 코드의 종목 존재 여부 및 기간 입력 유효성 검사 결과까지 리턴.
     * @param symbolCode: 시세를 요청하는 종목 코드
     * @param startDate: 시작 날짜
     * @param endDate: 종료 날짜
     * @return HistoricalDataDto.ChartDayDataListDto
     * @author kyung.kim
     */
    public HistoricalDataDto.ChartDayDataListDto getDayDataClosePriceListForChart(String symbolCode, LocalDate startDate, LocalDate endDate) {
        HistoricalDataDto.ChartDayDataListDto chartDayDataListDto = new HistoricalDataDto.ChartDayDataListDto();

        chartDayDataListDto.fillAuthForHistoricalData(
                historicalSymbolsRepository.findByCode(symbolCode).isPresent(),
                isOrderlyRightStartAndEndTime(startDate, endDate)
        );

        if (hasAuthForReservingHistoricalData(chartDayDataListDto.isHasSymbolCode(), chartDayDataListDto.isAllowedDateRange())) {
            chartDayDataListDto.toStrategyMinDataListDto(
                    historicalDayDataRepository.findDayDataByCodeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDate(symbolCode, startDate, endDate)
            );
        }
        return chartDayDataListDto;
    }

    /**
     * 차트 플로팅 목적의 과거 특정 기간 분봉 데이터를 DTO 로 변환해서 반환하는 서비스
     * 해당 코드의 종목 존재 여부 및 기간 입력 유효성 검사 결과까지 리턴.
     * @param symbolCode: 시세를 요청하는 종목 코드
     * @param startDateTime: 시작 날짜-시간, iso format
     * @param endDateTime: 종료 날짜-시간, iso format
     * @return HistoricalDataDto.ChartMinDataListDto
     * @author kyung.kim
     */
    public HistoricalDataDto.ChartMinDataListDto getMinDataClosePriceListForChart(String symbolCode, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        HistoricalDataDto.ChartMinDataListDto chartMinDataListDto = new HistoricalDataDto.ChartMinDataListDto();

        chartMinDataListDto.fillAuthForHistoricalData(
                historicalSymbolsRepository.findByCode(symbolCode).isPresent(),
                isOrderlyRightStartAndEndTime(startDateTime, endDateTime)
        );

        if (hasAuthForReservingHistoricalData(chartMinDataListDto.isHasSymbolCode(), chartMinDataListDto.isAllowedDateRange())) {
            chartMinDataListDto.toStrategyMinDataListDto(
                    historicalMinDataRepository.findMinDataByCodeAndDateTimeGreaterThanEqualAndDateTimeLessThanEqualOrderByDateTime(symbolCode, startDateTime, endDateTime)
            );
        }
        return chartMinDataListDto;
    }

    //@TODO 서비스로 옮기기
    /**
     * 해당 DTD 의 종목 코드 존재 여부 및 사용자 입력 시간 순서 유효성의 AND 연산 결과를 리턴하는 메서드
     * @param allowedDateRange
     * @param hasSymbolCode
     * @return boolean
     */
    public boolean hasAuthForReservingHistoricalData(boolean allowedDateRange, boolean hasSymbolCode) {
        return allowedDateRange && hasSymbolCode;
    }

    /**
     * 사용자가 입력한 날짜 형식의 startDate, endDate 의 전후 순서를 확인하는 메서드
     * @param startDate: 시작 날짜
     * @param endDate: 종료 날짜
     * @return boolean
     * @author kyung.kim
     */
    protected boolean isOrderlyRightStartAndEndTime(LocalDate startDate, LocalDate endDate) {
        return !(startDate.isAfter(endDate) || startDate.isEqual(endDate));
    }

    /**
     * 사용자가 입력한 날짜-시간 형식의 startDate, endDate 의 전후 순서를 확인하는 메서드
     * @param startDateTime: 시작 날짜-시간, iso format
     * @param endDateTime: 종료 날짜-시간, iso format
     * @return boolean
     * @author kyung.kim
     */
    protected boolean isOrderlyRightStartAndEndTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return !(startDateTime.isAfter(endDateTime) || startDateTime.isEqual(endDateTime));
    }
}
