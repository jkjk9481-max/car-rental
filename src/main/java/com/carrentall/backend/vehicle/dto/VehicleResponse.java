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
    private Long hourlyRate;
    private Long dailyRate;

    public VehicleResponse(Long id, String manufacturer, String modelName, String vehicleNumber, RentalType rentalType, FuelType fuelType, VehicleStatus status, Long hourlyRate, Long dailyRate) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.modelName = modelName;
        this.vehicleNumber = vehicleNumber;
        this.rentalType = rentalType;
        this.fuelType = fuelType;
        this.status = status;
        this.hourlyRate = hourlyRate;
        this.dailyRate = dailyRate;
    }
}
