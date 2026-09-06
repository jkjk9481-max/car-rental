package com.carrentall.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    // 401 , 402 , 403
    private int status;

    // 사용자한테 메시지를 보여주는거
    private String message;
}
