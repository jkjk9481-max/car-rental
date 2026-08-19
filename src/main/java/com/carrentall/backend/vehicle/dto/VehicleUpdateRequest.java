package com.carrentall.backend.vehicle.dto;

import com.carrentall.backend.vehicle.entity.FuelType;
import com.carrentall.backend.vehicle.entity.RentalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VehicleUpdateRequest {

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
    @NotNull
    @Positive
    private Long hourlyRate;
    @NotNull
    @Positive
    private Long dailyRate;


}
