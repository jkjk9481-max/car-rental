package com.carrentall.backend.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
// Controller까지 올라온 예외를 공통으로 감시
// -> 해당 예외를 잡음
// -> HTTP 응답으로 바꿀준비
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class) // 어떤 예외를 잡을지 지정
    public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException exception) {
        // ResponseEntity<String> -> 상태 코드와 문자열 본문을 함께 담는 응답
        // 실제로 발생한 예외를 전달받음
        HttpStatus status = HttpStatus.UNAUTHORIZED; // HTTP 401 상태로 응답
        String message = exception.getMessage();
        return new ResponseEntity<>(message, status);
        // 응답 메시지 , HTTP 상태
    }
}
