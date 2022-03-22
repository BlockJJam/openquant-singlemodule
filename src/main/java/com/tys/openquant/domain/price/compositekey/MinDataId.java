package com.tys.openquant.domain.price.compositekey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinDataId implements Serializable {
    private String code;
    private LocalDateTime dateTime;
}
