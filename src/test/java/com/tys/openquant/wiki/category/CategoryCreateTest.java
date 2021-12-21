package com.tys.openquant.wiki.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.tys.openquant.user.dto.LoginDto;
import com.tys.openquant.util.builder.StringAppendBuilder;
import com.tys.openquant.wiki.dto.WikiDto;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class CategoryCreateTest {
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
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
    @MethodSource("paramsForCategoryCreateValidInputs")
    void 카테고리생성_정상입력(String name, String categoryIcon) throws Exception {
        // given
        WikiDto.NewCategory newCategory = WikiDto.NewCategory.builder()
                .name(name)
                .categoryIcon(categoryIcon)
                .build();

        // when
        ResultActions result = getCategoryCreateTransaction(newCategory);

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_created").value(true));
    }

    private static Stream<Arguments> paramsForCategoryCreateValidInputs(){
        return Stream.of(
                Arguments.of("Test Code Category Test", "TestCode")
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForCategoryCreateEmptyInputs")
    void 카테고리생성_미입력(String name, String categoryIcon) throws Exception{
        // given
        WikiDto.NewCategory newCategory = WikiDto.NewCategory.builder()
                .name(name)
                .categoryIcon(categoryIcon)
                .build();

        // when
        ResultActions result = getCategoryCreateTransaction(newCategory);

        // then
        result.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> paramsForCategoryCreateEmptyInputs(){
        return Stream.of(
                Arguments.of("", "TestCode"),
                Arguments.of("Test Code Category Test", ""),
                Arguments.of("", "")
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForCategoryCreateInvalidInputs")
    void 카테고리생성_비정상입력(String name, String categoryIcon) throws Exception{
        // given
        WikiDto.NewCategory newCategory = WikiDto.NewCategory.builder()
                .name(name)
                .categoryIcon(categoryIcon)
                .build();

        // when
        ResultActions result = getCategoryCreateTransaction(newCategory);

        // then
        result.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> paramsForCategoryCreateInvalidInputs(){
        String maxOverNameLen = StringAppendBuilder.getStrMultipliedByLength("1",201);
        String maxOverIconLen = StringAppendBuilder.getStrMultipliedByLength("2",501);

        return Stream.of(
                Arguments.of(maxOverNameLen, "TestCode"),
                Arguments.of("Test Code Category Test", maxOverIconLen )
        );
    }

    private ResultActions getCategoryCreateTransaction(WikiDto.NewCategory newCategory) throws Exception {
        return this.mockMvc.perform(
                post("/api/wiki/category/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .content(objectMapper.writeValueAsString(newCategory))
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
