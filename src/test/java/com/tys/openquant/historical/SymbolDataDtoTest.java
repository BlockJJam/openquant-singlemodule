package com.tys.openquant.historical;

import com.tys.openquant.domain.symbol.Symbols;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.tys.openquant.historical.dto.SymbolDataDto.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
public class SymbolDataDtoTest {

    @Test
    void RealtimeSymbolsList_To_LiveSymbolsList_정상() {
        // given
        Symbols symbols1 = new Symbols(1,"A1","hi",1, LocalDateTime.now(), true);

        List<Symbols> testList = new ArrayList<>();
        testList.add(symbols1);
        LiveList symbolList = new LiveList();

        // when
        symbolList.convertToLiveList(testList);

        // then
        for(LiveSymbol liveSymbol: symbolList.getData()){
            assertThat(liveSymbol).hasFieldOrProperty("code");
            assertThat(liveSymbol).hasFieldOrProperty("live");
            assertThat(liveSymbol.getCode()).isNotEqualTo(null);
            assertThat(liveSymbol.getLive()).isNotEqualTo(null);

        }
    }

    @DisplayName("LiveSymbolsList의 convertToLiveList()에 필요한 RealtimeSymbols의 필드에 null이 들어있을 때 에러 테스트")
    @Test
    void LiveSymbols_null_error() {

        // given
        Symbols symbols1 = new Symbols(1,"A1","hi",1, LocalDateTime.now(), null);

        List<Symbols> testList = new ArrayList<>();
        testList.add(symbols1);
        LiveList symbolList = new LiveList();

        // when & then
        assertThatThrownBy(() -> {
            symbolList.convertToLiveList(testList);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
