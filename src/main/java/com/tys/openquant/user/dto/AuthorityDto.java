package com.tys.openquant.user.dto;

import com.tys.openquant.domain.Users.Authority;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AuthorityDto {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info{
        @NotNull
        @Size(max=50)
        private String authName;

        public Info(Authority authority){
            this.authName = authority.getAuthName();
        }
    }
}
