package com.carrentall.backend.reservation.repository;

import com.carrentall.backend.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
