package com.carrentall.backend.payment.controller;

import com.carrentall.backend.payment.dto.PaymentResponse;
import com.carrentall.backend.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/me")
    public List<PaymentResponse> getMyPayments(Authentication authentication) {
        String email =  authentication.getName();
        return paymentService.getMyPayments(email);
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(@PathVariable Long paymentId, Authentication authentication) {
        String email =  authentication.getName();
        return paymentService.getMyPayment(paymentId, email);
    }

    @PatchMapping("/{paymentId}/cancel")
    public PaymentResponse cancelPayment(@PathVariable Long paymentId, Authentication authentication) {
        String email =  authentication.getName();
        return paymentService.cancelPayment(paymentId, email);
    }
}
