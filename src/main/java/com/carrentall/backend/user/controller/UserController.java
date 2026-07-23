package com.carrentall.backend.user.controller;


import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {


    //  → JwtAuthenticationFilter가 JWT 검사
    //  → JWT에서 email 추출
    //  → Authentication의 principal에 email 저장
    //  → UserController가 authentication.getName()으로 email 확인
    @GetMapping("/me")
    public String getMyInfo(Authentication authentication) {
        return authentication.getName();
    }


}
