package com.tys.openquant.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.tys.openquant.user.dto.LoginDto;
import com.tys.openquant.util.builder.StringAppendBuilder;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.ServletException;
import javax.transaction.Transactional;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserLoginTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(WebApplicationContext context) throws ServletException {
        DelegatingFilterProxy delegateProxyFilter = new DelegatingFilterProxy();
        delegateProxyFilter.init( new MockFilterConfig(context.getServletContext(), BeanIds.SPRING_SECURITY_FILTER_CHAIN));


        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(delegateProxyFilter)
                .build();
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForLoginValidInputs")
    public void 로그인_정상입력_테스트(String userId, String password) throws Exception {
        LoginDto.Req loginReq = LoginDto.Req.builder()
                .id(userId)
                .pwd(password)
                .build();

        ResultActions result = getPostResult(loginReq);

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.AUTHORIZATION));
    }

    private static Stream<Arguments> paramsForLoginValidInputs(){
        return Stream.of(
            Arguments.of("admin","admin123!@#")
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForLoginNoInputs")
    public void 로그인_미입력값_테스트(String userId, String password) throws Exception {
        LoginDto.Req loginReq = LoginDto.Req.builder()
                .id(userId)
                .pwd(password)
                .build();

        ResultActions result = getPostResult(loginReq);

        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists());
    }

    private static Stream<Arguments> paramsForLoginNoInputs(){
        return Stream.of(
                Arguments.of("","admin123!@#"),
                Arguments.of("admin2",""),
                Arguments.of("","")
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForLoginInvalidInputs")
    public void 로그인_비정상입력값_테스트(String userId, String password) throws Exception {
        LoginDto.Req loginReq = LoginDto.Req.builder()
                .id(userId)
                .pwd(password)
                .build();

        ResultActions result = getPostResult(loginReq);

        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists());
    }

    private static Stream<Arguments> paramsForLoginInvalidInputs(){
        String minId = StringAppendBuilder.getStrMultipliedByLength("2",2);//getStrForLenTest("2",2);
        String maxId = StringAppendBuilder.getStrMultipliedByLength("50",26);
        String minPwd = StringAppendBuilder.getStrMultipliedByLength("2",2);
        String maxPwd = StringAppendBuilder.getStrMultipliedByLength("50",26);
        return Stream.of(
                // ID check
                Arguments.of(minId,"admin123!@#"), // 길이가 3미만인 경우
                Arguments.of(maxId,"admin123!@#"), // 길이가 50초과인 경우
                Arguments.of("한글이들어가있네wow","admin123!@#"), // 영어를 이외의 언어가 들어간 경우
                Arguments.of("!@#wow!@#","admin123!@#"), // 특수문자가 들어간 경우
                Arguments.of("admin 123","admin123!@#"), // 공백이 들어가 있는 경우
                Arguments.of(" admin1 ","admin123!@#"), // 앞뒤에 공백이 들어가 있는 경우

                // Password check
                Arguments.of("admin1",minPwd), // 길이가 3미만인 경우
                Arguments.of("admin1",maxPwd), // 길이가 50초과인 경우
                Arguments.of("admin1","admin123!@#한글"), // 세가지 조합에 추가로 영어가 아닌 언어가 들어가는 경우
                Arguments.of("admin1","123123!@#!@#"), // 영어가 빠지는 경우
                Arguments.of("admin1","admin!@#!@#"), // 숫자가 빠지는 경우
                Arguments.of("admin1","admin123123") // 특수문자가 빠지는 경우
        );
    }



    private ResultActions getPostResult(LoginDto.Req loginReq) throws Exception {
        return this.mockMvc.perform(
                post("/api/login")
                        .content(objectMapper.writeValueAsString(loginReq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
        );
    }

    private static String getStrForLenTest(String inputStr, int inputSize) {
        StringBuilder sb = new StringBuilder("");
        for(int i=0; i<inputSize; i++){
            sb.append(inputStr);
        }

        return sb.toString();
    }
}
