package com.tys.openquant.user.service;

import com.tys.openquant.jwt.JwtFilter;
import com.tys.openquant.jwt.TokenProvider;
import com.tys.openquant.user.dto.LoginDto;
import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    //private final RedisTemplate redisTemplate;

    private final long DIFF_SECONDS = 5;
    private final long SECONDS_ONE_DAY = 86400;
    private final String BEARER_STR = "Bearer ";

    public HttpHeaders getAuthHeaderWithNewToken(LoginDto.Req loginReq, HttpServletRequest request) {
        String jwt = getJwtByUser(loginReq, request);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, BEARER_STR+jwt);

        return httpHeaders;
    }

    /*public HttpHeaders getAuthHeaderWithUpdatedToken(HttpServletRequest request){
        // 기존의 jwt의 ip주소를 비교하고, 다른 ip주소로 들어왔으면 삭제한다
        String exJwt = request.getHeader("Authorization").substring(BEARER_STR.length());
        if(exJwt != null)
            compareIpAndDeleteJwtInRedis(exJwt,request);

        String jwt = getJwtByUsersInSecurityObject(request);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, BEARER_STR+jwt);

        return httpHeaders;
    }*/

   /* private void compareIpAndDeleteJwtInRedis(String exJwt, HttpServletRequest request) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key= exJwt;
        String value= request.getRemoteAddr();

        Optional.ofNullable(valueOperations.get(key))
                .ifPresent(addrOfKey -> {
                    if (!addrOfKey.equals(value))
                        throw new BadCredentialsException("해당 유저는 올바르지 않은 경로로 접근하고 있습니다");

                    redisTemplate.delete(key);
                });
    }*/

    private String getJwtByUser(LoginDto.Req loginReq, HttpServletRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginReq.getId(), loginReq.getPwd());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        String jwt = tokenProvider.createToken(authentication);
        //setAccessTokenMatchIpInRedis(jwt, request.getRemoteAddr());

        SecurityContextHolder.getContext().setAuthentication(authentication);


        return jwt;
    }

    private String getJwtByUsersInSecurityObject(HttpServletRequest request) {
        Authentication currAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String jwt = tokenProvider.createToken(currAuthentication);
        //setAccessTokenMatchIpInRedis(jwt, request.getRemoteAddr());

        SecurityContextHolder.getContext().setAuthentication(currAuthentication);

        return jwt;
    }

    /*private void setAccessTokenMatchIpInRedis(String jwt, String remoteAddr){
        // @TODO remoteAddr로 사용할 수 없는 주소 리스트를 검사할 필요성이 있음
        // @TODO redistTemplate.opsForSet()으로 만든 SetOperations로 변경할 필요성이 있음 -> 해당 유저 확인 인증이 들어가면
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String key= jwt;
        String value= remoteAddr;

        Optional.ofNullable(valueOperations.get(key))
                .ifPresent(addrOfKey -> {
                    if (!addrOfKey.equals(value))
                        throw new BadCredentialsException("해당 유저는 올바르지 않은 경로로 접근하고 있습니다");

                    redisTemplate.delete(key);
                });

        valueOperations.set(key, value);
        redisTemplate.expire(key, SECONDS_ONE_DAY - DIFF_SECONDS, TimeUnit.SECONDS); // 실사용
        //redisTemplate.expire(key, 60, TimeUnit.SECONDS); // 테스트
    }*/
}
