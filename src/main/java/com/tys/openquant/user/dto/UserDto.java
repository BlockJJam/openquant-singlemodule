package com.tys.openquant.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tys.openquant.domain.Users.Authority;
import com.tys.openquant.domain.Users.Users;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

import static java.util.stream.Collectors.*;

public class UserDto {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info{

        @JsonProperty("user_id")
        @NotNull
        @Size(min=3, max = 50)
        private String id;

        @NotNull
        @Size(min=7, max = 320)
        private String email;

        @JsonProperty("authority_set")
        private Set<AuthorityDto.Info> authoritySet;

        public static Info toInfo(Users users){
            return Info.builder()
                    .id(users.getId())
                    .email(users.getEmail())
                    .authoritySet(convertAuthoritySet(users.getAuthoritySet()))
                    .build();
        }

        private static Set<AuthorityDto.Info> convertAuthoritySet(Set<Authority> authoritySet){
            return authoritySet.stream()
                    .map(AuthorityDto.Info::new)
                    .collect(toSet());
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Auth{

        @JsonProperty("user_id")
        @NotNull
        @Size(min=3, max = 50)
        private String id;

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @Size(min=3, max= 100)
        private String pwd;

        @JsonProperty("authority_set")
        private Set<AuthorityDto.Info> authoritySet;

        public static Auth toAuth(Users users){
            return Auth.builder()
                    .id(users.getId())
                    .authoritySet(convertAuthoritySet(users.getAuthoritySet()))
                    .pwd(users.getPwd())
                    .build();
        }

        private static Set<AuthorityDto.Info> convertAuthoritySet(Set<Authority> authoritySet){
            return authoritySet.stream()
                    .map(AuthorityDto.Info::new)
                    .collect(toSet());
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Signup {

        @JsonProperty("user_id")
        @NotNull
        @Size(min=3, max = 50)
        @Pattern(regexp = "^[A-Za-z0-9]{3,50}$", message = "아이디는 영문 대소문자 및 숫자로만 구성")
        private String id;

        @JsonProperty("password")
        @NotNull
        @Size(min=8, max = 50)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[~!@#$%^&*()+|=])[A-Za-z0-9~!@#$%^&*()+|=]{8,50}$",
                message = "password는 영대소문자(대문자, 소문자 구별X),숫자, 특수문자로 구성" )
        private String pwd;

        @NotNull
        @Size(min=7, max=320)
        @Email(regexp = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")
        private String email;
    }

    @Getter
    @Builder
    public static class IsSigup {
        @JsonProperty("is_signuped")
        private boolean signuped;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class IsCheckedUser {
        @JsonProperty("is_duplicated")
        private boolean duplicated;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccessUpdateReq {
        @JsonProperty("password")
        private String pwd;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AccessUpdateOrNot {

        @JsonProperty("is_access_update")
        private boolean accessUpdate;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UpdateOrNot {
        @JsonProperty("is_updated")
        private boolean updated;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateReq {

        @JsonProperty("password")
        @NotNull
        @Size(min=8, max = 50)
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[~!@#$%^&*()+|=])[A-Za-z0-9~!@#$%^&*()+|=]{8,50}$",
                message = "password는 영대소문자(대문자, 소문자 구별X),숫자, 특수문자로 구성" )
        private String pwd;

        @NotNull
        @Size(min=7, max=320)
        @Email(regexp = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")
        private String email;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DeleteOrNot {
        @JsonProperty("is_deleted")
        private boolean deleted;
    }
}
