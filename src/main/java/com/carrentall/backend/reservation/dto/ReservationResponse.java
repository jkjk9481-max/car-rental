package com.carrentall.backend.reservation.dto;

import com.carrentall.backend.reservation.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationResponse {
    
    //  Response는 입력 검증용이 아니기 때문에 @NotNull이 필요 없다. , 이 DTO는 서버가 만들어 반환하는 응답이기 떄문이다
    
    private Long reservationId;
    private Long userId;
    private Long vehicleId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    
    
    
}
