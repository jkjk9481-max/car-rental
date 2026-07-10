package com.carrentall.backend.auth.controller;

import com.carrentall.backend.auth.dto.LoginRequest;
import com.carrentall.backend.auth.dto.SignupRequest;
import com.carrentall.backend.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public String signup(@RequestBody @Valid SignupRequest request){
        authService.signup(request);
        return "회원가입이 완료되었습니다";
    }

    @PostMapping("/login")
    public String login(@RequestBody @Valid LoginRequest request){
        authService.login(request);
        return "로그인이 완료되었습니다";
    }

}
