package com.carrentall.backend.payment.controller;

import com.carrentall.backend.payment.dto.PaymentResponse;
import com.carrentall.backend.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/reservations/{reservationId}")
    public PaymentResponse pay(@PathVariable Long reservationId , Authentication authentication) {
        String email =  authentication.getName();
        return paymentService.pay(reservationId, email);
    }
}
