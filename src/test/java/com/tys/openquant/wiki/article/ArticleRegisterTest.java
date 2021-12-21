package com.tys.openquant.wiki.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.tys.openquant.user.dto.LoginDto;
import com.tys.openquant.util.builder.StringAppendBuilder;
import com.tys.openquant.wiki.dto.WikiDto;
import com.tys.openquant.wiki.enums.ArticleFieldSize;
import com.tys.openquant.wiki.repository.ArticleIntegrationRepository;
import com.tys.openquant.wiki.repository.CategoryIntegrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ArticleRegisterTest {
    private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;
    @Autowired private CategoryIntegrationRepository categoryIntegrationRepository;
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

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForArticleRegisterValidInputs")
    void Article등록_정상입력(boolean publiced, String title, String contents, String overview) throws Exception {
        // given
        WikiDto.NewArticle article = WikiDto.NewArticle.builder()
                .publiced(publiced)
                .categoryId(categoryIntegrationRepository.findTopByOrderByIdDesc().getId())
                .title(title)
                .contents(contents)
                .overview(overview)
                .build();

        // when
        ResultActions result = getArticleRegisterTransaction(article);

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_registered").exists());

    }

    private static Stream<Arguments> paramsForArticleRegisterValidInputs(){
        return Stream.of(
                Arguments.of(true, "article register test", "contents register test", "overview register test")
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForArticleRegisterEmptyInputs")
    void Article등록_미입력(Boolean publiced, String title, String contents, String overview) throws Exception {
        // given
        WikiDto.NewArticle article = WikiDto.NewArticle.builder()
                .publiced(publiced)
                .categoryId(categoryIntegrationRepository.findTopByOrderByIdDesc().getId())
                .title(title)
                .contents(contents)
                .overview(overview)
                .build();

        // when
        ResultActions result = getArticleRegisterTransaction(article);

        // then
        result.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> paramsForArticleRegisterEmptyInputs(){
        return Stream.of(
                Arguments.of(null, "article register test", "contents register test", "overview register test"),
                Arguments.of(true, "", "contents register test", "overview register test"),
                Arguments.of(true, "article register test", "", "overview register test"),
                Arguments.of(true, "article register test", "contents register test", "")
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForArticleRegisterInvalidInputs")
    void Article등록_비정상입력(Boolean publiced, String title, String contents, String overview) throws Exception {
        // given
        WikiDto.NewArticle article = WikiDto.NewArticle.builder()
                .publiced(publiced)
                .categoryId(categoryIntegrationRepository.findTopByOrderByIdDesc().getId())
                .title(title)
                .contents(contents)
                .overview(overview)
                .build();

        // when
        ResultActions result = getArticleRegisterTransaction(article);

        // then
        result.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> paramsForArticleRegisterInvalidInputs(){
        String maxOverTitle = StringAppendBuilder.getStrMultipliedByLength("1", ArticleFieldSize.TITLE_MAX.getValue()+1);
        String maxOverContents = StringAppendBuilder.getStrMultipliedByLength("2", ArticleFieldSize.CONTENTS_MAX.getValue()+1);
        String maxOverOverview = StringAppendBuilder.getStrMultipliedByLength("3", ArticleFieldSize.OVERVIEW_MAX.getValue()+1);
        return Stream.of(
                Arguments.of(true, maxOverTitle, "contents register test", "overview register test"),
                Arguments.of(true, "article register test", maxOverContents, "overview register test"),
                Arguments.of(true, "article register test", "contents register test", maxOverOverview)
        );
    }

    private ResultActions getArticleRegisterTransaction(WikiDto.NewArticle article) throws Exception {
        return this.mockMvc.perform(
                post("/api/wiki/article/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .content(objectMapper.writeValueAsString(article))
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
