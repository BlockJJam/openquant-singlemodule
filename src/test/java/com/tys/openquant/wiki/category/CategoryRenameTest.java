package com.tys.openquant.wiki.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tys.openquant.user.dto.LoginDto;
import com.tys.openquant.util.builder.StringAppendBuilder;
import com.tys.openquant.wiki.dto.WikiDto;
import com.tys.openquant.wiki.repository.ArticleIntegrationRepository;
import com.tys.openquant.wiki.repository.CategoryIntegrationRepository;
import org.junit.jupiter.api.BeforeEach;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CategoryRenameTest {
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
    @MethodSource("paramsForCategoryRenameValidInputs")
    void 카테고리이름편집_정상입력( String name, String categoryIcon) throws Exception{
        // given
        WikiDto.CategoryRename category = WikiDto.CategoryRename.builder()
                .id(categoryIntegrationRepository.findTopByOrderByIdDesc().getId())
                .name(name)
                .categoryIcon(categoryIcon)
                .build();

        // when
        ResultActions result = getCategoryRenameTransaction(category);

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_updated").exists());
    }

    private static Stream<Arguments> paramsForCategoryRenameValidInputs(){
        return Stream.of(
                Arguments.of("Test Code Category Rename Test", "Rename Test" )
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForCategoryRenameIdInvalidInputs")
    void 카테고리이름편집_아이디_비정상입력(Long id, String name, String categoryIcon) throws Exception{
        // given
        WikiDto.CategoryRename category = WikiDto.CategoryRename.builder()
                .id(id)
                .name(name)
                .categoryIcon(categoryIcon)
                .build();

        // when
        ResultActions result = getCategoryRenameTransaction(category);

        // then
        result.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> paramsForCategoryRenameIdInvalidInputs(){
        long maxOverValue = Long.MAX_VALUE+1;
        long minusValue = -1L;
        return Stream.of(
                Arguments.of(maxOverValue,"Test Code Category Rename Test", "Rename Test" ),
                Arguments.of(minusValue ,"Test Code Category Rename Test", "Rename Test" ),
                Arguments.of(null ,"Test Code Category Rename Test", "Rename Test" )
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForCategoryRenameEmptyInputs")
    void 카테고리이름편집_미입력(String name, String categoryIcon) throws Exception{
        // given
        WikiDto.CategoryRename category = WikiDto.CategoryRename.builder()
                .id(categoryIntegrationRepository.findTopByOrderByIdDesc().getId())
                .name(name)
                .categoryIcon(categoryIcon)
                .build();

        // when
        ResultActions result = getCategoryRenameTransaction(category);

        // then
        result.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> paramsForCategoryRenameEmptyInputs(){
        return Stream.of(
                Arguments.of("Test Code Category Rename Test", null ),
                Arguments.of("Test Code Category Rename Test", "" ),
                Arguments.of("", "Rename Test" ),
                Arguments.of(null, "Rename Test" )
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForCategoryRenameInvalidInputs")
    void 카테고리이름편집_비정상입력(String name, String categoryIcon) throws Exception{
        // given
        WikiDto.CategoryRename category = WikiDto.CategoryRename.builder()
                .id(categoryIntegrationRepository.findTopByOrderByIdDesc().getId())
                .name(name)
                .categoryIcon(categoryIcon)
                .build();

        // when
        ResultActions result = getCategoryRenameTransaction(category);

        // then
        result.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> paramsForCategoryRenameInvalidInputs(){
        String maxOverNameLen = StringAppendBuilder.getStrMultipliedByLength("1",201);
        String maxOverIconLen = StringAppendBuilder.getStrMultipliedByLength("2",501);

        return Stream.of(
                Arguments.of("Test Code Category Rename Test", maxOverIconLen ),
                Arguments.of(maxOverNameLen, "Rename Test" )
        );
    }

    private ResultActions getCategoryRenameTransaction(WikiDto.CategoryRename category) throws Exception {
        return this.mockMvc.perform(
                post("/api/wiki/category/rename")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .content(objectMapper.writeValueAsString(category))
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
