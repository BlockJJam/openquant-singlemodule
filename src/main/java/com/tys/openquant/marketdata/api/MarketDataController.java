package com.tys.openquant.marketdata.api;

import com.tys.openquant.marketdata.dto.DayDataDto;
import com.tys.openquant.marketdata.dto.PriceDto;
import com.tys.openquant.marketdata.dto.SymbolDto;
import com.tys.openquant.marketdata.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Validated
@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/md")
public class MarketDataController {

    private final MarketDataService marketDataService;

    /**
     * DB의 현재 주식 상장되어있는 모든 종목 데이터 리스트를 조회하는 API
     * @return SymbolDto.AllData
     * @author Jaemin.Joo
     */
    @GetMapping("/symbol-list")
    public ResponseEntity<SymbolDto.AllData> getSymbolList(){
        return ResponseEntity.ok(marketDataService.getAllSymbolData());
    }

    /**
     * code에 대한 시세 정보를 page번째 인덱스에서 size만큼의 데이터 리스트를 조회하는 API
     * @param code
     * @param pageable
     * @return DayDataDto.PriceListAboutOneSymbol
     * @author Jaemin.Joo
     */
    @GetMapping("/price-data")
    public ResponseEntity<DayDataDto.PriceListAboutOneSymbol> getPriceListAboutOneSymbol(@RequestParam("code")
                                                                                                   @Size(min=1, max=50)
                                                                                                   @NotNull String code,
                                                                                         @PageableDefault(page= 0, size = 100, sort={"date"},direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(marketDataService.getPriceListByCodeAndPageable(code, pageable));
    }

    /**
     * 요청 파라미터인 기준 날짜로 수익률 top20 차트에 필요한 데이터를 조회하는 API
     * @param date
     * @return PriceDto.ReturnsTestList
     * @author Jaemin.Joo
     */
    @GetMapping("/returns/{date}")
    public ResponseEntity<PriceDto.ReturnsList> getReturnsOfStockPrice(@NotNull
                                                                           @PathVariable String date){

        return ResponseEntity.ok(marketDataService.getReturnsTop20WithDBFunction(date));
    }

}
