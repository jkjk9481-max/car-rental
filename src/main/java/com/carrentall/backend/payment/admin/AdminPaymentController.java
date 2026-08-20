package com.carrentall.backend.payment.admin;

import com.carrentall.backend.payment.dto.PaymentResponse;
import com.carrentall.backend.payment.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
public class AdminPaymentController {

    private final PaymentService paymentService;


    public AdminPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<PaymentResponse> getAllPaymentsForAdmin(){
        return paymentService.getAllPaymentsForAdmin();
        // 관리자는 모든 사용자의 결제 내역을 조회한다
    }
}
