package com.carrentall.backend.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ReservationCreateRequest {

    // vehicleId, startAt, endAt은 사용자가 결정하고, userId는 서버가 JWT를 통해 결정합니다.

    @NotNull
    private Long vehicleId; // 무엇을 예약하는가?
    @NotNull
    private LocalDateTime startAt; // 언제 시작하는가?
    @NotNull
    private LocalDateTime endAt; // 언제 끝나는가?
}
