package com.carrentall.backend.payment.dto;

import com.carrentall.backend.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    private Long reservationId;
    private Long amount;
    private PaymentStatus status;
    private LocalDateTime paidAt;


}
