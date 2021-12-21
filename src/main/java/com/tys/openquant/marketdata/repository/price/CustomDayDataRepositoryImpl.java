package com.tys.openquant.marketdata.repository.price;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.WindowFunction;
import com.tys.openquant.domain.price.DayData;
import com.tys.openquant.marketdata.dto.PriceDto;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static com.querydsl.core.alias.Alias.$;
import static com.tys.openquant.domain.price.QDayData.dayData;
import static com.tys.openquant.domain.price.embed.QBasePrice.basePrice;
import static com.tys.openquant.domain.symbol.QSymbols.symbols;

@Repository
public class CustomDayDataRepositoryImpl extends QuerydslRepositorySupport implements CustomDayDataRepository {
    private final JPAQueryFactory jpaFactory;

    CustomDayDataRepositoryImpl(JPAQueryFactory jpaFactory){
        super(DayData.class);
        this.jpaFactory = jpaFactory;
    }

    @Override
    public LocalDate findDateByCompDate(LocalDate compDate, Period period) {
        LocalDate retDate = jpaFactory.select(dayData.date)
                .from(dayData)
                .where(dateLoe(compDate.minus(period)))
                .orderBy(dayData.date.desc())
                .fetchFirst();
        return retDate;
    }

    @Override
    public List<String> findAllCodeByExistsByTerm(LocalDate currDate, LocalDate termDate) {
        List<String> retSymbolCodeList = jpaFactory.select(symbols.code)
                .from(symbols)
                .where(JPAExpressions.selectOne()
                                .from(dayData)
                                .where(codeEqBothDayAndSymbols(), dateEq(currDate))
                                .exists(),
                        JPAExpressions.selectOne()
                                .from(dayData)
                                .where(codeEqBothDayAndSymbols(), dateEq(termDate))
                                .exists())
                .fetch();
        return retSymbolCodeList;
    }

//    @Override
//    public List<PriceDto.ReturnsInfo> findTop20ReturnsOfStockPriceContainsSymbolListAndTerm(List<String> symbolCodeList, LocalDate currDate, LocalDate findTermDate) {
//        List<PriceDto.ReturnsInfo> retList = jpaFactory.select(Projections.fields(PriceDto.ReturnsInfo.class,
//                            dayData.code,
//                            ((NumberExpression)getLastPriceByCodeOrderByDate()).subtract((NumberExpression)getFirstPriceByCodeOrderByDate())
//                                    .floatValue()
//                                    .divide((NumberExpression)getFirstPriceByCodeOrderByDate())
//                                    .multiply(100).as("returns"),
//                            getFirstPriceByCodeOrderByDate().as("currPrice"),
//                        dayData.priceInfo.highPrice.castToNum(Double.class),
//                        dayData.priceInfo.highPrice.castToNum(Double.class),
//                        dayData.priceInfo.highPrice.castToNum(Double.class),
//                        dayData.priceInfo.highPrice.castToNum(Double.class),
//                        dayData.priceInfo.highPrice.castToNum(Double.class),
//                        dayData.priceInfo.highPrice.castToNum(Double.class)
//                        ))
//                .distinct()
//                .from(dayData)
//                .where(dayData.code.in(symbolCodeList), dayData.date.in(currDate,findTermDate))
//                .orderBy()
//                .limit(20)
//                .fetch();
//        return retList;
//    }


    @Override
    public Double findOneReturnsOfStockByCodeAndCurrPriceAndTerm(String code, Integer currPrice, LocalDate term) {

        Double retPrice = jpaFactory.select((dayData.basePrice != null? $(currPrice) : $(0))
                        .subtract(dayData.basePrice == null? $(currPrice) : dayData.basePrice.closePrice)
                        .doubleValue()
                        .divide(dayData.basePrice == null? $(currPrice) : dayData.basePrice.closePrice)
                        .multiply(100))
                .from(dayData)
                .where(codeEq(code), dateEq(term))
                .fetchOne();
        return retPrice;
    }

    private BooleanExpression dateLoe(LocalDate compDate){
        return compDate != null? dayData.date.loe(compDate): null;
    }

    private BooleanExpression dateEq(LocalDate date){
        return date != null? dayData.date.eq(date): null;
    }

    private BooleanExpression codeEq(String code){
        return code != null? dayData.code.eq(code): null;
    }

    private BooleanExpression codeEqBothDayAndSymbols(){
        return dayData.code.eq(symbols.code);
    }




    /********************************** below not use code ***************************************/

    private WindowFunction<Integer> getLastPriceByCodeOrderByDate(){
        return SQLExpressions.lastValue(dayData.basePrice.closePrice).over().partitionBy(dayData.code).orderBy(dayData.date);
    }

    private WindowFunction<Integer> getFirstPriceByCodeOrderByDate(){
        return SQLExpressions.firstValue(dayData.basePrice.closePrice).over().partitionBy(dayData.code).orderBy(dayData.date);
    }
}
