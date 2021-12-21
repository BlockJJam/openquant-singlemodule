package com.tys.openquant.marketdata.dto;

import com.tys.openquant.marketdata.dto.function.ReturnsDto;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PriceDto {

    // Serializable을 이용하는 이유
    // : 캐싱을 할 때, 외부 메모리나 Disk에 저장하기 위해서는 JVM의 힙 메모리가 아니기 때문에
    // : 외부에서 JVM 메모리에 인스턴스화 되어있는 객체의 데이터를 사용할 수 있도록 한다 ( 현재는 힙메모리, 확장성을 위해 미리 설정 )
    @Getter
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnsList implements Serializable {

        private List<ReturnsDto> data;

        public void changeReturnsData(List<ReturnsDto> data){
            this.data = data;
        }
    }
}
