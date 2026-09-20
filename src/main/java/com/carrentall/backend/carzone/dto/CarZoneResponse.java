package com.carrentall.backend.carzone.dto;

import lombok.Getter;

@Getter
// 대여 장소 한 곳의 조회 결과를 담는 응답 DTO
public class CarZoneResponse {
    //  - Request: “이 이름과 주소로 대여 장소를 등록해 주세요.”
    //  - Response: “조회한 장소의 ID, 이름, 주소는 이것입니다.”

    private Long id;
    private String name;
    private String address;

    public CarZoneResponse(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
}
