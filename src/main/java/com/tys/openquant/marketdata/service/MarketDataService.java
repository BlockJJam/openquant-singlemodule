package com.tys.openquant.marketdata.service;

import com.tys.openquant.domain.symbol.Symbols;
import com.tys.openquant.marketdata.dto.DayDataDto;
import com.tys.openquant.marketdata.dto.PriceDto;
import com.tys.openquant.marketdata.dto.SymbolDto;
import com.tys.openquant.marketdata.enums.TermConvertType;
import com.tys.openquant.marketdata.exception.SymbolException;
import com.tys.openquant.marketdata.repository.price.DayDataRepository;
import com.tys.openquant.marketdata.repository.symbol.SymbolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataService {
    private final SymbolRepository symbolRepository;
    private final DayDataRepository dayDataRepository;

    private final Integer TRADABLE_STATE = 0;

    /**
     * SymbolDB에서 가져온 상장된 모든 주식 종목 리스트를 반환하는 Service
     * @return SymbolDto.AllData
     * @author Jaemin.Joo
     */
    public SymbolDto.AllData getAllSymbolData() {
        // 수정이력: 2021.11.13 (이유: sybollist 수정)
        List<Symbols> symbolsList = symbolRepository.findAllByStateAndUpdateAtNotNull(TRADABLE_STATE);
        if(symbolsList.size() == 0)
            throw new SymbolException("현재 종목리스트에 어떠한 정보도 들어있지 않습니다");

        SymbolDto.AllData retData = new SymbolDto.AllData(symbolsList);
        return retData;
    }

    /**
     * 해당 주식 종목 code를 통해 symbol 정보와 해당 code를 가진 시세 정보를 day data db에서 조회하여 반환 자료구조에 담아 반환하는 service
     * @param code
     * @param pageable
     * @return DayDataDto.PriceListAboutOneSymbol
     * @author Jaemin.Joo
     */
    //@Cacheable(cacheNames="getReturnsOfStockPrice", key="#code")
    public DayDataDto.PriceListAboutOneSymbol getPriceListByCodeAndPageable(String code, Pageable pageable) {

        DayDataDto.PriceListAboutOneSymbol retList = getPriceListContainsSymbolInfo(code);
        retList.convertToPriceList(dayDataRepository.findAllByCode(retList.getCode(),pageable));


        return retList;
    }

    /**
     * 해당 code를 가지는 종목 정보를 symbol db에서 가져와 반환 자료구조에 담아 반환하는 method
     * @param code
     * @return DayDataDto.PriceListAboutOneSymbol
     * @author Jaemin.Joo
     */
    private DayDataDto.PriceListAboutOneSymbol getPriceListContainsSymbolInfo(String code) {
        Optional<Symbols> symbolOpt = symbolRepository.findByCode(code);
        return symbolOpt.map(symbols -> DayDataDto.PriceListAboutOneSymbol.builder()
                .code(symbols.getCode())
                .name(symbols.getName())
                .build())
                .orElseThrow(()-> new SymbolException("해당 code에 대한 종목 정보가 없습니다"));
    }


    /**
     * PostgreSQL function()을 통해 시세데이터 수익률 top20데이터와 해당 데이터의 날짜별 수익률을 가져오는 service
     * @param date
     * @return PriceDto.ReturnsList
     * @author Jaemin.Joo
     */
    @Cacheable(cacheNames="getReturnsOfStockPrice", key="#date")
    public PriceDto.ReturnsList getReturnsTop20WithDBFunction(String date) {

        PriceDto.ReturnsList retList = new PriceDto.ReturnsList();
        retList.changeReturnsData(
                dayDataRepository.findReturnsTop20OrderByTerm(TermConvertType.find(date).getConvertedTerm()));

        return retList;
    }
}


/*** 단순 시간체크용
     long start = System.currentTimeMillis();
     long end = System.currentTimeMillis();
     log.info("getReturnsTop20WithDBFunction interval time : {}", end-start);
 ***/