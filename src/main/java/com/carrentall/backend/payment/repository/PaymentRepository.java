package com.carrentall.backend.payment.repository;

import com.carrentall.backend.payment.entity.Payment;
import com.carrentall.backend.payment.entity.PaymentStatus;
import com.carrentall.backend.reservation.entity.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 두번쨰 결제를 막아야하기 위한 메서드
    // 전달받은 예약과 연결되어있으면서 , 전달받은 결제 상태를 가진 Payment가 존재하는가?
    boolean  existsByReservationAndStatus(Reservation reservation, PaymentStatus status);


}
