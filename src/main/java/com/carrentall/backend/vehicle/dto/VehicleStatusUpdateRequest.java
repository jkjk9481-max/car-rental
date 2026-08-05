package com.carrentall.backend.vehicle.dto;

import com.carrentall.backend.vehicle.entity.VehicleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VehicleStatusUpdateRequest {

    @NotNull
    private VehicleStatus status;
}
