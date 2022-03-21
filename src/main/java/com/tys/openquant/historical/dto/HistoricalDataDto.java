package com.tys.openquant.historical.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tys.openquant.domain.price.DayData;
import com.tys.openquant.domain.price.MinData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


// has_symbol_code, is_allowed 필드를 채워서 리턴할 것이다.
// 방법은 여러가지 static 메서드, 생성자, builder 여러가지
public class HistoricalDataDto {

    @Getter
    @Setter
    public static class StrategyDayDataListDto {
        private List<StrategyDayDataDto> data;
        @JsonProperty("has_symbol_code")
        private boolean hasSymbolCode;
        @JsonProperty("allowed_date_range")
        private boolean allowedDateRange;

        public StrategyDayDataListDto(List<DayData> dataList, boolean hasSymbolCode, boolean allowedDateRange) {
            this.data = dataList.stream()
                        .map(HistoricalDataDto.StrategyDayDataDto::new)
                        .collect(Collectors.toList());
            this.hasSymbolCode = hasSymbolCode;
            this.allowedDateRange = allowedDateRange;
        }

    }

    @Getter
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
    public static class StrategyMinDataListDto {
        private List<StrategyMinDataDto> data;

        public StrategyMinDataListDto(List<MinData> dataList) {
            this.data = dataList.stream()
                    .map(HistoricalDataDto.StrategyMinDataDto::new)
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
    public static class ChartDayDataListDto {
        private List<ChartDayDataDto> data;

        public ChartDayDataListDto(List<DayData> dayDataList) {
            this.data = dayDataList.stream()
                    .map(HistoricalDataDto.ChartDayDataDto::new)
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
    public static class ChartMinDataListDto {
        private List<ChartMinDataDto> data;

        public ChartMinDataListDto(List<MinData> minDataList) {
            this.data = minDataList.stream()
                    .map(HistoricalDataDto.ChartMinDataDto::new)
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
