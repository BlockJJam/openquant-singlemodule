package com.tys.openquant.jwt;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * TokenProvider, JwtFilter를 SecurityConfig에 적용할 때 사용할 클래스
 * ( SecurityConfigurerAdapter를 상속받아 TokenProvider를 주입받은 뒤 JwtFilter를 통해 security 로직에 fileter를 등록
 */
@Configuration
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final TokenProvider tokenProvider;

    public JwtSecurityConfig(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    /**
     * JwtFilter에 tokenProvider를 넣고 Security filter에 JwtFilter를 추가
     * @param builder
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity builder) throws Exception {
        JwtFilter customFilter = new JwtFilter(tokenProvider);
        builder.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
