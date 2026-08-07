package com.carrentall.backend.vehicle.repository;

import com.carrentall.backend.vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle,Long> {

    // 차량번호 중복 검사
    public boolean existsByVehicleNumber(String vehicleNumber);

    // 번호를 가진 차량이 대상 자신인지 다른 차량인지 구분해야한다
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);
}
