package com.tys.openquant.security;

import com.tys.openquant.jwt.JwtAccessDeniedHandler;
import com.tys.openquant.jwt.JwtAuthenticationEntryPoint;
import com.tys.openquant.jwt.JwtSecurityConfig;
import com.tys.openquant.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 설정 파일
 */
//@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 메서드 단위로 PreAuthorize annotation(@)을 사용하기 위해
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig( TokenProvider tokenProvider,
                           JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                           JwtAccessDeniedHandler jwtAccessDeniedHandler){
        this.tokenProvider= tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()   // token 방식을 사용한다는 알림
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                /*
                .and()
                .headers()
                .frameOptions()
                .sameOrigin() h2 console*/
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()    // <- httpServlet으로 오는 접근에 대한 제한을 설정하겠다
                .antMatchers("/").permitAll()
                .antMatchers("/api/signup","/api/signup/**").permitAll()
                .antMatchers("/api/login").permitAll() // <- login에 대한 접근은 모두 허용
                .antMatchers("/api/md/**", "/api/wiki/**").permitAll()
                .antMatchers("/api/historical/**").permitAll() // historical 에 대한 접근 모두 허용
                .antMatchers("/actuator/**").permitAll() // 모니터링 url 열어주기
                .antMatchers("/js/**","/css/**", "/img/**","/favicon.ico","/resources/docs/**").permitAll()
                // (below) page 요청으로 들어오는 모든 request를 허용하고, 이를 ../route/WebMvcConfig.java 에서 controller가 없는 경우 index.html로 라우팅을 해준다
                .antMatchers("/oq/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));  // spring security에 설정 했던 filter를 적용시키는 단계
    }
}
