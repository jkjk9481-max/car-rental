package com.carrentall.backend.vehicle.exception;


public class DuplicateVehicleNumberException extends RuntimeException{
    // 실행 중 발생할 수 있는 예외를 내가 직접 만든다


    // 왜 409 Conflict로 처리하는가??
    // 요청 JSON의 형식 자체는 정상이고 필수 값 검증도 통과했다
    // 하지만 요청한 차량번호가 이미 데이터베이스에 있기 떄문에 현재 서버 상태와 충돌한다
    // 409 -> 요쳥은 이해했지만 현재 데이터 상태와 충돌함
    public DuplicateVehicleNumberException(String message) {
        super(message);
    }
}
