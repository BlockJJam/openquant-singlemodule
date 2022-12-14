package com.tys.openquant.wiki.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tys.openquant.domain.wiki.Article;
import com.tys.openquant.user.dto.LoginDto;
import com.tys.openquant.wiki.repository.ArticleIntegrationRepository;
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
public class ArticleReadTest {
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
    void Article??????_??????_????????????() throws Exception {
        // given
        Article article = articleIntegrationRepository.findTopByDelNyAndIsPublicOrderByIdAsc(false, true);
        if(article == null) return;

        Long readId = article.getId();

        // when
        ResultActions result = getPublicArticleReadTransaction(readId);

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("category_id").exists())
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("contents").exists())
                .andExpect(jsonPath("overview").exists())
                .andExpect(jsonPath("updated_at").exists())
                .andExpect(jsonPath("created_at").exists());
    }

    @Test
    @Transactional
    void Article??????_Admin_????????????() throws Exception {
        // given
        Article article = articleIntegrationRepository.findTopByDelNyOrderByIdAsc(false);
        if(article == null) return;

        Long readId = article.getId();

        // when
        ResultActions result = getAdminArticleReadTransaction(readId);

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("category_id").exists())
                .andExpect(jsonPath("is_public").exists())
                .andExpect(jsonPath("title").exists())
                .andExpect(jsonPath("contents").exists())
                .andExpect(jsonPath("overview").exists())
                .andExpect(jsonPath("updated_at").exists())
                .andExpect(jsonPath("created_at").exists());
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForArticleReadInvalidInputs")
    void Article??????_Admin_???????????????(Object id) throws Exception {
        // given
        Object readId = id;

        // when
        ResultActions result = getAdminArticleReadTransaction(readId);

        // then
        result.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> paramsForArticleReadInvalidInputs(){
        return Stream.of(
                Arguments.of("abc"),
                Arguments.of(Long.MIN_VALUE),
                Arguments.of(true)
        );
    }

    private ResultActions getPublicArticleReadTransaction(Object readId) throws Exception {
        return this.mockMvc.perform(
                get("/api/wiki/public/article?id={readId}",readId)
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .accept(MediaType.ALL_VALUE)
        );
    }

    private ResultActions getAdminArticleReadTransaction(Object readId) throws Exception {
        return this.mockMvc.perform(
                get("/api/wiki/article?id={readId}",readId)
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
