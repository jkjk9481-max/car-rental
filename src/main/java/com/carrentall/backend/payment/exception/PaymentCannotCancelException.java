package com.carrentall.backend.payment.exception;

public class PaymentCannotCancelException extends RuntimeException{
    public PaymentCannotCancelException(String message){
        super(message);
    }
}
