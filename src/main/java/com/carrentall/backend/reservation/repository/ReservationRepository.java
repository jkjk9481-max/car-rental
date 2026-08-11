package com.carrentall.backend.reservation.repository;

import com.carrentall.backend.reservation.entity.Reservation;
import com.carrentall.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 이 사용자가 만든 예약 목록을 보여줘 ( 해당 User를 조건으로 DB를 조회 -> 그 사용자의 예약만 반환 )
    List<Reservation> findByUser(User user);
}
