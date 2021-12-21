package com.tys.openquant.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * 토큰의 생성, 토큰의 유효성 검증을 담당
 */
@Slf4j
@Component
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;
    private final long tokenValidityInMilliseconds;

    private Key key;

    public TokenProvider(@Value("${jwt.secret}") String secret,
                         @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds){
        this.secret= secret;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * jwts의 builder를 통해 불러온 권한들과 jwt 만료 기간으로 token을 만들어본다
     * @param authentication
     * @return token string
     */
    public String createToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now+ this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)    // jwt body에 존재하는 claim(속성 정보)
                .signWith(key, SignatureAlgorithm.HS512) // 시그니쳐
                .setExpiration(validity)    // 만료기간
                .compact();
    }

    /**
     * Token이 담겨있는 정보를 이용해서 Authentication 객체를 반환
     * @param token
     * @return Authentiaction obj
     */
    public Authentication getAuthentication(String token){
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(toList());

        User principal = new User(claims.getSubject(), "", authorities); // domain의 User (X) -> security의 User(O)

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * token을 파싱해보고 exception들이 발생하면 캐치, 문제가 발생하는지 flag를 반환
     * @param token
     * @return token parsing result flag
     */
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch(io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            log.info("잘못된 JWT 서명입니다");
        }catch(ExpiredJwtException e){
            log.info("만료된 JWT 서명입니다");
        }catch(UnsupportedJwtException e){
            log.info("지원되지 않는 JWT 토큰입니다");
        }catch(IllegalArgumentException e){
            log.info("JWT 토큰이 잘못 구성되었습니다");
        }
        return false;
    }
}
