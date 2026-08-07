package com.carrentall.backend.vehicle.controller;

import com.carrentall.backend.vehicle.dto.VehicleCreateRequest;
import com.carrentall.backend.vehicle.dto.VehicleResponse;
import com.carrentall.backend.vehicle.dto.VehicleStatusUpdateRequest;
import com.carrentall.backend.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public List<VehicleResponse> getAllVehicles(){
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/{vehicleId}")
    public VehicleResponse getVehicleById(@PathVariable Long vehicleId){
        return vehicleService.getVehicleById(vehicleId);
    }

    @PatchMapping("/{vehicleId}/status")
    // vehicleId -> 어떤 차량을 변경할지 , VehicleStatusUpdateRequest -> 그 차량을 어떤 상태로 변경할지
    // @PathVariable -> URL 경로에 있는 vehicleId 값을 Java 변수로 받겠다
    // RESTFul 설계에서 리소스 식별자(id 등)를 표현하고자 할 때
    // URL은 변경할 대상을 지정하고, 요청 본문은 변경할 내용을 전달한다
    public VehicleResponse updateVehicleStatus(@PathVariable Long vehicleId, @RequestBody @Valid VehicleStatusUpdateRequest request) {
        return vehicleService.updateVehicleStatus(vehicleId , request);
    }

    @DeleteMapping("/{vehicleId}")
    //  DELETE /api/vehicles/{vehicleId} 요청이 오면
    //  URL에 있는 vehicleId 값을 꺼내서
    //  VehicleService.deleteVehicle(vehicleId)를 호출한다.
    public void deleteVehicle(@PathVariable Long vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
    }

    //  PUT 요청 받기
    //  → URL에서 vehicleId 받기
    //  → 요청 본문을 VehicleUpdateRequest로 받기
    //  → @Valid로 입력값 검증
    //  → 서비스의 전체 수정 메서드 호출
    //  → 변경된 VehicleResponse 반환
}
