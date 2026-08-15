package com.carrentall.backend.reservation.exception;

public class ReservationTimeConflictException extends RuntimeException{
    public ReservationTimeConflictException(String message) {
        super(message);
    }
}
