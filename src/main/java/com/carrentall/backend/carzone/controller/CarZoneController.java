package com.carrentall.backend.carzone.controller;

import com.carrentall.backend.carzone.dto.CarZoneCreateRequest;
import com.carrentall.backend.carzone.service.CarZoneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/carzones")
public class CarZoneController {


    private final CarZoneService carZoneService;


    public CarZoneController(CarZoneService carZoneService) {
        this.carZoneService = carZoneService;
    }

    @PostMapping
    public void createCarZone(@Valid @RequestBody CarZoneCreateRequest carZoneCreateRequest) {
        carZoneService.createCarZone(carZoneCreateRequest);
    }


}
