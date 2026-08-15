package com.carrentall.backend.reservation.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReservationExceptionHandler {

    @ExceptionHandler(VehicleNotAvailableException.class)
    public ResponseEntity<String> handleVehicleNotAvailableException(VehicleNotAvailableException e) {
        HttpStatus status = HttpStatus.CONFLICT; // 차량은 있지만 현재 상태와 예약 요청이 충돌함
        String message = e.getMessage();
        return new ResponseEntity<>(message, status);
    }
    
    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<String> handleReservationNotFoundException(ReservationNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 예약을 찾을 수 없다
        String message = e.getMessage();
        return new ResponseEntity<>(message, status);
    }
    
    @ExceptionHandler(ReservationAccessDeniedException.class)
    public ResponseEntity<String> handleReservationAccessDeniedException(ReservationAccessDeniedException e) {
        HttpStatus status = HttpStatus.FORBIDDEN; // 예약은 있지만 소유자가 다르다
        String message = e.getMessage();
        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(ReservationCannotCancelException.class)
    // 예약은 존재하지만 현재 상태 때문에 취소할 수 없을 때 발생
    public ResponseEntity<String> handleReservationCannotCancelException(ReservationCannotCancelException e) {
        HttpStatus status = HttpStatus.CONFLICT;
        String message = e.getMessage();
        return new ResponseEntity<>(message, status);
    }
    
    @ExceptionHandler(InvalidReservationTimeException.class)
    // 예약 시간이 잘못됐다라는걸 표현
    public ResponseEntity<String> handleInvalidReservationTimeException(InvalidReservationTimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = e.getMessage();
        return new ResponseEntity<>(message, status);
    }
    
    @ExceptionHandler(ReservationTimeConflictException.class)
    // 시간 겹침이라는 비즈니스 오류를 표현
    public ResponseEntity<String> handleReservationTimeConflictException(ReservationTimeConflictException e) {
        HttpStatus status = HttpStatus.CONFLICT;
        String message = e.getMessage();
        return new ResponseEntity<>(message, status);
    }
}
