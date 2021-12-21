package com.tys.openquant.security.util;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

@Log4j2
@NoArgsConstructor
public class SecurityUtil {

    /**
     * Security Context의 Authentication 객체에서 username을 꺼내오는 것
     * Authentication 객체가 저장되는 시점 = doFilter(JwtFilter)메서드에서 request의 header에 있던 jwt로 authentication을 가져와 매핑된 authentication 객체를 불러옴
     * @return username
     */
    public static Optional<String> getCurrentUsername(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();   // security context에서 authentication 정보를 가져와서

        if(authentication == null){             // authentication 정보가 없으면 바로 return
            log.info("Security Context에 인증 정보가 없습니다");
            return Optional.empty();
        }

        String username = null;                                                                         // authentiaction에 담긴 정보의 형태(UserDetail이냐, String)에 따라 다르게 username을 채운다
        if(authentication.getPrincipal() instanceof UserDetails){
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        }else if(authentication.getPrincipal() instanceof String){
            username = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(username);
    }
}
