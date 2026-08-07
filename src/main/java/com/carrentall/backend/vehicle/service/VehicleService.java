package com.carrentall.backend.vehicle.service;

import com.carrentall.backend.vehicle.dto.VehicleCreateRequest;
import com.carrentall.backend.vehicle.dto.VehicleResponse;
import com.carrentall.backend.vehicle.dto.VehicleStatusUpdateRequest;
import com.carrentall.backend.vehicle.dto.VehicleUpdateRequest;
import com.carrentall.backend.vehicle.entity.Vehicle;
import com.carrentall.backend.vehicle.exception.DuplicateVehicleNumberException;
import com.carrentall.backend.vehicle.exception.VehicleNotFoundException;
import com.carrentall.backend.vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    // 차량번호 중복 확인
    public void createVehicle(VehicleCreateRequest request) {
        // 1.차량번호 중복 확인
        boolean exists =  vehicleRepository.existsByVehicleNumber(request.getVehicleNumber());

        // 2.중복이면 예외발생
        if(exists){
            throw new DuplicateVehicleNumberException("중복된 차량 번호 입니다.");
        }
        // 3.Vehicle 객체 생성
        // VehicleRepository는 Vehicle만 저장할 수 있으므로, Request에서 값을 꺼내 유효한 Vehicle 객체를 생성한 다음 그 Vehicle을 저장합니다.
        // Request는 사용자의 차량 등록 신청서이고, Vehicle은 그 신청서의 값을 바탕으로 서버 규칙까지 적용해 만든 실제 DB 저장 대상입니다.
        Vehicle vehicle = new Vehicle(request.getManufacturer() , request.getHourlyRate() , request.getDailyRate() , request.getVehicleNumber() , request.getModelName() , request.getRentalType() , request.getFuelType());

        vehicleRepository.save(vehicle);
    }

    // 목록 조회
    public List<VehicleResponse> getAllVehicles(){
        // 반환하는 타입이랑 알맞게 타입을 변환해줘야한다 ( 내가 설정한 타입이랑 같게 )
        List<VehicleResponse> vehicles = vehicleRepository.findAll()
                .stream()
                // 매개변수 -> 반환할 표현식 , 왼쪽객체를 오른쪽 방식으로 반환한다
                .map(vehicle -> new VehicleResponse(vehicle.getId() , vehicle.getManufacturer() , vehicle.getModelName() , vehicle.getVehicleNumber() , vehicle.getRentalType() , vehicle.getFuelType() , vehicle.getStatus() , vehicle.getHourlyRate() , vehicle.getDailyRate()))
                .toList();

        return vehicles;
        // DB 저장용 Vehicle 목록
        // -> 사용자에게 보여줄 VehicleResponse 목록으로 바꿔서 반환
    }

    // 차량 단건 조회
    public VehicleResponse getVehicleById(Long vehicleId){
        // 1.findById()로 차량 조회
        // 조회 결과를 그냥 호출하고 버리면 DTO로 변환할수 없다
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("해당 차량이 존재하지 않습니다."));

        // 있으면 VehicleResponse로 변환해 반환
        VehicleResponse vehicleResponse = new VehicleResponse(vehicle.getId() , vehicle.getManufacturer() , vehicle.getModelName() , vehicle.getVehicleNumber() , vehicle.getRentalType() , vehicle.getFuelType() , vehicle.getStatus() , vehicle.getHourlyRate() , vehicle.getDailyRate());
        return vehicleResponse;
    }

    @Transactional // 정상 종료되면 변경 내용이 DB에 반영 , 도중에 예외가 발생하면 일반적으로 변경 사항이 롤백된다
    public VehicleResponse updateVehicleStatus(Long vehicleId  , VehicleStatusUpdateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("해당 차량이 존재하지 않습니다."));

        vehicle.changeStatus(request.getStatus());

        VehicleResponse response = new VehicleResponse(vehicle.getId() , vehicle.getManufacturer() , vehicle.getModelName() , vehicle.getVehicleNumber() , vehicle.getRentalType() , vehicle.getFuelType() , vehicle.getStatus() , vehicle.getHourlyRate() , vehicle.getDailyRate());
        return response;
        // DB에서 해당 ID의 차량을 찾고, 없으면 오류를 발생시킨다. 있으면 차량 상태를 변경하고, 변경된 차량을 VehicleResponse로 변환해 반환한다.
    }

    @Transactional
    // 없는 차량이면 → VehicleNotFoundException
    //  있는 차량이면 → 삭제 진행
    public void deleteVehicle(Long vehicleId){
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("해당 차량이 존재하지 않습니다."));

        vehicleRepository.delete(vehicle);
    }

    @Transactional

    //  1. findById(vehicleId)로 수정 대상 차량을 조회합니다.
    //  2. 없으면 VehicleNotFoundException을 던집니다.
    //  3. 요청한 차량번호를 사용하는 차량이 있는지 조회합니다.
    //  4. 발견된 차량이 수정 대상과 다른 차량이면 DuplicateVehicleNumberException을 던집니다.
    //  5. request의 값들을 vehicle.updateInfo()에 전달합니다.
    //  6. @Transactional의 Dirty Checking으로 변경 내용이 DB에 반영됩니다.
    //  7. 변경된 차량을 VehicleResponse로 반환합니다.
    public VehicleResponse updateVehicle(Long vehicleId  , VehicleUpdateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException("해당 차량이 존재하지 않습니다."));

        vehicleRepository.findByVehicleNumber(request.getVehicleNumber())
                .ifPresent(foundVehicle -> {
                    // 조회된 차량 Id와 현재 수정하려는 차량 ID가 같은지 확인한다
                    // 해당 차량번호로 발견한 차량의 ID가 현재 수정하려는 차량 ID와 같지 않다면    
                    // foundVehicle은 차량번호로 DB에서 발견된 차량이고 , vehicleId는 지금 수정하려는 차량의 ID이다
                    // 다른 차량이 이미 이 번호를 사용 중이라는뜻
                    if(!foundVehicle.getId().equals(vehicleId)){
                       throw new DuplicateVehicleNumberException("중복된 차량 번호입니다.");
                    }
                });

        vehicle.updateInfo(request.getManufacturer(), request.getModelName(), request.getVehicleNumber(),
                request.getRentalType(), request.getFuelType(), request.getHourlyRate(), request.getDailyRate());

        return new VehicleResponse(vehicle.getId(), vehicle.getManufacturer(), vehicle.getModelName(),
                vehicle.getVehicleNumber(), vehicle.getRentalType(), vehicle.getFuelType(),
                vehicle.getStatus(), vehicle.getHourlyRate(), vehicle.getDailyRate());
    }
}
