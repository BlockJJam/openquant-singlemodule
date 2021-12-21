package com.tys.openquant.marketdata.enums;

import lombok.Getter;

import java.time.Period;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum TermConvertType {
    ONE_WEEKEND("W1", "weekly"),
    ONE_MONTH("M1","monthly"),
    THREE_MONTH("M3","monthly3"),
    SIX_MONTH("M6","monthly6"),
    ONE_YEAR("Y1","annually"),
    THREE_YEAR("Y3","annually3");

    private final String abbrDateStr;
    private final String convertedTerm;
    private static final Map<String, TermConvertType> termConvertMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(TermConvertType::getAbbrDateStr, Function.identity())));

    TermConvertType(String abbrDateStr, String convertedTerm) {
        this.abbrDateStr = abbrDateStr;
        this.convertedTerm = convertedTerm;
    }

    public static TermConvertType find(String abbrDateStr){
        return Optional.ofNullable(termConvertMap.get(abbrDateStr)).orElseThrow(
                () -> new IllegalArgumentException(abbrDateStr)
        );
    }
}
