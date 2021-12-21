package com.tys.openquant.wiki.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.tys.openquant.domain.wiki.Article;
import com.tys.openquant.user.dto.LoginDto;
import com.tys.openquant.util.builder.StringAppendBuilder;
import com.tys.openquant.wiki.dto.WikiDto;
import com.tys.openquant.wiki.dto.WikiTestDto;
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
public class ArticleUpdateTest {
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
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
    @MethodSource("paramsForArticleUpdateValidInputs")
    void Article등록_정상입력(boolean publiced, String title, String contents, String overview) throws Exception {
        // given
        Article findArticle = articleIntegrationRepository.findTopByDelNyOrderByIdAsc(false);
        WikiDto.ArticleUpdate article = WikiDto.ArticleUpdate.builder()
                .id(findArticle.getId())
                .publiced(publiced)
                .categoryId(findArticle.getCategory().getId())
                .title(title)
                .contents(contents)
                .overview(overview)
                .build();

        // when
        ResultActions result = getArticleUpdateTransaction(article);

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_updated").exists());

    }

    private static Stream<Arguments> paramsForArticleUpdateValidInputs(){
        return Stream.of(
                Arguments.of(true, "article update test", "contents update test", "overview update test")
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForArticleUpdateIdCheckInputs")
    void Article등록_아이디검증(Object id, Object categoryId, Object publiced, Object title, Object contents, Object overview) throws Exception {
        // given
        WikiTestDto.ArticleUpdateRequest requestField = new WikiTestDto.ArticleUpdateRequest(id, categoryId, publiced, title, contents, overview);

        // when
        ResultActions result = getArticleUpdateTransaction(requestField);

        // then
        result.andExpect(status().isBadRequest());

    }

    private static Stream<Arguments> paramsForArticleUpdateIdCheckInputs(){
        return Stream.of(
                Arguments.of(null, 8L, true, "article update test", "contents update test", "overview available empty field"),
                Arguments.of(-1L, 8L, true, "article update test", "contents update test", "overview available empty field"),
                Arguments.of("abc", 8L, true, "article update test", "contents update test", "overview available empty field"),
                Arguments.of(10L, -1L, true, "article update test", "contents update test", "overview available empty field"),
                Arguments.of(10L, null, true, "article update test", "contents update test", "overview available empty field"),
                Arguments.of(10L, "abc", true, "article update test", "contents update test", "overview available empty field")
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForArticleUpdateInvalidInputs")
    void Article등록_비정상입력(Object publiced, Object title, Object contents, Object overview) throws Exception {
        // given
        Article findArticle = articleIntegrationRepository.findTopByDelNyOrderByIdAsc(false);
        WikiTestDto.ArticleUpdateRequest requestField = new WikiTestDto.ArticleUpdateRequest(
                findArticle.getId(), findArticle.getCategory().getId(), publiced, title, contents, overview);

        // when
        ResultActions result = getArticleUpdateTransaction(requestField);

        // then
        result.andExpect(status().isBadRequest());

    }

    private static Stream<Arguments> paramsForArticleUpdateInvalidInputs(){
        String maxOverTitle = StringAppendBuilder.getStrMultipliedByLength("1", ArticleFieldSize.TITLE_MAX.getValue()+1);
        String maxOverContents = StringAppendBuilder.getStrMultipliedByLength("2", ArticleFieldSize.CONTENTS_MAX.getValue()+1);
        String maxOverOverview = StringAppendBuilder.getStrMultipliedByLength("3", ArticleFieldSize.OVERVIEW_MAX.getValue()+1);

        return Stream.of(
                Arguments.of("abc", "article update test", "contents update test", "overview available empty field"),
//                Arguments.of(1L, "article update test", "contents update test", "overview available empty field"), -> publiced = true로 들어감
                Arguments.of(true, maxOverTitle, "contents update test", "overview available empty field"),
//                Arguments.of(true, -1L, "contents update test", "overview available empty field"), //-> title = '1'로 들어감
//                Arguments.of(true, true, "contents update test", "overview available empty field"), -> title = 'true'로 들어감
                Arguments.of(true, "article update test", maxOverContents, "overview available empty field"),
//                Arguments.of(true, "article update test", true, "overview available empty field"),
//                Arguments.of(true, "article update test", 1L, "overview available empty field"),
                Arguments.of(true, "article update test", "contents update test", maxOverOverview)
//                Arguments.of(true, "article update test", "contents update test", true),
//                Arguments.of(true, "article update test", "contents update test", 1L)
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForArticleUpdateEmptyInputs")
    void Article등록_미입력(Boolean publiced, String title, String contents, String overview) throws Exception {
        // given
        Article findArticle = articleIntegrationRepository.findTopByDelNyOrderByIdAsc(false);
        WikiDto.ArticleUpdate article = WikiDto.ArticleUpdate.builder()
                .id(findArticle.getId())
                .publiced(publiced)
                .categoryId(findArticle.getCategory().getId())
                .title(title)
                .contents(contents)
                .overview(overview)
                .build();

        // when
        ResultActions result = getArticleUpdateTransaction(article);

        // then
        result.andExpect(status().isBadRequest());

    }

    private static Stream<Arguments> paramsForArticleUpdateEmptyInputs(){
        return Stream.of(
                Arguments.of(null, "article update test", "contents update test", "overview available empty field"),
                Arguments.of(true, "", "contents update test", "overview available empty field"),
                Arguments.of(true, "article update test", "", "overview available empty field")
        );
    }

    private ResultActions getArticleUpdateTransaction(WikiDto.ArticleUpdate article) throws Exception {
        return this.mockMvc.perform(
                post("/api/wiki/article/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .content(objectMapper.writeValueAsString(article))
        );
    }

    private ResultActions getArticleUpdateTransaction(WikiTestDto.ArticleUpdateRequest article) throws Exception {
        return this.mockMvc.perform(
                post("/api/wiki/article/update")
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
