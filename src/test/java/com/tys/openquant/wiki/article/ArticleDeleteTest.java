package com.tys.openquant.wiki.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tys.openquant.domain.wiki.Article;
import com.tys.openquant.domain.wiki.Category;
import com.tys.openquant.user.dto.LoginDto;
import com.tys.openquant.wiki.repository.ArticleIntegrationRepository;
import com.tys.openquant.wiki.repository.CategoryIntegrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.security.config.BeanIds;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.transaction.Transactional;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ArticleDeleteTest {
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired private ArticleIntegrationRepository articleIntegrationRepository;

    private String authStr;


    @BeforeEach
    public void setUp(WebApplicationContext context) throws Exception {
        DelegatingFilterProxy delegateProxyFilter = new DelegatingFilterProxy();
        delegateProxyFilter.init( new MockFilterConfig(context.getServletContext(), BeanIds.SPRING_SECURITY_FILTER_CHAIN));


        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(delegateProxyFilter)
                .build();

        authStr = getAuthTokenInHeaderAfterLogin();
    }

    @Test
    @Transactional
    void Article삭제_정상입력() throws Exception {
        // given
        Long deleteId = articleIntegrationRepository.findTopByDelNyOrderByIdAsc(false).getId();

        // when
        ResultActions result = getArticleDeleteTransaction(deleteId);

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_deleted").exists());
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForArticleDeleteInvalidInputs")
    void Article삭제_비정상입력(Object id) throws Exception {
        // given
        Object deleteId = id;

        // when
        ResultActions result = getArticleDeleteTransaction(deleteId);

        // then
        result.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> paramsForArticleDeleteInvalidInputs(){
        return Stream.of(
                Arguments.of("abc"),
                Arguments.of(Long.MIN_VALUE),
                Arguments.of(true)
        );
    }

    private ResultActions getArticleDeleteTransaction(Object deleteId) throws Exception {
        return this.mockMvc.perform(
                get("/api/wiki/article/delete?id={deleteId}",deleteId)
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .accept(MediaType.ALL_VALUE)
        );
    }

    private String getAuthTokenInHeaderAfterLogin() throws Exception {
        LoginDto.Req loginReq = LoginDto.Req.builder()
                .id("admin")
                .pwd("admin123!@#")
                .build();

        ResultActions loginResult = this.mockMvc.perform(
                post("/api/login")
                        .content(objectMapper.writeValueAsString(loginReq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        MvcResult mvcResult = loginResult.andReturn();
        String authStr = mvcResult.getResponse().getHeader("Authorization");
        return authStr;
    }
}
