package com.carrentall.backend.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(PaymentAlreadyPaidException.class)
    public ResponseEntity<String> handlePaymentAlreadyPaidException(PaymentAlreadyPaidException e) {
        HttpStatus status = HttpStatus.CONFLICT;
        String message = e.getMessage();
        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(PaymentNotAllowedException.class)
    public ResponseEntity<String> handlePaymentNotAllowedException(PaymentNotAllowedException e) {
        HttpStatus status = HttpStatus.CONFLICT;
        String message = e.getMessage();
        return new ResponseEntity<>(message, status);
    }
}
