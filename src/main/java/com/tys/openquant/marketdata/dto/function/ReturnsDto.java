package com.tys.openquant.marketdata.dto.function;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ReturnsDto implements Serializable {
    private String code;
    private String name;
    private Double W1;
    private Double M1;
    private Double M3;
    private Double M6;
    private Double Y1;
    private Double Y3;

    public ReturnsDto(String code, String name, Double w1, Double m1, Double m3, Double m6, Double y1, Double y3) {
        this.code = code;
        this.name = name;
        W1 = w1;
        M1 = m1;
        M3 = m3;
        M6 = m6;
        Y1 = y1;
        Y3 = y3;
    }
}
