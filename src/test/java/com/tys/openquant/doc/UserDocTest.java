package com.tys.openquant.doc;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tys.openquant.user.dto.LoginDto;
import com.tys.openquant.user.dto.UserDto;
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
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestPartsSnippet;
import org.springframework.security.config.BeanIds;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;


import javax.servlet.ServletException;

import static com.tys.openquant.APIDocumentUtils.getDocumentRequest;
import static com.tys.openquant.APIDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class,SpringExtension.class})
//@WebMvcTest(controllers = {AuthController.class, UserController.class})
@SpringBootTest
@AutoConfigureRestDocs(uriScheme ="https", uriHost = "docs.api.com")
@ContextConfiguration
public class UserDocTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;



    @BeforeEach
    void setUp(WebApplicationContext context,
                      RestDocumentationContextProvider provider) throws ServletException {
        DelegatingFilterProxy delegateProxyFilter = new DelegatingFilterProxy();
        delegateProxyFilter.init( new MockFilterConfig(context.getServletContext(), BeanIds.SPRING_SECURITY_FILTER_CHAIN));


        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(provider))
                .addFilter(delegateProxyFilter)
                .build();
    }


    @Test
    void 로그인_API문서() throws Exception {
        // given
        LoginDto.Req loginReq = LoginDto.Req.builder()
                .id("admin")
                .pwd("admin123!@#")
                .build();

        // when
        ResultActions result = this.mockMvc.perform(
                post("/api/login")
                        .content(objectMapper.writeValueAsString(loginReq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        // then( docs url: http://localhost:63342/openquant/BOOT-INF/classes/static/docs/user-login.html )
        result.andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION))
                .andDo(document("user-login",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("user_id").type(JsonFieldType.STRING).description("사용자 ID"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("사용자 Password")
                        )
                ));
    }

//     @Test
    @Transactional
    void 회원가입_API문서() throws Exception {
        // given
        UserDto.Signup signupReq = UserDto.Signup.builder()
                .id("apiSpecTest")
                .pwd("apitest123!@#")
                .email("jaemin.joo@tysystems.com")
                .build();

        //when
        ResultActions result = this.mockMvc.perform(
                post("/api/signup")
                        .content(objectMapper.writeValueAsString(signupReq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then( docs url:  http://localhost:63342/openquant/BOOT-INF/classes/static/docs/user-signup.html )
        result.andExpect(status().isOk())
                .andDo(document(
                        "user-signup",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("user_id").type(JsonFieldType.STRING).description("회원가입하는 유저 ID"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("영어, 숫자, 특수문자 꼭 포함이 된 유저 password"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("email 형식에 맞는 유저 email")
                        ),
                        responseFields(
                                fieldWithPath("is_signuped").type(JsonFieldType.BOOLEAN).description("회원가입이 정상적으로 완료되었는지 여부")
                        )
                ));
    }

    @Test
    @Transactional
    void 중복아이디검사_API문서() throws Exception {
        String id="admin";

        ResultActions result = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/signup/check/{user_id}",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_duplicated").isBoolean())
                .andDo(document(
                        "user-id-check",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        // RequestDocumentation의 pathParameters 사용
                        pathParameters(
                                parameterWithName("user_id").description("중복체크할 회원ID로 입력 검증 필터링 포함")
                        ),
                        // PayloadDocumentation의 responseFields 사용
                        responseFields(
                                fieldWithPath("is_duplicated").type(JsonFieldType.BOOLEAN).description("중복 회원ID 검사가 올바르게 이루어졌는지 여부")
                        )
                ));
    }

    @Test
    @Transactional
    void 회원정보요청_API문서() throws Exception{
        // given
        String authStr = getAuthTokenInHeaderAfterLogin();

        // when
        ResultActions result =  this.mockMvc.perform(
                get("/api/userinfo")
                        .header("Authorization",authStr)
                        .accept(MediaType.ALL_VALUE)
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("user_id").isString())
                .andExpect(jsonPath("email").isString())
                .andExpect(jsonPath("authority_set").isArray())
                .andDo(document(
                        "user-get-userinfo",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        //HeaderDocumentation의 requestHeader를 사용
                        requestHeaders(
                                headerWithName("Authorization").description("해당 로그인된 유저의 token으로 Authorization 헤더 구성")
                        ),
                        responseFields(
                                fieldWithPath("user_id").type(JsonFieldType.STRING).description("해당 토큰의 회원ID 정보"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("해당 토큰의 회원 Email 정보"),
                                subsectionWithPath("authority_set").type(JsonFieldType.ARRAY).description("해당 토큰의 회원 권한 Set 정보")
                        )
                ));

    }

    @Test
    @Transactional
    void 회원인증_API문서() throws Exception{
        // given
        String authStr = getAuthTokenInHeaderAfterLogin();

        // when
        ResultActions result = this.mockMvc.perform(
                get("/api/auth")
                        .header("Authorization", authStr)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("user_id").isString())
                .andExpect(jsonPath("authority_set").isArray())
                .andDo(document(
                   "user-auth",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("해당 로그인된 유저의 token으로 Authorization 헤더 구성")
                        ),
                        responseFields(
                                fieldWithPath("user_id").type(JsonFieldType.STRING).description("인증으로 검증된 회원의 ID정보"),
                                subsectionWithPath("authority_set").type(JsonFieldType.ARRAY).description("인증으로 검증된 회원의 권한정보")
                        )
                ));
    }

    @Test
    @Transactional
    void 회원수정접근요청_API문서() throws Exception{
        // given
        String authStr = getAuthTokenInHeaderAfterLogin();
        UserDto.AccessUpdateReq accessUpdateReq = UserDto.AccessUpdateReq.builder()
                .pwd("admin123!@#")
                .build();

        // when
        ResultActions result = this.mockMvc.perform(
                post("/api/userinfo/access-update")
                        .header("Authorization", authStr)
                        .content(objectMapper.writeValueAsString(accessUpdateReq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_access_update").isBoolean())
                .andDo(document(
                        "user-access-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("해당 로그인된 유저의 토큰으로 Authorization 헤더 구성")
                        ),
                        requestFields(
                                fieldWithPath("password").description("해당 유저의 패스워드 정보")
                        ),
                        responseFields(
                                fieldWithPath("is_access_update").description("회원 수정 페이지로 접근 가능한 유저인지 여부")
                        )
                ));
    }

    @Test
    @Transactional
    void 회원정보수정_API문서() throws Exception {
        // given
        String authStr = getAuthTokenInHeaderAfterLogin();
        UserDto.UpdateReq updateReq = UserDto.UpdateReq.builder()
                .email("update@naver.com")
                .pwd("admin321#@!")
                .build();

        // when
        ResultActions result = this.mockMvc.perform(
                post("/api/userinfo/update")
                        .header("Authorization", authStr)
                        .content(objectMapper.writeValueAsString(updateReq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_updated").isBoolean())
                .andDo(document(
                        "user-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("해당 로그인된 유저의 토큰으로 Authorization 헤더 구성")
                        ),
                        requestFields(
                                fieldWithPath("password").description("수정을 원하는 회원의 password 정보"),
                                fieldWithPath("email").description("수정을 원하는 회원의 email 정보")
                        ),
                        responseFields(
                                fieldWithPath("is_updated").description("회원 정보 수정 여부")
                        )
                ));
    }

    @Test
    @Transactional
    void 회원탈퇴_API문서() throws Exception {
        // given
        String authStr = getAuthTokenInHeaderAfterLogin();
        String deleteId = "admin";

        // when
        ResultActions result = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/userinfo/delete/{user_id}",deleteId)
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_deleted").isBoolean())
                .andDo(document("user-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("user_id").description("로그인한 유저 자신의 ID")
                        ),
                        responseFields(
                                fieldWithPath("is_deleted").type(JsonFieldType.BOOLEAN).description("해당 유저의 탈퇴 성공 여부")
                        )));
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

