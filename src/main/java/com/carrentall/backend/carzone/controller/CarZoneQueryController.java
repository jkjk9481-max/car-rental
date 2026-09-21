package com.carrentall.backend.carzone.controller;

import com.carrentall.backend.carzone.dto.CarZoneResponse;
import com.carrentall.backend.carzone.service.CarZoneService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/carzones")
public class CarZoneQueryController {

    private final CarZoneService carZoneService;

    public CarZoneQueryController(CarZoneService carZoneService) {
        this.carZoneService = carZoneService;
    }

    //  로그인한 사용자가 장소 목록을 조회
    @GetMapping
    public List<CarZoneResponse> getAllCarZones() {
        return carZoneService.getAllCarZones();
    }
}
