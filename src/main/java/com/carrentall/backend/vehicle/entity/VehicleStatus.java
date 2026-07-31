package com.carrentall.backend.vehicle.entity;

public enum VehicleStatus {

    AVAILABLE , // 예약 가능
    RESERVED , // 예약됨
    IN_USE , // 현재 대여 중
    MAINTENANCE , // 정비 중
    UNAVAILABLE; // 운영 중지
}
