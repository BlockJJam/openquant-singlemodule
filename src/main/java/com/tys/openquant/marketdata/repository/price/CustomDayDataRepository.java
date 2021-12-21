package com.tys.openquant.marketdata.repository.price;

import com.tys.openquant.marketdata.dto.PriceDto;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public interface CustomDayDataRepository {

    public LocalDate findDateByCompDate(LocalDate compDate, Period period);

    public List<String> findAllCodeByExistsByTerm(LocalDate currDate, LocalDate termDate);

//    public List<PriceDto.ReturnsInfo> findTop20ReturnsOfStockPriceContainsSymbolListAndTerm(
//            List<String> symbolCodeList, LocalDate currDate, LocalDate findTermDate
//    );

    Double findOneReturnsOfStockByCodeAndCurrPriceAndTerm(String code, Integer currPrice, LocalDate Term);
}
