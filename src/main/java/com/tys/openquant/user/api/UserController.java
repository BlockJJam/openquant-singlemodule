package com.tys.openquant.user.api;

import com.tys.openquant.user.dto.UserDto;
import com.tys.openquant.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@CrossOrigin("*")
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    /**
     * id, pwd, email을 입력받아 회원을 등록하는 API
     * @param signup , 회원가입 정보
     * @return registered user info
     * @author Jaemin.Joo
     */
    @PostMapping("/signup")
    public ResponseEntity<UserDto.IsSigup> signUp(@Valid @RequestBody UserDto.Signup signup){
        log.info("signup api access info: {}", signup.getId());
        return ResponseEntity.ok(userService.signUp(signup));
    }

    /**
     * id를 입력받아, 회원 ID가 중복되는지 여부를 체크하는 API
     * @param id , 중복 여부를 체크할 회원 ID
     * @return UserDto.IsCheckedUser
     * @author Jaemin.Joo
     */
    @GetMapping("/signup/check/{user_id}")
    public ResponseEntity<UserDto.IsCheckedUser> signUpCheckUserId(@PathVariable("user_id")
                                                             @Size(min=3, max=50)
                                                             @Pattern(regexp = "^[A-Za-z0-9]{3,50}$", message = "아이디는 영문 대소문자 및 숫자로만 구성") String id){
        log.info("signup id duplicate check api access info: {}", id);
        return ResponseEntity.ok(userService.findDuplicatedUser(id));
    }

    /**
     * 요청으로 들어온 User의 Security Context에 저장된 권한이 'USER' 혹은 'ADMIN'일 경우에,  userService에서 유저정보를 조회하는 API
     * @return UserDto.Info
     * @author Jaemin.Joo
     */
    @GetMapping("/userinfo")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<UserDto.Info> getUserInfo(){
        return ResponseEntity.ok(userService.getUserInfoByAuthenticatedUser());
    }

    /**
     * User의 password를 이용하여, 해당 User가 회원 수정 페이지에 대한 접근이 가능한지 여부를 제공하는 API
     * @param accessUpdateReq
     * @return UserDto.AccessUpdateOrNot
     * @author Jaemin.Joo
     */
    @PostMapping("/userinfo/access-update")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<UserDto.AccessUpdateOrNot> getAccessUpdateOrNotBeforeUpdate(@Valid @RequestBody UserDto.AccessUpdateReq accessUpdateReq) {

        return ResponseEntity.ok(userService.getUserInfoByAccessUpdateReq(accessUpdateReq));
    }

    /**
     * 요청으로 들어온 유저정보가 담긴 parameter를 통해, 회원 정보를 수정하고 해당 수정 여부를 제공하는 API
     * @param updateReq
     * @return
     */
    @PostMapping("/userinfo/update")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<UserDto.UpdateOrNot> getUpdateOrNot(@Valid @RequestBody UserDto.UpdateReq updateReq){

        return ResponseEntity.ok(userService.updateUserInfoByUpdateInfo(updateReq));
    }

    /**
     * 회원 ID를 pathParameter로 받아 회원 탈퇴를 진행하고 탈퇴 여부를 제공하는 API
     * @param id
     * @return UserDto.DeleteOrNot
     * @author Jaemin.Joo
     */
    @GetMapping("/userinfo/delete/{user_id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<UserDto.DeleteOrNot> getUserDeleteOrNot( @PathVariable("user_id")
                                                                   @Size(min=3, max=50)
                                                                   @Pattern(regexp = "^[A-Za-z0-9]{3,50}$", message = "아이디는 영문 대소문자 및 숫자로만 구성") String id){

        return ResponseEntity.ok(userService.deleteUserInfoById(id));
    }


}
