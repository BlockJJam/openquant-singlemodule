package com.tys.openquant.doc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tys.openquant.doc.repository.wiki.ArticleTestRepository;
import com.tys.openquant.doc.repository.wiki.CategoryTestRepository;
import com.tys.openquant.domain.wiki.Article;
import com.tys.openquant.domain.wiki.Category;
import com.tys.openquant.user.dto.LoginDto;
import com.tys.openquant.wiki.dto.WikiDto;
import com.tys.openquant.wiki.repository.ArticleRepository;
import com.tys.openquant.wiki.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.config.BeanIds;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.ServletException;
import javax.transaction.Transactional;

import java.util.List;

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
public class WikiDocTest {
    private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;
    @Autowired private ArticleTestRepository articleTestRepository;
    @Autowired private CategoryTestRepository categoryTestRepository;

    private String authStr;

    @BeforeEach
    void setUp(WebApplicationContext context,
               RestDocumentationContextProvider provider) throws Exception {
        DelegatingFilterProxy proxyFilter = new DelegatingFilterProxy();
        proxyFilter.init( new MockFilterConfig(context.getServletContext(),
                BeanIds.SPRING_SECURITY_FILTER_CHAIN));

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(provider))
                .addFilter(proxyFilter)
                .build();

        authStr = getAuthTokenInHeaderAfterLogin();
    }

    @Test
    @Transactional
    void 일반유저_메뉴트리목록조회_API문서() throws Exception {
        // given & when
        ResultActions result = this.mockMvc.perform(
                get("/api/wiki/public/menu-tree")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("data").isArray())
                .andExpect(jsonPath("$.data[0].id").isNumber())
                .andExpect(jsonPath("$.data[0].index").isNumber())
                .andExpect(jsonPath("$.data[0].name").isString())
                .andExpect(jsonPath("$.data[0].category_icon").isString())
                .andExpect(jsonPath("$.data[0].submenu_list").isArray())
                .andExpect(jsonPath("$.data[0].submenu_list[0].id").isNumber())
                .andExpect(jsonPath("$.data[0].submenu_list[0].index").isNumber())
                .andExpect(jsonPath("$.data[0].submenu_list[0].article_name").isString())
                .andDo(document("wiki-public-menutree",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                subsectionWithPath("data").type(JsonFieldType.ARRAY).description("일반 유저가 조회하는 메뉴트리 리스트 정보"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("메뉴트리의 카테고리 id 정보"),
                                fieldWithPath("data[].index").type(JsonFieldType.NUMBER).description("메뉴트리의 카테고리 순서 정보"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("메뉴트리의 카테고리 이름 정보"),
                                fieldWithPath("data[].category_icon").type(JsonFieldType.STRING).description("메뉴트리의 카테고리 아이콘 이미지 정보"),
                                subsectionWithPath("data[].submenu_list").type(JsonFieldType.ARRAY).description("메뉴트리의 카테고리 안에 있는 서브메뉴(article)"),
                                fieldWithPath("data[].submenu_list[].id").type(JsonFieldType.NUMBER).description("메뉴트리의 서브메뉴 id 정보"),
                                fieldWithPath("data[].submenu_list[].index").type(JsonFieldType.NUMBER).description("메뉴트리의 서브메뉴 순서 정보"),
                                fieldWithPath("data[].submenu_list[].article_name").type(JsonFieldType.STRING).description("메뉴트리의 글 제목 정보")
                                )
                ));

    }

    @Test
    @Transactional
    void 일반유저_서브메뉴_글상세보기_API문서() throws Exception{
        // given
        Long id = articleTestRepository.findTopByDelNyOrderByIdAsc(false).getId();

        // when
        ResultActions result = this.mockMvc.perform(
                get("/api/wiki/public/article?id={id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("id").isNumber())
                .andExpect(jsonPath("category_id").isNumber())
                .andExpect(jsonPath("title").isString())
                .andExpect(jsonPath("contents").isString())
                .andExpect(jsonPath("updated_at").isString())
                .andExpect(jsonPath("overview").isString())
                .andDo(document("wiki-public-article",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("id").description("서브메뉴 글에 해당하는 id정보")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("서브메뉴 글의 id 정보"),
                                fieldWithPath("category_id").type(JsonFieldType.NUMBER).description("서브메뉴 글의 카테고리 id 정보"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("서브메뉴 글의 제목 정보"),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("서브메뉴 글의 컨텐츠 내용 정보"),
                                fieldWithPath("overview").type(JsonFieldType.STRING).description("서브메뉴 글의 컨텐츠 내용의 Header 목록 정보"),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("서브메뉴 글의 생성 날짜 정보"),
                                fieldWithPath("updated_at").type(JsonFieldType.STRING).description("서브메뉴 글의 업데이트 날짜 정보")
                        )
                ));
    }

    @Test
    @Transactional
    void 관리자_메뉴트리_목록조회_API문서() throws Exception {
        // given
        

        // when
        ResultActions result = this.mockMvc.perform(
                get("/api/wiki/menu-tree")
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("data").isArray())
                .andExpect(jsonPath("$.data[0].id").isNumber())
                .andExpect(jsonPath("$.data[0].index").isNumber())
                .andExpect(jsonPath("$.data[0].name").isString())
                .andExpect(jsonPath("$.data[0].category_icon").isString())
                .andExpect(jsonPath("$.data[0].submenu_list").isArray())
                .andExpect(jsonPath("$.data[0].submenu_list[0].id").isNumber())
                .andExpect(jsonPath("$.data[0].submenu_list[0].index").isNumber())
                .andExpect(jsonPath("$.data[0].submenu_list[0].article_name").isString())
                .andExpect(jsonPath("$.data[0].submenu_list[0].is_public").isBoolean())
                .andDo(document("wiki-menutree",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                subsectionWithPath("data").type(JsonFieldType.ARRAY).description("일반 유저가 조회하는 메뉴트리 리스트 정보"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("메뉴트리의 카테고리 id 정보"),
                                fieldWithPath("data[].index").type(JsonFieldType.NUMBER).description("메뉴트리의 카테고리 순서 정보"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("메뉴트리의 카테고리 이름 정보"),
                                fieldWithPath("data[].category_icon").type(JsonFieldType.STRING).description("메뉴트리의 카테고리 아이콘 이미지 정보"),
                                subsectionWithPath("data[].submenu_list").type(JsonFieldType.ARRAY).description("메뉴트리의 카테고리 안에 있는 서브메뉴(article)"),
                                fieldWithPath("data[].submenu_list[].id").type(JsonFieldType.NUMBER).description("메뉴트리의 서브메뉴 id 정보"),
                                fieldWithPath("data[].submenu_list[].index").type(JsonFieldType.NUMBER).description("메뉴트리의 서브메뉴 순서 정보"),
                                fieldWithPath("data[].submenu_list[].article_name").type(JsonFieldType.STRING).description("메뉴트리의 글 이름 정보"),
                                fieldWithPath("data[].submenu_list[].is_public").type(JsonFieldType.BOOLEAN).description("메뉴트리의 서브메뉴 공개여부")
                        )
                )
         );
    }

    @Test
    @Transactional
    void 관리자_서브메뉴_글상세보기_API문서() throws Exception {
        // given
        Long id = articleTestRepository.findTopByDelNyOrderByIdAsc(false).getId();;

        // when
        ResultActions result = this.mockMvc.perform(
                get("/api/wiki/public/article?id={id}", id)
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("id").isNumber())
                .andExpect(jsonPath("category_id").isNumber())
                .andExpect(jsonPath("title").isString())
                .andExpect(jsonPath("contents").isString())
                .andExpect(jsonPath("updated_at").isString())
                .andExpect(jsonPath("overview").isString())
                .andDo(document("wiki-article",
                        getDocumentRequest(),
                        getDocumentResponse(),requestHeaders(
                                headerWithName("Authorization").description("해당 로그인된 유저의 토큰으로 Authorization 헤더 구성")
                        ),
                        requestParameters(
                                parameterWithName("id").description("서브메뉴 글에 해당하는 id정보")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("서브메뉴 글의 id 정보"),
                                fieldWithPath("category_id").type(JsonFieldType.NUMBER).description("서브메뉴 글의 카테고리 id 정보"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("서브메뉴 글의 제목 정보"),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("서브메뉴 글의 컨텐츠 내용 정보"),
                                fieldWithPath("overview").type(JsonFieldType.STRING).description("서브메뉴 글의 컨텐츠 내용의 Header 목록 정보"),
                                fieldWithPath("created_at").type(JsonFieldType.STRING).description("서브메뉴 글의 생성 날짜 정보"),
                                fieldWithPath("updated_at").type(JsonFieldType.STRING).description("서브메뉴 글의 업데이트 날짜 정보")
                        )
                ));
    }

    @Test
    @Transactional
    void 카테고리_생성_API문서() throws Exception {
        // given
        
        WikiDto.NewCategory category = WikiDto.NewCategory.builder()
                .name("testCode")
                .categoryIcon("home")
                .build();

        // when
        ResultActions result = this.mockMvc.perform(
                post("/api/wiki/category/create")
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .content(objectMapper.writeValueAsString(category))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_created").isBoolean())
                .andDo(document("wiki-category-create",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("해당 로그인된 유저의 토큰으로 Authorization 헤더 구성")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("생성할 카테고리 이름"),
                                fieldWithPath("category_icon").type(JsonFieldType.STRING).description("생성할 카테고리 ")
                        ),
                        responseFields(
                                fieldWithPath("is_created").type(JsonFieldType.BOOLEAN).description("카테고리의 생성 여부")
                        )
                ));
    }

    @Test
    @Transactional
    void 카테고리_내용수정_API문서() throws Exception {
        // given
        WikiDto.CategoryRename rename = WikiDto.CategoryRename.builder()
                .id(categoryTestRepository.findTopByOrderByIdDesc().getId())
                .name("testUpdate")
                .categoryIcon("enterprise")
                .build();

        // when
        ResultActions result = this.mockMvc.perform(
                post("/api/wiki/category/rename")
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
                        .content(objectMapper.writeValueAsString(rename))
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_updated").isBoolean())
                .andDo(document("wiki-category-rename",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("해당 로그인된 유저의 토큰으로 Authorization 헤더 구성")
                        ),
                        requestFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("업데이트할 카테고리의 id"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("업데이트할 카테고리의 이름"),
                                fieldWithPath("category_icon").type(JsonFieldType.STRING).description("업데이트할 카테고리의 아이콘")
                        ),
                        responseFields(
                                fieldWithPath("is_updated").type(JsonFieldType.BOOLEAN).description("카테고리 업데이트 여부")
                        )
                ));

    }

    @Test
    @Transactional
    void 카테고리_삭제_API문서() throws Exception {
        // given
        Long id = saveCategoryReturnId();

        // when
        ResultActions result = this.mockMvc.perform(
                get("/api/wiki/category/delete?id={id}",id )
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .accept(MediaType.ALL_VALUE)
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_deleted").isBoolean())
                .andDo(document("wiki-category-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("해당 로그인된 유저의 토큰으로 Authorization 헤더 구성")
                        ),
                        requestParameters(
                                parameterWithName("id").description("삭제하기 위한 카테고리 id")
                        ),
                        responseFields(
                                fieldWithPath("is_deleted").type(JsonFieldType.BOOLEAN).description("카테고리 삭제 성공 여부")
                        )
                ));
    }

    @Test
    @Transactional
    void Article_생성_API문서() throws Exception {
        // given
        WikiDto.NewArticle article = WikiDto.NewArticle.builder()
                .publiced(false)
                .categoryId(categoryTestRepository.findTopByOrderByIdDesc().getId())
                .title("api test")
                .contents("api test overview")
                .overview("api test contents")
                .build();

        // when
        ResultActions result = this.mockMvc.perform(
                post("/api/wiki/article/register")
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(article))
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_registered").isBoolean())
                .andDo(document("wiki-article-register",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("해당 로그인된 유저의 토큰으로 Authorization 헤더 구성")
                        ),
                        requestFields(
                                fieldWithPath("category_id").type(JsonFieldType.NUMBER).description("Article이 생성될 Category id정보"),
                                fieldWithPath("is_public").type(JsonFieldType.BOOLEAN).description("Article의 공개여부"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("Article의 title 정보"),
                                fieldWithPath("overview").type(JsonFieldType.STRING).description("Article의 헤더 목록"),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("Article의 contents 정보")
                        ),
                        responseFields(
                                fieldWithPath("is_registered").type(JsonFieldType.BOOLEAN).description("Article의 생성 성공 여부")
                        )
                ));
    }

    @Test
    @Transactional
    void Article_수정_API문서() throws Exception {
        // given
        WikiDto.ArticleUpdate articleUpdate = WikiDto.ArticleUpdate.builder()
                .id(articleTestRepository.findTopByDelNyOrderByIdAsc(false).getId())
                .categoryId(categoryTestRepository.findTopByOrderByIdDesc().getId())
                .title("update new title")
                .overview("new header list")
                .contents("new contents")
                .publiced(true)
                .build();

        // when
        ResultActions result = this.mockMvc.perform(
                post("/api/wiki/article/update")
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleUpdate))
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_updated").isBoolean())
                .andDo(document("wiki-article-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("해당 로그인 유저의 토큰정보가 담긴 헤더")
                        ),
                        requestFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("수정 대상 Article의 id정보"),
                                fieldWithPath("category_id").type(JsonFieldType.NUMBER).description("수정 대상 Article의 카테고리 id정보"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("수정 대상 Article의 title 정보"),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("수정 대상 Article의 contents 정보"),
                                fieldWithPath("overview").type(JsonFieldType.STRING).description("수정 대상 Article의 헤더 목록 정보"),
                                fieldWithPath("is_public").type(JsonFieldType.BOOLEAN).description("수정 대상 Article의 공개여부정보")
                        ),
                        responseFields(
                                fieldWithPath("is_updated").type(JsonFieldType.BOOLEAN).description("Article 수정 성공 여부")
                        )
                ));
    }

    @Test
    @Transactional
    void Article_삭제_API문서() throws Exception{
        // given
        Long sampleArticleId = articleTestRepository.findTopByDelNyOrderByIdAsc(false).getId();

        // when
        ResultActions result = this.mockMvc.perform(
                get("/api/wiki/article/delete?id={sampleArticleId}", sampleArticleId)
                        .header(HttpHeaders.AUTHORIZATION,authStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_deleted").isBoolean())
                .andDo(document("wiki-article-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("해당 로그인 유저의 토큰정보가 담긴 헤더")
                        ),
                        requestParameters(
                                parameterWithName("id").description("삭제할 Article의 id 정보")
                        ),
                        responseFields(
                                fieldWithPath("is_deleted").type(JsonFieldType.BOOLEAN).description("Article의 삭제 성공 여부")
                        )
                ));
    }

    @Test
    @Transactional
    void 메뉴트리_편집_API문서() throws Exception {
        // given
        final Long GARBAGE_INDEX = -1L;
        WikiDto.MenuTree findMenuTree = new WikiDto.MenuTree();
        findMenuTree.createAdminMenuTree(
                categoryTestRepository.findCategoriesByIdxNotOrderByIdxAsc(GARBAGE_INDEX)
        );
        List<WikiDto.CategoryMenu> menuTree = findMenuTree.getData();

        // when
        ResultActions result = this.mockMvc.perform(
                post("/api/wiki/category/update")
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
                        .content(objectMapper.writeValueAsString(menuTree))
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_updated").isBoolean())
                .andDo(document("wiki-category-update",
                        requestHeaders(
                                headerWithName("Authorization").description("해당 로그인 유저의 토큰 정보")
                        ),
                        requestFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("메뉴트리의 카테고리 id 정보"),
                                fieldWithPath("[].index").type(JsonFieldType.NUMBER).description("메뉴트리의 카테고리 순서 정보"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("메뉴트리의 카테고리 이름 정보"),
                                fieldWithPath("[].category_icon").type(JsonFieldType.STRING).description("메뉴트리의 카테고리 아이콘 이미지 정보"),
                                subsectionWithPath("[].submenu_list").type(JsonFieldType.ARRAY).description("메뉴트리의 카테고리 안에 있는 서브메뉴(article)"),
                                fieldWithPath("[].submenu_list[].id").type(JsonFieldType.NUMBER).description("메뉴트리의 서브메뉴 id 정보"),
                                fieldWithPath("[].submenu_list[].index").type(JsonFieldType.NUMBER).description("메뉴트리의 서브메뉴 순서 정보"),
                                fieldWithPath("[].submenu_list[].article_name").type(JsonFieldType.STRING).description("메뉴트리의 글 이름 정보"),
                                fieldWithPath("[].submenu_list[].is_public").type(JsonFieldType.BOOLEAN).description("메뉴트리의 서브메뉴 공개여부")
                        ),
                        responseFields(
                                fieldWithPath("is_updated").type(JsonFieldType.BOOLEAN).description("메뉴 트리 구조 편집에 대한 성공 여부")
                        )
                ));
    }

    private Long saveCategoryReturnId(){
        Category category = Category.builder()
                .idx(categoryTestRepository.count())
                .categoryName("deleted_test")
                .categoryIcon("-")
                .build();

        Long ret = categoryTestRepository.saveAndFlush(category).getId();

        return ret;
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
