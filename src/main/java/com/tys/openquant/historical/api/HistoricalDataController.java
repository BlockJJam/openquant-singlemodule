package com.tys.openquant.historical.api;

import com.tys.openquant.historical.dto.HistoricalDataDto;
import com.tys.openquant.historical.service.HistoricalDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Validated
@CrossOrigin("*")
@Controller
@RequestMapping("/api/historical")
@RestController
@Slf4j
@RequiredArgsConstructor
public class HistoricalDataController {
    private final HistoricalDataService historicalDataService;

    /**
     * 전략 시뮬레이션 목적의 과거 특정 기간 일봉 데이터 요청을 라우팅해주는 컨트롤러
     * @param symbolCode: 시세를 요청하는 종목코드
     * @param startDate: 시작 날짜
     * @param endDate: 종료 날짜
     * @return ResponseEntity<HistoricalDataDto.StrategyDayDataListDto>
     * @author kyung.kim
     */
    @GetMapping("/strategy/daydata")
    public ResponseEntity<HistoricalDataDto.StrategyDayDataListDto> getStrategyDayData(
            @NotBlank
            @RequestParam("symbol_code") String symbolCode,
            @DateTimeFormat(pattern="yyyy-MM-dd") @RequestParam("start_date") LocalDate startDate,
            @DateTimeFormat(pattern="yyyy-MM-dd") @RequestParam("end_date") LocalDate endDate) {
        return ResponseEntity.ok(historicalDataService.getDayDataClosePriceListForStrategy(symbolCode, startDate, endDate));
    }

    /**
     * 전략 시뮬레이션 목적의 과거 특정 기간 분봉 데이터 요청을 라우팅해주는 컨트롤러
     * @param symbolCode: 시세를 요청하는 종목코드
     * @param startDateTime: 시작 날짜-시간, iso format
     * @param endDateTime: 종료 날짜-시간, iso format
     * @return ResponseEntity<HistoricalDataDto.StrategyMinDataListDto>
     * @author kyung.kim
     */
    @GetMapping("/strategy/mindata")
    public ResponseEntity<HistoricalDataDto.StrategyMinDataListDto> getStrategyMinData(
        @NotBlank
        @RequestParam("symbol_code") String symbolCode,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("start_datetime") LocalDateTime startDateTime,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("end_datetime") LocalDateTime endDateTime) {
        return ResponseEntity.ok(historicalDataService.getMinDataClosePriceListForStrategy(symbolCode, startDateTime, endDateTime));
    }

    /**
     * 차트 플로팅 목적의 과거 특정 기간 일봉 데이터 요청을 라우팅해주는 컨트롤러
     * @param symbolCode: 시세를 요청하는 종목코드
     * @param startDate: 시작 날짜
     * @param endDate: 종료 날짜
     * @return ResponseEntity<HistoricalDataDto.ChartDayDataListDto>
     * @author kyung.kim
     */
    @GetMapping("/chart/daydata")
    public ResponseEntity<HistoricalDataDto.ChartDayDataListDto> getChartDayData(
            @NotBlank
            @RequestParam("symbol_code") String symbolCode,
            @DateTimeFormat(pattern="yyyy-MM-dd") @RequestParam("start_date") LocalDate startDate,
            @DateTimeFormat(pattern="yyyy-MM-dd") @RequestParam("end_date") LocalDate endDate) {
        return ResponseEntity.ok(historicalDataService.getDayDataClosePriceListForChart(symbolCode, startDate, endDate));
    }

    /**
     * 차트 플로팅 목적의 과거 특정 기간 분봉 데이터 요청을 라우팅해주는 컨트롤러
     * @param symbolCode: 시세를 요청하는 종목코드
     * @param startDateTime: 시작 날짜-시간, iso format
     * @param endDateTime: 종료 날짜-시간, iso format
     * @return ResponseEntity<HistoricalDataDto.ChartMinDataListDto>
     * @author kyung.kim
     */
    @GetMapping("/chart/mindata")
    public ResponseEntity<HistoricalDataDto.ChartMinDataListDto> getChartMinData(
            @NotBlank
            @RequestParam("symbol_code") String symbolCode,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("start_datetime") LocalDateTime startDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("end_datetime") LocalDateTime endDateTime) {
        return ResponseEntity.ok(historicalDataService.getMinDataClosePriceListForChart(symbolCode, startDateTime, endDateTime));
    }

}
