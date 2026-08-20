package com.carrentall.backend.reservation.admin;

import com.carrentall.backend.reservation.dto.ReservationResponse;
import com.carrentall.backend.reservation.service.ReservationService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<ReservationResponse> getAllReservationsForAdmin(){
        return reservationService.getAllReservationsForAdmin();
        // 관리자가 전체 예약 목록을 조회한다
    }
}
