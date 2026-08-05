package com.carrentall.backend.vehicle.exception;

public class VehicleNotFoundException extends RuntimeException {
     
    // 문자열 메시지를 받는 생성자 작성
    public VehicleNotFoundException(String message) {
        super(message);
    }


}
