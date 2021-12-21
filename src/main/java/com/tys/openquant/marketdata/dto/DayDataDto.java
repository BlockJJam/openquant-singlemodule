package com.tys.openquant.marketdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tys.openquant.domain.price.DayData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class DayDataDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class Info{

        private LocalDate date;
        private String code;
        private Integer openPrice;
        private Integer highPrice;
        private Integer lowPrice;
        private Integer closePrice;
        private Integer volume;

        public Info toInfo(DayData dayData){
            return Info.builder()
                    .date(dayData.getDate() != null? dayData.getDate() : null )
                    .code(dayData.getCode() != null? dayData.getCode() : "No data")
                    .openPrice(dayData.getBasePrice().getOpenPrice() != null? dayData.getBasePrice().getOpenPrice(): 0)
                    .highPrice(dayData.getBasePrice().getHighPrice() != null? dayData.getBasePrice().getHighPrice(): 0)
                    .lowPrice(dayData.getBasePrice().getLowPrice() != null? dayData.getBasePrice().getLowPrice(): 0)
                    .closePrice(dayData.getBasePrice().getClosePrice() != null? dayData.getBasePrice().getClosePrice(): 0)
                    .volume(dayData.getBasePrice().getVolume() != null? dayData.getBasePrice().getVolume() : -1)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class OneSymbolInfo{

        private LocalDate date;
        @JsonProperty("open_price")
        private Integer openPrice;
        @JsonProperty("high_price")
        private Integer highPrice;
        @JsonProperty("low_price")
        private Integer lowPrice;
        @JsonProperty("close_price")
        private Integer closePrice;
        private Integer volume;

        public static OneSymbolInfo toOneSymbolPriceInfo(DayData dayData){
            return OneSymbolInfo.builder()
                    .date(dayData.getDate() != null? dayData.getDate() : null )
                    .openPrice(dayData.getBasePrice().getOpenPrice() != null? dayData.getBasePrice().getOpenPrice(): 0)
                    .highPrice(dayData.getBasePrice().getHighPrice() != null? dayData.getBasePrice().getHighPrice(): 0)
                    .lowPrice(dayData.getBasePrice().getLowPrice() != null? dayData.getBasePrice().getLowPrice(): 0)
                    .closePrice(dayData.getBasePrice().getClosePrice() != null? dayData.getBasePrice().getClosePrice(): 0)
                    .volume(dayData.getBasePrice().getVolume() != null? dayData.getBasePrice().getVolume() : -1)
                    .build();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    public static class PriceListAboutOneSymbol{
        private String code;
        private String name;
        private List<OneSymbolInfo> data;

        public void convertToPriceList(List<DayData> dayDataList){
            this.data = dayDataList.stream()
                    .map(OneSymbolInfo::toOneSymbolPriceInfo)
                    .collect(toList());
        }
    }
}
