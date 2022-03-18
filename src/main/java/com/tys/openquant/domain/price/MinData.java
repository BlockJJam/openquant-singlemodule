package com.tys.openquant.domain.price;

import com.tys.openquant.domain.price.compositekey.MinDataId;
import com.tys.openquant.domain.price.embed.BasePrice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="min_data", schema = "openquant")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@IdClass(MinDataId.class)
public class MinData {
    @Id
    private LocalDateTime dateTime;

    @Id
    @Column(length = 50)
    private String code;

    @Embedded
    private BasePrice basePrice;

}
