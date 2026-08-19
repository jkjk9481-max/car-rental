package com.carrentall.backend.payment.service;

import com.carrentall.backend.payment.dto.PaymentResponse;
import com.carrentall.backend.payment.entity.Payment;
import com.carrentall.backend.payment.entity.PaymentStatus;
import com.carrentall.backend.payment.exception.PaymentAlreadyPaidException;
import com.carrentall.backend.payment.exception.PaymentNotAllowedException;
import com.carrentall.backend.payment.repository.PaymentRepository;
import com.carrentall.backend.reservation.entity.Reservation;
import com.carrentall.backend.reservation.entity.ReservationStatus;
import com.carrentall.backend.reservation.exception.ReservationAccessDeniedException;
import com.carrentall.backend.reservation.exception.ReservationNotFoundException;
import com.carrentall.backend.reservation.repository.ReservationRepository;
import com.carrentall.backend.user.entity.User;
import com.carrentall.backend.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentRepository paymentRepository , UserRepository userRepository,ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }

    private PaymentResponse toResponse(Payment payment){
        return  new PaymentResponse(payment.getId(), payment.getReservation().getId(), payment.getAmount() , payment.getStatus() , payment.getPaidAt());
    }

    @Transactional
    public PaymentResponse pay(Long reservationId , String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("예약을 찾을 수 없습니다."));

        if (!reservation.getUser().getId().equals(user.getId())){
            throw new ReservationAccessDeniedException("해당 예약에 접근할 수 없습니다");
        }
        if(!ReservationStatus.RESERVED.equals(reservation.getStatus())){
            throw new PaymentNotAllowedException("예약할 수 있는 상태가 아닙니다");
        }
        boolean alreadyPaid = paymentRepository.existsByReservationAndStatus(reservation, PaymentStatus.PAID);
        //  현재 조회한 예약과 연결되어 있으면서
        //  상태가 PAID인 Payment가 이미 존재하는가?
        if(alreadyPaid){
            throw new PaymentAlreadyPaidException("이미 결제된 예약입니다.");
        }
        Long amount = reservation.getTotalPrice();
        Payment payment = new Payment(reservation , amount);
        paymentRepository.save(payment);

        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyPayments(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자가 존재하지 않습니다."));

       return paymentRepository.findByReservation_User(user)
                .stream()
                .map(this::toResponse)
                .toList();
       // 이메일로 현재 사용자를 찾고, 그 사용자의 예약에 연결된 결제들을 조회한 뒤, 각 결제를 PaymentResponse로 변환한다
    }

}
