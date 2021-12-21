package com.tys.openquant.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class
LoginDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req{
        @JsonProperty("user_id")
        @NotNull
        @Size(min=3, max=50)
        @Pattern(regexp = "^[A-Za-z0-9]{3,50}$", message = "아이디는 영문 대소문자 및 숫자로만 구성")
        private String id;

        @JsonProperty("password")
        @NotNull
        @Size(min=3, max=50)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[~!@#$%^&*()+|=])[A-Za-z0-9~!@#$%^&*()+|=]{8,50}$",
                message = "password는 영대소문자(대문자, 소문자 구별안함),숫자, 특수문자로 구성" )
        private String pwd;
    }

}
