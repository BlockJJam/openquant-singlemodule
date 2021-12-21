package com.tys.openquant.domain.price.compositekey;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayDataId implements Serializable {
    private LocalDate date;
    private String code;
}
