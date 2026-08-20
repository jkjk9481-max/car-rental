package com.carrentall.backend.reservation.controller;

import com.carrentall.backend.reservation.dto.ReservationCreateRequest;
import com.carrentall.backend.reservation.dto.ReservationResponse;
import com.carrentall.backend.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/my")
    public List<ReservationResponse> getMyReservations(Authentication authentication) {
        // Authentication 자체가 JWT를 검증하는 것은 아니에요. 앞에서 JwtAuthenticationFilter가 JWT를 먼저 검증하고, 검증된 사용자 정보를 Authentication에 담아둡니다.
        String email =  authentication.getName(); // 인증 정보에서 현재 사용자의 email을 꺼낸다 ( DB 조회는 하지 않는다 )
        return reservationService.getMyReservations(email);
        // Spring Security가 이미 인증해 둔 현재 로그인 사용자의 인증 정보에서 email을 꺼내 email 변수에 저장한다.

        //  1. 사용자가 JWT를 담아 GET /api/reservations/my 요청
        //  2. Spring Security가 JWT를 검증
        //  3. 검증된 사용자의 인증 정보를 Authentication에 저장
        //  4. Controller가 authentication.getName()으로 email을 꺼냄
        //  5. Controller가 email을 Service에 전달
        //  6. Service가 DB에서 그 email에 해당하는 User를 조회
        //  7. 그 User의 예약 목록만 조회
        //  8. List<ReservationResponse>로 반환
    }

    //  현재 로그인한 사용자가
    //  자기 예약 중 특정 예약 하나를 상세 조회한다
    @GetMapping("/{reservationId}")
    public ReservationResponse getMyReservation(@PathVariable Long reservationId , Authentication authentication) {
        String email =  authentication.getName();
        return reservationService.getMyReservation(reservationId, email);

        //  GET /api/reservations/1 요청
        //  → reservationId = 1
        //  → JWT에서 현재 로그인 사용자 email 추출
        //  → Service에 reservationId와 email 전달
        //  → 내 예약이면 ReservationResponse 반환
    }

    @PatchMapping("/{reservationId}/cancel")
    public ReservationResponse cancelReservation(@PathVariable Long reservationId , Authentication authentication) {
        String email =  authentication.getName();
        return reservationService.cancelReservation(reservationId, email);

        //  PATCH /api/reservations/1/cancel
        //  → reservationId = 1
        //  → authentication.getName()으로 현재 사용자 email 추출
        //  → reservationService.cancelReservation(reservationId, email)
        //  → ReservationResponse 반환
    }
}
