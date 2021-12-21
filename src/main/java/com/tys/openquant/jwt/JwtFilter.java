package com.tys.openquant.jwt;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Jwt를 통한 필터링 로직이 담길 클래스
 */
@Log4j2
public class JwtFilter extends GenericFilterBean {
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    /**
     * jwt(토큰)이 헤더에 존재하는지 알아보고 토큰이 있는 경우에만
     * jwt token의 인증정보를 SecurityContext에 저장하는 역할
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(request);
        String requestURI= request.getRequestURI();
        log.info("접근한 remote ip 주소: {}",request.getRemoteAddr());

        if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){ // 토큰 유효성 검증을 통과하면
            Authentication authentication = tokenProvider.getAuthentication(jwt); // 토큰에서 authentiaction 정보를
            SecurityContextHolder.getContext().setAuthentication(authentication); // securityContext에 설정


            log.info("Security Context에 '{}' 인증 정보를 저장했습니다, url: {}", authentication.getName(), requestURI);
        }else{
            log.info("유효한 jwt token이 없습니다, uri: {}", requestURI);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * Request header에서 토큰 정보를 꺼내오기 위한 메서드
     * @param request
     * @return token info
     */
    public String resolveToken(HttpServletRequest request){
         String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
         if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
             return bearerToken.substring(7);
         }
         return null;
    }
}
