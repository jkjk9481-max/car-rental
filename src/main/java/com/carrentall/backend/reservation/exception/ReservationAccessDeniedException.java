package com.carrentall.backend.reservation.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;


public class ReservationAccessDeniedException extends RuntimeException {
    public ReservationAccessDeniedException(String message) {
        super(message);
    }


}
