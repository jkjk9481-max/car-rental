package com.carrentall.backend.reservation.exception;

public class VehicleNotAvailableException extends RuntimeException{
    // 차량은 존재하지만 현재 상태가 AVAILABLE이 아니어서 예약할 수 없는 경우를 표현

    public VehicleNotAvailableException(String message) {
        super(message);
    }

}
