package com.carrentall.backend.vehicle.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class VehicleExceptionHandler {

    @ExceptionHandler(DuplicateVehicleNumberException.class)
    public ResponseEntity<String> handleDuplicateVehicleNumberException(DuplicateVehicleNumberException exception) {
        HttpStatus status = HttpStatus.CONFLICT;
        String message = exception.getMessage();
        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<String> handleVehicleNotFoundException(VehicleNotFoundException exception) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = exception.getMessage();
        return new ResponseEntity<>(message, status);
    }
}
