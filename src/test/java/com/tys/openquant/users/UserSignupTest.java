package com.tys.openquant.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tys.openquant.user.dto.UserDto;
import com.tys.openquant.util.builder.StringAppendBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.ServletException;
import javax.transaction.Transactional;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserSignupTest {

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

    // @Test
    @Transactional
    public void 회원가입요청_정상입력() throws Exception {
        // given
        UserDto.Signup testSignup = UserDto.Signup.builder()
                .id("test123123")
                .pwd("test123!@#")
                .email("abc@naver.com")
                .build();

        // when
        ResultActions result = getPostResult(testSignup);

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("is_signuped").isBoolean());
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForSignupNotFillInputs")
    public void 회원가입_미입력값_검증(String userId, String password, String email) throws Exception {
        // given
        UserDto.Signup testSignup = UserDto.Signup.builder()
                .id(userId)
                .pwd(password)
                .email(email)
                .build();

        // when
        ResultActions result = getPostResult(testSignup);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists());
    }

    private static Stream<Arguments> paramsForSignupNotFillInputs(){
        return Stream.of(
                Arguments.of("", "test123!@#", "abc@naver.com"),
                Arguments.of("testNotFill1","","abc@naver.com"),
                Arguments.of("testNotFill2","test123!@#","")
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForSignupInvalidInputs")
    public void 회원가입_비정상입력값_검증(String userId, String password, String email) throws Exception{
        // given
        UserDto.Signup testSignup = UserDto.Signup.builder()
                .id(userId)
                .pwd(password)
                .email(email)
                .build();

        // when
        ResultActions result = getPostResult(testSignup);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").exists())
                .andExpect(jsonPath("message").exists());

    }

    private static Stream<Arguments> paramsForSignupInvalidInputs(){
        String minId = StringAppendBuilder.getStrMultipliedByLength("2",2);
        String maxId = StringAppendBuilder.getStrMultipliedByLength("50",26);
        String minPwd = StringAppendBuilder.getStrMultipliedByLength("8",8);
        String maxPwd = StringAppendBuilder.getStrMultipliedByLength("50",26);
        String minEmail = StringAppendBuilder.getStrMultipliedByLength("7", 7);
        String maxEmail = StringAppendBuilder.getStrMultipliedByLength("50",25);

        return Stream.of(
                // ID check 부터 해봅시다
                Arguments.of(minId, "test123!@#", "abc@naver.com"), // 3글자 미만 체크
                Arguments.of(maxId, "test123!@#", "abc@naver.com"), // 50글자 초과 체크
                Arguments.of("한글체크", "test123!@#", "abc@naver.com"), // 영어를 제외한 언어 체크
                Arguments.of("!@@##$$@!#___@#", "test123!@#", "abc@naver.com"), // 특수문자 체크
                Arguments.of("testNot Fill15", "abc123!#","테스트 naver.com"), // blank가 들어간 경우

                // password check 진행
                Arguments.of("testNotFill1",minPwd,"abc@naver.com"), // 8글자 미만 체크
                Arguments.of("testNotFill2",maxPwd,"abc@naver.com"), // 50글자 초과 체크
                Arguments.of("testNotFill3","1234!@#","abc@naver.com"), // 영(대소문구분x) + 숫자 + 특수문자 조합이 깨지는 경우, 영어
                Arguments.of("testNotFill4","abc!@#$","abc@naver.com"), // 영(대소문구분x) + 숫자 + 특수문자 조합이 깨지는 경우, 숫자
                Arguments.of("testNotFill5","abc123123","abc@naver.com"), // 영(대소문구분x) + 숫자 + 특수문자 조합이 깨지는 경우, 특수문자
                Arguments.of("testNotFill6","abc123123한글낌","abc@naver.com"), // 영(대소문구분x) + 숫자 + 특수문자 조합에서 해당하지 않는 언어 사용
                Arguments.of("testNotFill15", "abc123 !#","테스트 naver.com"), // blank가 들어간 경우

                //email check 진행
                Arguments.of("testNotFill7", "abc123!#",minEmail), // 7글자 미만 체크
                Arguments.of("testNotFill8", "abc123!#",maxEmail), // 50글자 초과 체크
                Arguments.of("testNotFill9", "abc123!#","test@naver"), // @이후 도메인이 명확하지 않은 경우
                Arguments.of("testNotFill10", "abc123!#","email#$!)(test@naver.com"), // -_.를 제외한 특수문자가 껴있는 경우1
                Arguments.of("testNotFill11", "abc123!#","emailtest@nave!#$r.com"), // -_.를 제외한특수문자가 껴있는 경우2
                Arguments.of("testNotFill12", "abc123!#","emailtest@naver.com!#$"), // -_.를 제외한특수문자가 껴있는 경우3
                Arguments.of("testNotFill13", "abc123!#","테스트@naver.com"), // 영어 외의 문자가 들어간 경우
                Arguments.of("testNotFill14", "abc123!#","테스트naver.com"), // "@"가 안 들어간 경우
                Arguments.of("testNotFill15", "abc123!#","test@ naver.com") // blank가 들어간 경우
        );
    }

    private ResultActions getPostResult(UserDto.Signup testSignup) throws Exception {
        return this.mockMvc.perform(
                post("/api/signup")
                        .content(objectMapper.writeValueAsString(testSignup))
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
