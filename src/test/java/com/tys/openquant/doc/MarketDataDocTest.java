package com.tys.openquant.doc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestParametersSnippet;
import org.springframework.security.config.BeanIds;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.ServletException;
import javax.transaction.Transactional;

import static com.tys.openquant.APIDocumentUtils.getDocumentRequest;
import static com.tys.openquant.APIDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureRestDocs(uriScheme ="https", uriHost = "docs.api.com")
@ContextConfiguration
public class MarketDataDocTest {
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(WebApplicationContext context,
                      RestDocumentationContextProvider provider) throws ServletException {
        DelegatingFilterProxy delegateProxyFilter = new DelegatingFilterProxy();
        delegateProxyFilter.init( new MockFilterConfig(context.getServletContext(), BeanIds.SPRING_SECURITY_FILTER_CHAIN));


        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(provider))
                .addFilter(delegateProxyFilter)
                .build();
    }

    @Test
    @Transactional
    public void 모든종목정보조회_API문서() throws Exception {
        // given & when
        ResultActions result = this.mockMvc.perform(
                get("/api/md/symbol-list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("data").isArray())
                .andExpect(jsonPath("$.data[0].symbol_seq").isNotEmpty())
                .andExpect(jsonPath("$.data[0].code").isString())
                .andExpect(jsonPath("$.data[0].name").isString())
                .andExpect(jsonPath("$.data[0].updated_at").isString())
                .andDo(document("marketdata-symbol-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                subsectionWithPath("data").type(JsonFieldType.ARRAY).description("모든 종목리스트 정보"),
                                fieldWithPath("data[].symbol_seq").type(JsonFieldType.NUMBER).description("해당 종목의 sequence"),
                                fieldWithPath("data[].code").type(JsonFieldType.STRING).description("해당 종목의 종목 코드"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("해당 종목의 이름"),
                                fieldWithPath("data[].updated_at").type(JsonFieldType.STRING).description("해당 종목의 업데이트된 날짜").optional()
                                // 뒤에 optional()을 붙이면, null이어도 넘어간다
                        )
                ));

    }

    @Test
    @Transactional
    public void 한종목_시세데이터조회_API문서() throws Exception{
        // given
        String code = "A003465";
        int page = 0;
        int size = 100;

        // when
        ResultActions result = this.mockMvc.perform(
                get("/api/md/price-data?code={code}&page={page}&size={size}",code, page, size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("code").isString())
                .andExpect(jsonPath("name").isString())
                .andExpect(jsonPath("data").isArray())
                .andDo(document("marketdata-price-data",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("code").description("시세데이터를 가져올 종목 코드"),
                                parameterWithName("page").description("분할된 시세데이터의 페이지 정보"),
                                parameterWithName("size").description("한번에 가져올 수 있는 시세데이터 개수")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.STRING).description("조회한 시세데이터 종목코드"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("조회한 시세데이터 종목이름"),
                                subsectionWithPath("data").type(JsonFieldType.ARRAY).description("조회한 시세데이터 종목코드"),
                                fieldWithPath("data[].date").type(JsonFieldType.STRING).description("시세데이터의 날짜정보"),
                                fieldWithPath("data[].open_price").type(JsonFieldType.NUMBER).description("시세데이터의 시가정보"),
                                fieldWithPath("data[].high_price").type(JsonFieldType.NUMBER).description("시세데이터의 고가정보"),
                                fieldWithPath("data[].low_price").type(JsonFieldType.NUMBER).description("시세데이터의 저가정보"),
                                fieldWithPath("data[].close_price").type(JsonFieldType.NUMBER).description("시세데이터의 종가정보"),
                                fieldWithPath("data[].volume").type(JsonFieldType.NUMBER).description("시세데이터의 거래량정보")
                        )
                ));
    }

    @Test
    @Transactional
    public void 수익률TOP20차트_API문서() throws Exception{
        // given
        String date = "W1";

        // when
        ResultActions result = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/md/returns/{date}",date)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("data").isArray())
                .andDo(document("marketdata-returns",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("date").description("수익률 차트에 기준이 되는 과거 날짜와의 Term")
                        ),
                        responseFields(
                                subsectionWithPath("data").type(JsonFieldType.ARRAY).description("수익률 데이터가 담긴 배열"),
                                fieldWithPath("data[].code").type(JsonFieldType.STRING).description("수익률 데이터 배열요소 객체의 종목코드 필드"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("수익률 데이터 배열요소 객체의 종목이름 필드"),
                                fieldWithPath("data[].w1").type(JsonFieldType.NUMBER).description("수익률 데이터 배열요소 객체의 weekly 수익률").optional(),
                                fieldWithPath("data[].m1").type(JsonFieldType.NUMBER).description("수익률 데이터 배열요소 객체의 monthly 수익률").optional(),
                                fieldWithPath("data[].m3").type(JsonFieldType.NUMBER).description("수익률 데이터 배열요소 객체의 monthly3 수익률").optional(),
                                fieldWithPath("data[].m6").type(JsonFieldType.NUMBER).description("수익률 데이터 배열요소 객체의 monthly6 수익률").optional(),
                                fieldWithPath("data[].y1").type(JsonFieldType.NUMBER).description("수익률 데이터 배열요소 객체의 annually1 수익률").optional(),
                                fieldWithPath("data[].y3").type(JsonFieldType.NUMBER).description("수익률 데이터 배열요소 객체의 annually3 수익률").optional()
                        )
                ));
    }


}
