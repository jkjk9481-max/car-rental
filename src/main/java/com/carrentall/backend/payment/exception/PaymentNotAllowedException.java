package com.carrentall.backend.payment.exception;

public class PaymentNotAllowedException extends RuntimeException{
    public PaymentNotAllowedException(String message) {
        super(message);
    }
    // 예약 상태가 RESERVED가 아니어서 결제할수 없을떄 ---- 400
}
