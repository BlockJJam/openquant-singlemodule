package com.tys.openquant.user.service;

import com.tys.openquant.domain.Users.Authority;
import com.tys.openquant.domain.Users.Users;
import com.tys.openquant.security.util.SecurityUtil;
import com.tys.openquant.user.dto.UserDto;
import com.tys.openquant.user.exception.UserException;
import com.tys.openquant.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * signup메서드는 현재 "ADMIN"권한을 가지는 계정으로 회원으로 등록
     * @param signup
     * @return UserDto.IsSigup
     * @author Jaemin.Joo
     */
    public UserDto.IsSigup signUp(UserDto.Signup signup){
        if(usersRepository.findOneWithAuthoritySetById(signup.getId()).orElse(null) != null){
            throw new UserException("이미 가입되어있는 유저입니다");
        }

        Authority authority = Authority.builder()
                .authName("ROLE_USER")
                .build();

        Users users = Users.builder()
                .id(signup.getId())
                .pwd(passwordEncoder.encode(signup.getPwd()))
                .email(signup.getEmail())
                .createdBy(signup.getId())
                .delNy(false)
                .authoritySet(Collections.singleton(authority))
                .build();

        Users registeredUser = usersRepository.save(users);

        return Optional.ofNullable(registeredUser)
                .map(mappedUser -> UserDto.IsSigup.builder()
                        .signuped(true)
                        .build())
                .orElse(UserDto.IsSigup.builder()
                        .signuped(false)
                        .build());
    }

    /**
     * user id를 통해 조회된 User가 있는 경우, UserDto.IsCheckedUser의 중복 여부를 채우는 함수
     * @param id
     * @return UserDto.IsCheckedUser
     * @author Jaemin.Joo
     */
    @Transactional(readOnly = true)
    public UserDto.IsCheckedUser findDuplicatedUser(String id) {
        Optional<Users> usersOptional =usersRepository.findOneById(id);
        return usersOptional.map(mappedUser -> UserDto.IsCheckedUser.builder()
                .duplicated(true)
                .build())
                .orElse(UserDto.IsCheckedUser.builder()
                        .duplicated(false)
                        .build());

    }

    /**
     * 현재 security context에 저장되어 있는 회원 정보를 가져오는 서비스
     * @return UserDto.Auth
     * @author Jaemin.Joo
     */
    @Transactional(readOnly = true)
    public UserDto.Auth getActivatedUserWithAuthoritySet(){
        Optional<Users> activatedUser = getActivatedUser();

        log.info("user info in security: {}", activatedUser.get().getId());
        return UserDto.Auth.toAuth(activatedUser.get());
    }

    /**
     * security context에 저장된 유저 정보를 통해 인증된 유저에 한하여, DB에 저장된 해당 유저정보를 조회하는 API
     * @return UserDto.Info
     * @author Jaemin.Joo
     */
    @Transactional(readOnly = true)
    public UserDto.Info getUserInfoByAuthenticatedUser() {
        Optional<Users> currentUser = getActivatedUser();

        log.info("served user info : {}", currentUser.get().getId());
        return UserDto.Info.toInfo(currentUser.get());
    }

    /**
     * Security에 있는 해당 유저의 정보를 가지고 해당 유저의 password와 요청으로 들어온 password를 비교하여 수정이 가능한 접근인지 여부를 제공하는 service
     * @param accessUpdateReq
     * @return UserDto.AccessUpdateOrNot
     * @author Jaemin.Joo
     */
    public UserDto.AccessUpdateOrNot getUserInfoByAccessUpdateReq(UserDto.AccessUpdateReq accessUpdateReq) {
        Optional<Users> activatedUser = getActivatedUser();
        log.info("The active user who has accessed modification API is {}", activatedUser.get().getId());

        if(passwordEncoder.matches(accessUpdateReq.getPwd(),activatedUser.get().getPwd())){
            return UserDto.AccessUpdateOrNot.builder().accessUpdate(true).build();
        }else{
            return UserDto.AccessUpdateOrNot.builder().accessUpdate(false).build();
        }
    }

    /**
     * security에 있는 유저 정보를 통해 Users Entity를 request로 들어온 updateInfo parameter로 수정한 뒤 수정 여부를 제공하는 service
     * @param updateReq
     * @return UserDto.UpdateOrNot
     * @author Jaemin.Joo
     */
    public UserDto.UpdateOrNot updateUserInfoByUpdateInfo(UserDto.UpdateReq updateReq) {
        Users activatedUser = getActivatedUser().get();
        log.info("The active user who has updated userinfo API is {}", activatedUser.getId());

        //같은 Password를 입력한 경우
        if(passwordEncoder.matches(updateReq.getPwd(), activatedUser.getPwd()) )
            throw new UserException("현재 패스워드와 동일한 패스워드를 사용하실 수 없습니다");

        activatedUser.setModifiedBy(activatedUser.getId());
        activatedUser.setPwd(passwordEncoder.encode(updateReq.getPwd()));
        activatedUser.setEmail(updateReq.getEmail());
        Users updatedUser = usersRepository.saveAndFlush(activatedUser);

        return Optional.ofNullable(updatedUser)
                .map(existedUser-> UserDto.UpdateOrNot.builder().updated(true).build())
                .orElse(UserDto.UpdateOrNot.builder().updated(false).build());
    }

    /**
     * 회원 ID를 입력받아 스프링 시큐리티에 등록한 회원 정보와 입력 받은 회원 ID를 비교하여 회원 탈퇴를 진행하는 service
     * @param userId
     * @return UserDto.DeleteOrNot
     * @author Jaemin.Joo
     */
    public UserDto.DeleteOrNot deleteUserInfoById(String userId) {
        Users activatedUser = getActivatedUser().get();
        log.info("The activate user who has deleted userinfo API is {}", activatedUser.getId());

        if(activatedUser.getDelNy() == true)
            throw new UserException("해당 User는 이미 삭제되었습니다");
        else if( !activatedUser.getId().equals(userId))
            throw new UserException("요청한 ID와 로그인한 ID가 일치하지 않습니다");

        activatedUser.setDeletedInfo(true, LocalDateTime.now());

        Users deletedUser = usersRepository.saveAndFlush(activatedUser);

        return Optional.ofNullable(deletedUser)
                .map(existedUser -> UserDto.DeleteOrNot.builder().deleted(true).build())
                .orElse(UserDto.DeleteOrNot.builder().deleted(false).build());
    }

    /**
     * Security에 있는 유저 정보를 가지고 오는 method
     * @return Optional<Users>
     * @author Jaemin.Joo
     */
    private Optional<Users> getActivatedUser() {
        Optional<Users> activatedUser = SecurityUtil.getCurrentUsername()
                .flatMap(usersRepository::findOneWithAuthoritySetById);

        activatedUser.orElseThrow(() -> new UsernameNotFoundException("Database에서 security에 저장된 해당 유저 정보를 찾을 수 없습니다"));
        return activatedUser;
    }
}