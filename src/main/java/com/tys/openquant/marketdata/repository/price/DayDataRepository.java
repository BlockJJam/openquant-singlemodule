package com.tys.openquant.marketdata.repository.price;

import com.tys.openquant.domain.price.DayData;
import com.tys.openquant.domain.price.compositekey.DayDataId;
import com.tys.openquant.marketdata.dto.function.ReturnsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DayDataRepository extends JpaRepository<DayData, DayDataId>, CustomDayDataRepository {
    List<DayData> findAllByCode(String code, Pageable pageable);
    DayData findTopByOrderByDateDesc();

    @Query(value="select \n" +
            "distinct code, \n" +
            "round(CAST(\n" +
            "  (\n" +
            "    (\n" +
            "      last_value(close_price) over(partition by code order by date) - CAST(first_value(close_price) over(partition by code order by date) as float)\n" +
            "    ) / first_value(close_price) over(partition by code) * 100\n" +
            "  ) as numeric), 2\n" +
            ") AS returns, \n" +
            "last_value(close_price) over(partition by code order by date) AS currPrice\n" +
            "\n" +
            "from openquant.day_data\n" +
            "where code in ?1 \n" +
            "and date in (?2, ?3)   \n" +
            "order by returns desc\n" +
            "limit 20", nativeQuery = true)
    List<Object[]> findTop20ReturnsOfStockPriceContainsSymbolListAndTerm(List<String> symbolCodeList, LocalDate currDate, LocalDate findTermDate);


    @Query(nativeQuery = true)
    List<ReturnsDto> findReturnsTop20OrderByTerm(@Param("term") String term);
}
