package com.carrentall.backend.vehicle.dto;

import com.carrentall.backend.vehicle.entity.FuelType;
import com.carrentall.backend.vehicle.entity.RentalType;
import com.carrentall.backend.vehicle.entity.VehicleStatus;
import lombok.Getter;

@Getter
public class VehicleResponse {
    
    // 서버가 클라이언트에게 보여줄 차량 정보를 담는 응답 전용 객체

    private Long id;
    private String manufacturer;
    private String modelName;
    private String vehicleNumber;
    private RentalType rentalType;
    private FuelType fuelType;
    private VehicleStatus status;
    private Long carZoneId; // 이름이 같은 장소도 구분할 수 있는 식별자
    private String carZoneName; // 사용자가 알아볼 수 있는 대여장소이름
    private String carZoneAddress; // 실제로 찾아갈 위치
    private Long hourlyRate;
    private Long dailyRate;

    public VehicleResponse(Long id, String manufacturer, String modelName, String vehicleNumber, RentalType rentalType, FuelType fuelType, VehicleStatus status, Long carZoneId , String carZoneName , String carZoneAddress  , Long hourlyRate, Long dailyRate) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.modelName = modelName;
        this.vehicleNumber = vehicleNumber;
        this.rentalType = rentalType;
        this.fuelType = fuelType;
        this.status = status;
        this.carZoneId = carZoneId;
        this.carZoneName = carZoneName;
        this.carZoneAddress = carZoneAddress;
        this.hourlyRate = hourlyRate;
        this.dailyRate = dailyRate;
    }
}
