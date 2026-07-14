package com.carrentall.backend.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
// 우리 코드가 토큰을 넣어 만들어야함
// Request는 클라이언트가 보낸 JSON을 Spring이 객체로 만듭니다
public class LoginResponse {

    private String accessToken;


    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
