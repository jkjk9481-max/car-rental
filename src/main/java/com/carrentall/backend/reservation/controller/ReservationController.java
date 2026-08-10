package com.carrentall.backend.reservation.controller;

import com.carrentall.backend.reservation.dto.ReservationCreateRequest;
import com.carrentall.backend.reservation.dto.ReservationResponse;
import com.carrentall.backend.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 메서드 반환값을 JSON 응답으로 전달
@RequestMapping("/api/reservations") // 예약 API의 공통 주소 설정
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ReservationResponse createReservation(@RequestBody @Valid ReservationCreateRequest request , Authentication authentication) {
        // Authentication은 Spring Security가 관리하는 현재 로그인 사용자의 인증 정보 객체이다
        // "이 요청을 보낸 사용자가 누구인지 Spring Security가 정리해 둔 정보"
        //  HTTP 요청
        //  → Authorization 헤더에서 JWT 추출
        //  → JWT가 유효한지 확인
        //  → JWT에서 email과 role 추출
        //  → Authentication 객체 생성
        //  → SecurityContext에 저장
        //  → Controller 실행

        //  철수가 JWT를 가지고 예약 요청
        //  → 서버가 JWT 확인
        //  → “이 요청을 보낸 사람은 철수”라고 Authentication에 기록
        //  → Controller에 전달
        String email =  authentication.getName();
        //  authentication.getName()
        //  → "chulsoo@email.com"
        return reservationService.createReservation(request , email);
    }


}
