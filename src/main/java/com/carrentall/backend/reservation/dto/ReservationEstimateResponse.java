package com.carrentall.backend.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationEstimateResponse {

    private Long vehicleId; // 요금을 계산한 차량
    private LocalDateTime startAt; // 대여시작
    private LocalDateTime endAt; // 반납
    private Long totalPrice; // 예상 총 요금
}
