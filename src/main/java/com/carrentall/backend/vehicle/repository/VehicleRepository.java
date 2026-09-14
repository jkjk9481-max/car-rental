package com.carrentall.backend.vehicle.repository;

import com.carrentall.backend.vehicle.entity.FuelType;
import com.carrentall.backend.vehicle.entity.RentalType;
import com.carrentall.backend.vehicle.entity.Vehicle;
import com.carrentall.backend.vehicle.entity.VehicleStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    @Query // “Spring Data 메서드 이름으로 자동 쿼리 만들지 말고, 내가 직접 JPQL을 적을게”
            // status가 null이야?
            //→ 맞으면 이 조건은 그냥 통과
            //
            //status 값이 있어?
            //→ 그럼 v.status와 같은지 검사
            ("""
    SELECT v
        FROM Vehicle v
        WHERE (:status IS NULL OR v.status = :status)  
          AND (:fuelType IS NULL OR v.fuelType = :fuelType)
          AND (:rentalType IS NULL OR v.rentalType = :rentalType)
""")
    List<Vehicle> searchVehicles(@Param("status")VehicleStatus status , @Param("fuelType")FuelType fuelType , @Param("rentalType")RentalType rentalType);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // 락을 걸고 차량을 조회하는 메서드 "이 차량을 조회하면서 쓰기 목적의 비관적 락을 잡겠다"
    Optional<Vehicle> findWithLockById(Long id);
    // id로 Vehicle을 조회하되, 이 조회에서는 PESSIMISTIC_WRITE 락을 잡는다.

}
