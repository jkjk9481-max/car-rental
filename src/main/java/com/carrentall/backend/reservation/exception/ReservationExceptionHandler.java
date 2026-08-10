package com.carrentall.backend.reservation.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReservationExceptionHandler {

    @ExceptionHandler(VehicleNotAvailableException.class)
    public ResponseEntity<String> handleVehicleNotAvailableException(VehicleNotAvailableException e) {
        HttpStatus status = HttpStatus.CONFLICT; // 차량은 있지만 현재 상태와 예약 요청이 충돌함
        String message = e.getMessage();
        return new ResponseEntity<>(message, status);
    }
}
