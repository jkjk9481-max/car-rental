package com.carrentall.backend.vehicle.dto;

import com.carrentall.backend.vehicle.entity.FuelType;
import com.carrentall.backend.vehicle.entity.RentalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
// 외부에서 입력받을 값
// 차량 등록 요청 JSON을 담는 상자
// 사용자가 Postman으로 보낸 차량 정보를 Spring이 DTO에 담아둠
public class VehicleCreateRequest {

    @NotBlank
    private String manufacturer;

    @NotBlank
    private String modelName;

    @NotBlank
    private String vehicleNumber;

    @NotNull
    private RentalType rentalType;

    @NotNull
    private FuelType fuelType;

    @Positive
    // 무료 요금이 포함된 정책이라면 @PositiveOrZero 이걸쓴다
    private int hourlyRate;

    @Positive
    private int dailyRate;



}
