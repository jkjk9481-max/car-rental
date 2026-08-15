package com.carrentall.backend.reservation.exception;

public class InvalidReservationTimeException extends RuntimeException{
    public InvalidReservationTimeException(String message) {
        super(message);
    }
}
