package com.tys.openquant.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tys.openquant.user.dto.LoginDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.security.config.BeanIds;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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

@SpringBootTest
public class UserAuthTest {
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    private String authorizationStr;

    @BeforeEach
    public void setUp(WebApplicationContext context) throws Exception {
        DelegatingFilterProxy delegateProxyFilter = new DelegatingFilterProxy();
        delegateProxyFilter.init( new MockFilterConfig(context.getServletContext(), BeanIds.SPRING_SECURITY_FILTER_CHAIN));


        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(delegateProxyFilter)
                .build();

        ResultActions loginResult = getPrevTestTokenResult(
                LoginDto.Req.builder()
                .id("admin")
                .pwd("admin123!@#")
                .build()
        );

        MvcResult mvcResult = loginResult.andReturn();
        authorizationStr = mvcResult.getResponse().getHeader("Authorization");
    }

    @Test
    @Transactional
    public void 회원인증_정상토큰() throws Exception {
        // give & when
        System.out.println("before each 결과: "+ authorizationStr);
        ResultActions result = getAuthorizedUser();

        // @TODO

    }

    // @TODO (method naming 수정)
    private ResultActions getAuthorizedUser() throws Exception {
        return this.mockMvc.perform(
                get("/api/auth")
                        .header("Authorization",authorizationStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );
    }

    private ResultActions getPrevTestTokenResult(LoginDto.Req loginReq) throws Exception {
        return this.mockMvc.perform(
                post("/api/login")
                        .content(objectMapper.writeValueAsString(loginReq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );
    }
}
