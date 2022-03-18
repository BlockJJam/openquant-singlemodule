package com.tys.openquant.historical.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tys.openquant.domain.price.DayData;
import com.tys.openquant.domain.price.MinData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Local;

import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HistoricalDataDto {

    @Getter
    public static class StrategyDayDataPriceListDto {
        private List<StrategyDayDataDto> strategyDayDataDtoList;

        public StrategyDayDataPriceListDto(List<DayData> dataList) {
            this.strategyDayDataDtoList = dataList.stream()
                    .map(d -> new HistoricalDataDto.StrategyDayDataDto(d))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class StrategyDayDataDto {
        @JsonProperty("daydata_code")
        private String code;
        @JsonProperty("daydata_date")
        private LocalDate date;
        @JsonProperty("daydata_close")
        private Integer closePrice;

        public StrategyDayDataDto(DayData dayData){
            this.code = dayData.getCode();
            this.date = dayData.getDate();
            this.closePrice = dayData.getBasePrice().getClosePrice();
        }
    }

    @Getter
    public static class StrategyMinDataPriceListDto {
        private List<StrategyMinDataDto> strategyMinDataDtoList;

        public StrategyMinDataPriceListDto(List<MinData> dataList) {
            this.strategyMinDataDtoList = dataList.stream()
                    .map(d -> new HistoricalDataDto.StrategyMinDataDto(d))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    public static class StrategyMinDataDto{
        @JsonProperty("mindata_code")
        private String code;
        @JsonProperty("mindata_datetime")
        private LocalDateTime dateTime;
        @JsonProperty("mindata_close")
        private Integer closePrice;

        public StrategyMinDataDto(MinData minData) {
            this.code = minData.getCode();
            this.dateTime = minData.getDateTime();
            this.closePrice = minData.getBasePrice().getClosePrice();
        }
    }


    @Getter
    @NoArgsConstructor
    public static class ChartDayDataPriceListDto {
        private List<ChartDayDataDto> chartDayDataListDtoList;

        public ChartDayDataPriceListDto(List<DayData> dayDataList) {
            this.chartDayDataListDtoList = dayDataList.stream()
                    .map(d -> new HistoricalDataDto.ChartDayDataDto(d))
                    .collect(Collectors.toList());
        }
    }


    @Getter
    public static class ChartDayDataDto {
        @JsonProperty("daydata_code")
        private String code;
        @JsonProperty("daydata_date")
        private LocalDate date;
        @JsonProperty("daydata_open")
        private Integer openPrice;
        @JsonProperty("high")
        private Integer highPrice;
        @JsonProperty("low")
        private Integer lowPrice;
        @JsonProperty("close")
        private Integer closePrice;
        @JsonProperty("volume")
        private Integer volume;

        public ChartDayDataDto(DayData dayData) {
            this.code = dayData.getCode();
            this.date = dayData.getDate();
            this.openPrice = dayData.getBasePrice().getOpenPrice();
            this.highPrice = dayData.getBasePrice().getHighPrice();
            this.lowPrice = dayData.getBasePrice().getLowPrice();
            this.closePrice = dayData.getBasePrice().getClosePrice();
            this.volume = dayData.getBasePrice().getVolume();
        }
    }

    @Getter
    public static class ChartMinDataPriceListDto {
        private List<ChartMinDataDto> chartMinDataDtoList;

        public ChartMinDataPriceListDto(List<MinData> minDataList) {
            this.chartMinDataDtoList = minDataList.stream()
                    .map(d -> new HistoricalDataDto.ChartMinDataDto(d))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    public static class ChartMinDataDto {
        @JsonProperty("mindata_code")
        private String code;
        @JsonProperty("mindata_date")
        private LocalDateTime dateTime;
        @JsonProperty("mindata_open")
        private Integer openPrice;
        @JsonProperty("high")
        private Integer highPrice;
        @JsonProperty("low")
        private Integer lowPrice;
        @JsonProperty("close")
        private Integer closePrice;
        @JsonProperty("volume")
        private Integer volume;

        public ChartMinDataDto(MinData minData) {
            this.code = minData.getCode();
            this.dateTime = minData.getDateTime();
            this.openPrice = minData.getBasePrice().getOpenPrice();
            this.highPrice = minData.getBasePrice().getHighPrice();
            this.lowPrice = minData.getBasePrice().getLowPrice();
            this.closePrice = minData.getBasePrice().getClosePrice();
            this.volume = minData.getBasePrice().getVolume();
        }
    }
}
