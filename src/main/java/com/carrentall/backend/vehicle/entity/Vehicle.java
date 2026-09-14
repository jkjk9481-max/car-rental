package com.carrentall.backend.vehicle.entity;

import com.carrentall.backend.carzone.entity.CarZone;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 제조사
    @Column(nullable = false)
    private String manufacturer;

    // 시간당 요금
    @Column(nullable = false)
    private Long hourlyRate;

    // 일일 요금
    @Column(nullable = false)
    private Long dailyRate;

    // 차량 번호판 번호
    @Column(nullable = false, unique = true) // unique 제약이 없다면 차량 중복이 등록될 수 있습니다
    // nullable = false -> 차량번호없이 저장하지 못하게함
    private String vehicleNumber;

    // 차량 모델명
    @Column(nullable = false)
    private String modelName;

    // 카세어링 또는 일반 렌터카 구분
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    // -> DB값을 사람이 바로 이해 할 수 있음 , enum 순서가 바뀌어도 기존 데이터의 으미가 유지됨 , 잘못된 상태 데이터를 발견하기 쉬움
    private RentalType rentalType;

    // 연료종류
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType;

    // 차량의 현재 운영 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_zone_id" , nullable = false)
    // → Vehicle 테이블에서 어떤 FK 컬럼으로 CarZone과 연결할지 지정
    // Vehicle 객체 하나는
    //반드시 CarZone 하나를 알아야 한다
    private CarZone carZone;

    // 차량 정보가 등록된 시각
    @Column(nullable = false , updatable = false)
    private LocalDateTime createdAt;

    public Vehicle(String manufacturer, Long hourlyRate , Long dailyRate , String vehicleNumber,  CarZone carZone , String modelName , RentalType rentalType, FuelType fuelType) {
        this.manufacturer = manufacturer;
        this.hourlyRate = hourlyRate;
        this.dailyRate = dailyRate;
        this.vehicleNumber = vehicleNumber;
        this.carZone = carZone;
        this.modelName = modelName;
        this.rentalType = rentalType;
        this.fuelType = fuelType;
        this.status = VehicleStatus.AVAILABLE;
        this.createdAt = LocalDateTime.now();
    }

    // 차량 상태는 Vehicle이 가진 데이터이므로 상태를 실제로 변경하는 책임도 Vehicle에 두는 것이 자연스럽다
    // 차량 자체의 상태 변경
    public void changeStatus(VehicleStatus status) {
        this.status = status;
    }

    // 차량 기본 정보를 수정한다
    public void updateInfo(String manufacturer, String modelName, String vehicleNumber,
                           RentalType rentalType, FuelType fuelType,
                           Long hourlyRate, Long dailyRate) {
        this.manufacturer = manufacturer;
        this.modelName = modelName;
        this.vehicleNumber = vehicleNumber;
        this.rentalType = rentalType;
        this.fuelType = fuelType;
        this.hourlyRate = hourlyRate;
        this.dailyRate = dailyRate;
    }
}
