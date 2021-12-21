package com.tys.openquant.domain.price.embed;

import com.querydsl.core.annotations.QueryInit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Embeddable;

@Builder
@Embeddable
@Getter
@AllArgsConstructor
public class BasePrice {

    protected BasePrice(){}

    private Integer openPrice;
    private Integer highPrice;
    private Integer lowPrice;
    private Integer closePrice;
    private Integer volume;
}
