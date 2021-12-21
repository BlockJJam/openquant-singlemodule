package com.tys.openquant.domain.price;

import com.querydsl.core.annotations.QueryInit;
import com.tys.openquant.domain.price.compositekey.DayDataId;
import com.tys.openquant.domain.price.embed.BasePrice;
import com.tys.openquant.marketdata.dto.function.ReturnsDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name="day_data", schema = "openquant")
@NamedNativeQuery(name="DayData.findReturnsTop20OrderByTerm",
        query="select r.code as code, r.name as name, r.weekly as W1, r.monthly as M1, r.monthly3 as M3, r.monthly6 as M6, r.annually as Y1, r.annually3 as Y3 " +
                "from openquant.tb_returns() r " +
                "where code not in ('A010580','A263540') " +
                "order by " +
                " case when :term = 'weekly' then r.weekly " +
                " when :term = 'monthly' then r.monthly " +
                " when :term = 'monthly3' then r.monthly3 " +
                " when :term = 'monthly6' then r.monthly6 " +
                " when :term = 'annually' then r.annually " +
                " when :term = 'annually3' then r.annually3 " +
                " end desc nulls last" +
                " limit 20",
        resultSetMapping = "Mapping.ReturnsDto")
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "Mapping.ReturnsDto",
                classes = {@ConstructorResult(
                        targetClass = ReturnsDto.class,
                        columns = {
                                @ColumnResult(name = "code", type= String.class),
                                @ColumnResult(name = "name", type= String.class),
                                @ColumnResult(name = "W1", type= Double.class),
                                @ColumnResult(name = "M1", type= Double.class),
                                @ColumnResult(name = "M3", type= Double.class),
                                @ColumnResult(name = "M6", type= Double.class),
                                @ColumnResult(name = "Y1", type= Double.class),
                                @ColumnResult(name = "Y3", type= Double.class)
                        })
                }
        )
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@IdClass(DayDataId.class)
public class DayData {
    @Id
    private LocalDate date;

    @Id
    @Column(length = 50)
    private String code;

    @Embedded
    private BasePrice basePrice;
}
