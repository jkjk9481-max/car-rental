package com.carrentall.backend.carzone.service;

import com.carrentall.backend.carzone.dto.CarZoneCreateRequest;
import com.carrentall.backend.carzone.entity.CarZone;
import com.carrentall.backend.carzone.repository.CarZoneRepository;
import org.springframework.stereotype.Service;

@Service
public class CarZoneService {

    private final CarZoneRepository carZoneRepository;

    public CarZoneService(CarZoneRepository carZoneRepository) {
        this.carZoneRepository = carZoneRepository;
    }

    public void createCarZone(CarZoneCreateRequest request){
        boolean exists = carZoneRepository.existsByNameAndAddress(request.getName() , request.getAddress());

        if(exists){
            // if (exists)
            //→ 이미 있다
            //→ 중복 예외
            throw new RuntimeException("이미 존재하는 예약장소입니다.");
        }

        CarZone carZone = new CarZone(request.getName(), request.getAddress());
        carZoneRepository.save(carZone);
    }
}
