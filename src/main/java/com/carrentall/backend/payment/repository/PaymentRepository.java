package com.carrentall.backend.payment.repository;

import com.carrentall.backend.payment.entity.Payment;
import com.carrentall.backend.payment.entity.PaymentStatus;
import com.carrentall.backend.reservation.entity.Reservation;

import com.carrentall.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 두번쨰 결제를 막아야하기 위한 메서드
    // 전달받은 예약과 연결되어있으면서 , 전달받은 결제 상태를 가진 Payment가 존재하는가?
    boolean  existsByReservationAndStatus(Reservation reservation, PaymentStatus status);

    //  Payment 안의 reservation 안의 user가
    //  전달받은 user와 같은 Payment 목록을 찾아줘.
    List<Payment> findByReservation_User(User user);
}
