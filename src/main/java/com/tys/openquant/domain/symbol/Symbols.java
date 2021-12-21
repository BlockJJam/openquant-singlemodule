package com.tys.openquant.domain.symbol;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="symbols", schema = "openquant")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@SequenceGenerator(
        name="SYMBOLS_SEQ_GENERATOR",
        sequenceName = "symbols_seq_seq",
        schema="openquant",
        initialValue = 1,
        allocationSize = 1
)
public class Symbols {

    @Id
    @Column(name="seq")
    private Integer symbolSeq;

    @Column(length = 50)
    private String code;

    @Column(length = 50)
    private String name;

    // 수정이력: 2021.11.13 (이유: sybollist 수정)
    private Integer state;

    @Column(name="update_datetime")
    private LocalDateTime updateAt;

}
