package com.tys.openquant.wiki.menutree;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tys.openquant.user.dto.LoginDto;
import com.tys.openquant.wiki.repository.ArticleIntegrationRepository;
import com.tys.openquant.wiki.repository.CategoryIntegrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import javax.servlet.ServletException;
import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class MenuTreeReadTest {
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

    @Test
    @Transactional
    void 메뉴트리_일반사용자_전체목록조회() throws Exception {
        // given & when
        ResultActions result = this.mockMvc.perform(
                get("/api/wiki/public/menu-tree")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("data").exists());
    }

    @Test
    @Transactional
    void 메뉴트리_관리자_전체목록조회() throws Exception{
        // given & when
        ResultActions result = this.mockMvc.perform(
                get("/api/wiki/public/menu-tree")
                        .header(HttpHeaders.AUTHORIZATION, authStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("data").exists());
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
