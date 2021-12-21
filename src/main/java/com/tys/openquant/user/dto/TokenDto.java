package com.tys.openquant.user.dto;

import lombok.*;

public class TokenDto {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info{
        private String token;
    }
}
