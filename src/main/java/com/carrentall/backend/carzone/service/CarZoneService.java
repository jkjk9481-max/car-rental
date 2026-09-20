package com.carrentall.backend.carzone.service;

import com.carrentall.backend.carzone.dto.CarZoneCreateRequest;
import com.carrentall.backend.carzone.dto.CarZoneResponse;
import com.carrentall.backend.carzone.entity.CarZone;
import com.carrentall.backend.carzone.exception.CarZoneConflictException;
import com.carrentall.backend.carzone.repository.CarZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarZoneService {

    private final CarZoneRepository carZoneRepository;

    public CarZoneService(CarZoneRepository carZoneRepository) {
        this.carZoneRepository = carZoneRepository;
    }

    private CarZoneResponse toResponse(CarZone carZone) {
        return new CarZoneResponse(carZone.getId() , carZone.getName() , carZone.getAddress());
    }

    public void createCarZone(CarZoneCreateRequest request){
        boolean exists = carZoneRepository.existsByNameAndAddress(request.getName() , request.getAddress());

        if(exists){
            // if (exists)
            //→ 이미 있다
            //→ 중복 예외
            throw new CarZoneConflictException("이미 존재하는 예약장소입니다.");
        }

        CarZone carZone = new CarZone(request.getName(), request.getAddress());
        carZoneRepository.save(carZone);
    }

    public List<CarZoneResponse>  getAllCarZones(){
        List<CarZoneResponse> carZone = carZoneRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();

        // DB에서 카존 목록을 조회하고, 각 카존을 응답 DTO로 변환한 뒤 목록으로 모아서, 이 메서드를 호출한 곳에 반환한다.
        return carZone;
        // findAll()       → List<CarZone>
        //  stream()        → Stream<CarZone>
        //  map(toResponse) → Stream<CarZoneResponse>
        //  toList()        → List<CarZoneResponse>
    }


}
