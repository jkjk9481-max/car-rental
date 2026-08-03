package com.carrentall.backend.vehicle.controller;

import com.carrentall.backend.vehicle.dto.VehicleCreateRequest;
import com.carrentall.backend.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/vehicles")
@RestController
public class VehicleController {
    // 클라이언트의 HTTP 요청을 애플리케이션 내부 로직과 연결하는 입구다

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public void createVehicle(@RequestBody @Valid VehicleCreateRequest request) {
        vehicleService.createVehicle(request);
    }
}
