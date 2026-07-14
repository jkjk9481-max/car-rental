package com.carrentall.backend.auth.exception;

public class InvalidCredentialsException extends RuntimeException{

    public InvalidCredentialsException(String message){
        super(message);
        // 이 오류 메시지를 예외의 정식 메시지로 저장
    }




}
