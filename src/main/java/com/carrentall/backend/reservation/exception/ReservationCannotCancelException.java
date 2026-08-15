package com.carrentall.backend.reservation.exception;

public class ReservationCannotCancelException extends RuntimeException {

    public ReservationCannotCancelException(String message) {
        super(message);
    }
}
