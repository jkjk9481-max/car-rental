package com.carrentall.backend.payment.exception;

public class PaymentAlreadyPaidException extends RuntimeException{
    public PaymentAlreadyPaidException(String message){
        super(message);
    }
    // 이미 결제가 존재한다 --- 409
}
