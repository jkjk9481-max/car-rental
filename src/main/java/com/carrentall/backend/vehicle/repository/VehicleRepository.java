package com.carrentall.backend.vehicle.repository;

import com.carrentall.backend.vehicle.entity.FuelType;
import com.carrentall.backend.vehicle.entity.RentalType;
import com.carrentall.backend.vehicle.entity.Vehicle;
import com.carrentall.backend.vehicle.entity.VehicleStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE) // 락을 걸고 차량을 조회하는 메서드 "이 차량을 조회하면서 쓰기 목적의 비관적 락을 잡겠다"
    Optional<Vehicle> findWithLockById(Long id);
    // id로 Vehicle을 조회하되, 이 조회에서는 PESSIMISTIC_WRITE 락을 잡는다.

}
