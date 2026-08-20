package com.carrentall.backend.payment.entity;

import com.carrentall.backend.payment.exception.PaymentCannotCancelException;
import com.carrentall.backend.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // 예약 하나당 결제 하나로 본다
    @JoinColumn(name = "reservation_id" , nullable = false , unique = true)
    // 외래키 컬럼 설정 , 예약이 없는 결제는 DB에 저장할수없다 , reservation_id를 두 번 사용할 수 없다는 뜻
    //  > 이 결제는 하나의 예약과 일대일로 연결된다. 예약 정보는 필요할 때 조회하며, payments 테이블의 reservation_id 외래 키로 관계를 저장한다. 모든 결제는 반드시 예약을 가져야 하며, 동일한 예약으로 두 개의
    //  > 결제를 저장할 수 없다.
    private Reservation reservation;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime paidAt;

    public Payment(Reservation reservation, Long amount) {
        this.reservation = reservation;
        this.amount = amount;
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public void cancel(){
        if(!PaymentStatus.PAID.equals(status)){
            throw new PaymentCannotCancelException("결제 완료 상태에서만 취소할 수 있습니다.");
        }
        this.status = PaymentStatus.CANCELED;
    }
}
