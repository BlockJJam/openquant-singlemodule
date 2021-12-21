package com.tys.openquant.user.service;

import com.tys.openquant.domain.Users.Users;
import com.tys.openquant.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UsersRepository usersRepository;

    /**
     * UserDetails는 security 권한 정보로, DB에서 유저정보와 함께 제공하기 위함
     * @param username
     * @return UserDetails.User obj
     * @author Jaemin.Joo
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return usersRepository.findOneWithAuthoritySetByIdAndDelNy(username,false)
                .map(users-> createUser(username, users))
                .orElseThrow(()-> new UsernameNotFoundException(username+"-> Database에서 찾을 수 없습니다"));
    }

    /**
     * DB에 들어있는 user와 권한 정보를 통해 security의 User객체로 만들어 제공
     * @param username
     * @param users
     * @return security User obj
     * @author Jaemin.Joo
     */
    private org.springframework.security.core.userdetails.User createUser(String username, Users users){

        List<GrantedAuthority> grantedAuthorityList = users.getAuthoritySet().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthName()))
                .collect(toList());

        return new org.springframework.security.core.userdetails.User(users.getId(), users.getPwd(), grantedAuthorityList);
    }
}
