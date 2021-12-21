package com.tys.openquant.marketdata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tys.openquant.domain.symbol.Symbols;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

public class SymbolDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class Info{
        @JsonProperty("symbol_seq")
        private Integer symbolSeq;
        private String code;
        private String name;
        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;

        public static Info toInfo(Symbols symbols){
            return Info.builder()
                    .symbolSeq(symbols.getSymbolSeq() != null? symbols.getSymbolSeq(): -1)
                    .code(symbols.getCode() != null? symbols.getCode():"No Data")
                    .name(symbols.getName() != null? symbols.getName(): "No Data")
                    .updatedAt(symbols.getUpdateAt() != null? symbols.getUpdateAt(): null)
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class AllData{
        private List<Info> data;

        public AllData(List<Symbols> symbolsList){
            this.data = symbolsList.stream()
                    .map(Info::toInfo)
                    .collect(toList());
        }
    }
}
