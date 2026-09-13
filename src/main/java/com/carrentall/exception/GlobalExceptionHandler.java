package com.carrentall.exception;

import com.carrentall.backend.payment.exception.PaymentNotFoundException;
import com.carrentall.backend.reservation.exception.ReservationNotFoundException;
import com.carrentall.backend.reservation.exception.ReservationTimeConflictException;
import com.carrentall.backend.vehicle.exception.VehicleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

// Exception
//→ Service가 "문제 생김"을 알림
//
//@ExceptionHandler
//→ 특정 Exception 담당자를 지정
//
//@RestControllerAdvice
//→ 여러 Controller의 예외를 한 곳에서 관리
//
//ErrorResponse
//→ 프론트에 보여줄 에러 데이터
//
//ResponseEntity
//→ HTTP 상태코드 + Body를 함께 반환
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

    @ExceptionHandler(ReservationTimeConflictException.class)
    public ResponseEntity<ErrorResponse> handleReservationTimeConflictException(ReservationTimeConflictException e){
        ErrorResponse response = new ErrorResponse(HttpStatus.CONFLICT.value() , e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFoundException(PaymentNotFoundException e){
        ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value() , e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Validation 실패
    //↓
    //MethodArgumentNotValidException 발생
    //↓
    //@ExceptionHandler가 잡음
    //↓
    //Validation 오류 하나 꺼냄
    //↓
    //HTTP 응답으로 반환
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value() , e.getMessage());

        // getBindingResult = DTO를 검증한 결과 전체를 가져오는것
        // getFieldError = 필드 오류 하나 가져오기
        // FieldError = DTO의 특정 필드에서 발생한 Validation 오류 정보

        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }



}
