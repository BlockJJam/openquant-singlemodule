package com.tys.openquant.historical.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tys.openquant.historical.repositories.LiveSymbolsRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matcher.*;

@Slf4j
@SpringBootTest
public class SymbolDataMockTest {
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    LiveSymbolsRepository liveSymbolsRepository;


    @BeforeEach
    void setUp(WebApplicationContext context) throws Exception {
        DelegatingFilterProxy proxyFilter = new DelegatingFilterProxy();
        proxyFilter.init( new MockFilterConfig(context.getServletContext(),
                BeanIds.SPRING_SECURITY_FILTER_CHAIN));

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(proxyFilter)
                .build();
    }

    @Test
    void LiveSymbol_조회API_정상() throws Exception {
        // given
        int symbolListSize = liveSymbolsRepository.findAllByLive(true).size();

        // when
        ResultActions result = this.mockMvc.perform(
                get("/api/historical/symbol/live")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        log.info("{} size return", symbolListSize);
        result.andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andExpect(jsonPath("$.data[0].code").isString())
                .andExpect(jsonPath("$.data[0].live").isBoolean())
                .andExpect(jsonPath("data", hasSize(symbolListSize)));
    }
}
