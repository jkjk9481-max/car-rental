package com.carrentall.backend.vehicle.repository;

import com.carrentall.backend.vehicle.entity.FuelType;
import com.carrentall.backend.vehicle.entity.RentalType;
import com.carrentall.backend.vehicle.entity.Vehicle;
import com.carrentall.backend.vehicle.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle,Long> {

    // 차량번호 중복 검사
    public boolean existsByVehicleNumber(String vehicleNumber);

    // 번호를 가진 차량이 대상 자신인지 다른 차량인지 구분해야한다
    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    // 차량 상태별 조회
    List<Vehicle> findByStatus(VehicleStatus status);

    // 차량 상태 , 연료 , 렌탈상태를 조회
    List<Vehicle> findByStatusAndFuelTypeAndRentalType(VehicleStatus status , FuelType fuelType , RentalType rentalType);
}
