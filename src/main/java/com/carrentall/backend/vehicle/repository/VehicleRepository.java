package com.carrentall.backend.vehicle.repository;

import com.carrentall.backend.vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle,Long> {

    // 차량번호 중복 검사
    public boolean existsByVehicleNumber(String vehicleNumber);
}
