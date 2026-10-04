package com.carrentall.backend.reservation.repository;

import com.carrentall.backend.payment.entity.Payment;
import com.carrentall.backend.reservation.entity.Reservation;
import com.carrentall.backend.reservation.entity.ReservationStatus;
import com.carrentall.backend.user.entity.User;
import com.carrentall.backend.vehicle.entity.Vehicle;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 이 사용자가 만든 예약 목록을 보여줘 ( 해당 User를 조건으로 DB를 조회 -> 그 사용자의 예약만 반환 )
    List<Reservation> findByUser(User user);

    //  특정 차량에 대해
    //  상태가 RESERVED인 예약 중에서
    //  기존 startAt < 새 endAt
    //  그리고 기존 endAt > 새 startAt인 예약이 존재하는가?
    boolean existsByVehicleAndStatusAndStartAtLessThanAndEndAtGreaterThan(
            Vehicle vehicle,
            ReservationStatus status,
            LocalDateTime endAt,
            LocalDateTime startAt
    );

    // 같은 예약의 결제와 예약 취소를 순서대로 처리하기 위한 잠금 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE) // “이 예약을 조회하면서 DB에 잠금을 걸어, 다른 요청이 동시에 변경하지 못하도록 하겠다
    // 서버의 검사·저장 작업이 끝날 때까지 대기
    Optional<Reservation> findWithLockById(Long id);


}
