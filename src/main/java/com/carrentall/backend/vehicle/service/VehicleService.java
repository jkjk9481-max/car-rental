package com.carrentall.backend.vehicle.service;

import com.carrentall.backend.vehicle.dto.VehicleCreateRequest;
import com.carrentall.backend.vehicle.entity.Vehicle;
import com.carrentall.backend.vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    // 차량번호 중복 확인
    public void existsByVehicleNumber(VehicleCreateRequest request) {
        // 1.차량번호 중복 확인
        boolean exists =  vehicleRepository.existsByVehicleNumber(request.getVehicleNumber());

        // 2.중복이면 예외발생
        if(exists){
            throw new RuntimeException("중복된 차량 번호입니다.");
        }
        // 3.Vehicle 객체 생성
        // VehicleRepository는 Vehicle만 저장할 수 있으므로, Request에서 값을 꺼내 유효한 Vehicle 객체를 생성한 다음 그 Vehicle을 저장합니다.
        // Request는 사용자의 차량 등록 신청서이고, Vehicle은 그 신청서의 값을 바탕으로 서버 규칙까지 적용해 만든 실제 DB 저장 대상입니다.
        Vehicle vehicle = new Vehicle(request.getManufacturer() , request.getHourlyRate() , request.getDailyRate() , request.getVehicleNumber() , request.getModelName() , request.getRentalType() , request.getFuelType());

        vehicleRepository.save(vehicle);
        
    }
}
