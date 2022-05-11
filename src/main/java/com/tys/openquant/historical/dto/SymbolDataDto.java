package com.tys.openquant.historical.dto;

import com.tys.openquant.domain.symbol.Symbols;
import lombok.*;

import java.util.List;

import static java.util.stream.Collectors.*;

public class SymbolDataDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LiveList{
        private List<LiveSymbol> data;

        public void convertToLiveList(List<Symbols> symbolsList){
            this.data = symbolsList.stream()
                    .map(LiveSymbol::toLiveSymbol)
                    .collect(toList());
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class LiveSymbol{
        private String code;
        private Boolean live;


        public static LiveSymbol toLiveSymbol(Symbols symbols){
            if(symbols.getCode() == null || symbols.getLive() == null)
                throw new IllegalArgumentException("RealtimeSymbols에서 넘어온 값 중에 null값이 있습니다");
            return LiveSymbol.builder()
                    .code(symbols.getCode())
                    .live(symbols.getLive())
                    .build();
        }
    }
}

