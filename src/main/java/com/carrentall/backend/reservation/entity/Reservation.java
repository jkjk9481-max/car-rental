package com.carrentall.backend.reservation.entity;

import com.carrentall.backend.user.entity.User;
import com.carrentall.backend.vehicle.entity.Vehicle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false) // 여러 예약이 한 사용자를 참조한다. 이 관계는 reservations 테이블의 user_id 외래 키에 저장하며, 사용자는 필요할 때 조회하고 사용자 없는 예약은 허용하지 않는다.
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // 자바/JPA에서 엔티티 사이의 관계를 설명 ( 여러 Reservation -> 하나의 Vehicle )
    @JoinColumn(name = "vehicle_id" , nullable = false) //  DB에서 관계를 저장할 외래 키 컬럼을 설명
    private Vehicle vehicle;

    @Column(nullable = false)
    private LocalDateTime startAt; // 시작 시간

    @Column(nullable = false)
    private LocalDateTime endAt; // 반납 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Reservation(User user, Vehicle vehicle, LocalDateTime startAt, LocalDateTime endAt) {
        this.user = user;
        this.vehicle = vehicle;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = ReservationStatus.RESERVED;
        this.createdAt = LocalDateTime.now();
    }

    public void cancel(){
        this.status = ReservationStatus.CANCELED;
        // 예약을 취소한다는 의미가 분명함
    }
}
