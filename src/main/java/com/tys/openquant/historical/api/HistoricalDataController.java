package com.tys.openquant.historical.api;

import com.tys.openquant.historical.dto.HistoricalDataDto;
import com.tys.openquant.historical.service.HistoricalDataService;
import com.tys.openquant.marketdata.dto.DayDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/api/historical")
@RestController
@Slf4j
@RequiredArgsConstructor
public class HistoricalDataController {
    private final HistoricalDataService historicalDataService;

    @GetMapping("/strategy/daydata")
    public ResponseEntity<HistoricalDataDto.StrategyDayDataPriceListDto> getStrategyDayData(
            @NotBlank
            @RequestParam("symbol_code") String symbolCode,
            @DateTimeFormat(pattern="yyyy-MM-dd") @RequestParam("start_date") LocalDate startDate,
            @DateTimeFormat(pattern="yyyy-MM-dd") @RequestParam("end_date") LocalDate endDate) {
        return ResponseEntity.ok(historicalDataService.getDayDataClosePriceListForStrategy(symbolCode, startDate, endDate));
    }

    @GetMapping("/strategy/mindata")
    public ResponseEntity<HistoricalDataDto.StrategyMinDataPriceListDto> getStrategyMinData(
        @NotBlank
        @RequestParam("symbol_code") String symbolCode,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("start_datetime") LocalDateTime startDateTime,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("end_datetime") LocalDateTime endDateTime) {

        return ResponseEntity.ok(historicalDataService.getMinDataClosePriceListForStrategy(symbolCode, startDateTime, endDateTime));
    }

    @GetMapping("/chart/daydata")
    public ResponseEntity<HistoricalDataDto.ChartDayDataPriceListDto> getChartDayData(
            @NotBlank
            @RequestParam("symbol_code") String symbolCode,
            @DateTimeFormat(pattern="yyyy-MM-dd") @RequestParam("start_date") LocalDate startDate,
            @DateTimeFormat(pattern="yyyy-MM-dd") @RequestParam("end_date") LocalDate endDate) {
        return ResponseEntity.ok(historicalDataService.getDayDataClosePriceListForChart(symbolCode, startDate, endDate));
    }

    @GetMapping("/chart/mindata")
    public ResponseEntity<HistoricalDataDto.ChartMinDataPriceListDto> getChartMinData(
            @NotBlank
            @RequestParam("symbol_code") String symbolCode,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("start_datetime") LocalDateTime startDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("end_datetime") LocalDateTime endDateTime) {
        return ResponseEntity.ok(historicalDataService.getMinDataClosePriceListForChart(symbolCode, startDateTime, endDateTime));
    }

}
