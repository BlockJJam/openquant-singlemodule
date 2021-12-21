package com.tys.openquant.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tys.openquant.user.dto.LoginDto;
import com.tys.openquant.util.builder.StringAppendBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.config.BeanIds;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;


import javax.servlet.ServletException;
import javax.transaction.Transactional;
import java.text.Normalizer;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class UserDupCheckTest {
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
    @MethodSource("paramsForDuplicateCheckIdSuccessCaseInputs")
    void 회원가입_중복ID체크_사용가능ID_정상입력_테스트(String userId) throws Exception {
        // given & when
        ResultActions result = getMockGetResult(userId);

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_duplicated").isBoolean())
                .andExpect(jsonPath("is_duplicated").value(false));
    }

    private static Stream<Arguments> paramsForDuplicateCheckIdSuccessCaseInputs(){
        return Stream.of(
                Arguments.of("admin01Test"),
                Arguments.of("adminDupTest")
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForDuplicateCheckIdFailCaseInputs")
    void 회원가입_중복ID체크_중복되는ID_정상입력_테스트(String userId) throws Exception {
        // given & when
        ResultActions result = getMockGetResult(userId);

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("is_duplicated").isBoolean())
                .andExpect(jsonPath("is_duplicated").value(true));
    }

    private static Stream<Arguments> paramsForDuplicateCheckIdFailCaseInputs(){
        return Stream.of(
                Arguments.of("admin"),
                Arguments.of("admin2")
        );
    }

    @ParameterizedTest
    @Transactional
    @MethodSource("paramsForDuplicateCheckIdInvalidInputs")
    void 회원가입_중복ID체크_비정상입력값_테스트(String userId) throws Exception {
        // given & when
        ResultActions result = getMockGetResult(userId);

        //then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").isString())
                .andExpect(jsonPath("message").isString());
    }

    private static Stream<Arguments> paramsForDuplicateCheckIdInvalidInputs(){

        String minId = StringAppendBuilder.getStrMultipliedByLength("a",2);
        String maxId = StringAppendBuilder.getStrMultipliedByLength("a",51);
        String korean = "한";
        String nfc = Normalizer.normalize(Normalizer.normalize(korean, Normalizer.Form.NFD), Normalizer.Form.NFC);
        String nfcUnicode = String.format("U+%04X", nfc.codePointAt(0));
        return Stream.of(
                Arguments.of(minId), // 최소 문자길이 검증
                Arguments.of(maxId), // 최대 문자길이 검증
                Arguments.of("!@##@!!@"), // 특수문자 검증
                Arguments.of("abc123!@#@!#"), // 특수문자 포함 검증
                Arguments.of("한글검증"), // 다른언어 검증
                Arguments.of("english한글english") // 다른언어 포함 검증
        );
    }

    private ResultActions getMockGetResult(String userId) throws Exception {
        return this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/signup/check/{user_id}",userId)
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
