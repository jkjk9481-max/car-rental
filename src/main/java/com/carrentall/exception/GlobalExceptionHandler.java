package com.carrentall.exception;

import com.carrentall.backend.reservation.exception.ReservationNotFoundException;
import com.carrentall.backend.vehicle.exception.VehicleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
// 백엔드에서 발생한 예외를 잡아서, 프론트가 이해하기 쉬운 HTTP 에러 응답으로 바꿔주는 중앙 처리소야. 🔥
// VehicleController
//↓
//VehicleService
//↓
//VehicleNotFoundException 발생 💥
//↓
//GlobalExceptionHandler
//↓
//"VehicleNotFoundException은 내가 처리할게"
//↓
//HTTP 404 + 에러 JSON 반환
public class GlobalExceptionHandler {

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVehicleNotFoundException(VehicleNotFoundException e){
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value() , e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        // HTTP 상태코드는 404로 보내고
        //
        //Body에는
        //{
        //  status: 404,
        //  message: "차량을 찾을 수 없습니다."
        //}
        //
        //를 넣어라.
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReservationNotFoundException(ReservationNotFoundException e){
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value() , e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


}
