package com.tys.openquant.historical.api;

import com.tys.openquant.historical.repositories.LiveSymbolsRepository;
import com.tys.openquant.historical.service.SymbolService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import static com.tys.openquant.historical.dto.SymbolDataDto.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class SymbolServiceTest {

    @Spy
    LiveSymbolsRepository liveSymbolsRepository;

    @InjectMocks
    SymbolService symbolService;

    @Test
    void getLiveSymbolList_return_type_test(){
        // given & when & then
        assertThat(symbolService.getLiveSymbolList()).isInstanceOf(LiveList.class);
    }

}
