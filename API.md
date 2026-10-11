# API 목록

모든 경로는 `http://localhost:8080`을 기준으로 한다. 별도 표시 없으면 요청 헤더에 `Authorization: Bearer <JWT>`가 필요하다.

## 인증 (Auth)

| Method | 경로 | 설명 | 인증 |
|---|---|---|---|
| POST | /api/auth/signup | 회원가입 | 불필요 |
| POST | /api/auth/login | 로그인, JWT 발급 | 불필요 |

## 유저 (User)

| Method | 경로 | 설명 | 인증 |
|---|---|---|---|
| GET | /api/users/me | 현재 로그인한 유저 정보 조회 | 로그인 필요 |

## 차량 (Vehicle)

| Method | 경로 | 설명 | 인증 |
|---|---|---|---|
| GET | /api/vehicles | 전체 차량 목록 조회 | 로그인 필요 |
| GET | /api/vehicles/{vehicleId} | 차량 상세 조회 | 로그인 필요 |
| GET | /api/vehicles/status/{status} | 상태별 차량 조회 | 로그인 필요 |
| GET | /api/vehicles/search | 상태/연료/대여유형 조건으로 차량 검색 | 로그인 필요 |
| GET | /api/vehicles/carzone/{carZoneId} | 특정 카존의 차량 조회 | 로그인 필요 |
| GET | /api/vehicles/available | 카존·기간 조건으로 예약 가능한 차량 조회 | 로그인 필요 |
| POST | /api/vehicles | 차량 등록 | **관리자 전용** |
| PUT | /api/vehicles/{vehicleId} | 차량 정보 수정 | **관리자 전용** |
| PATCH | /api/vehicles/{vehicleId}/status | 차량 상태 변경 | **관리자 전용** |
| DELETE | /api/vehicles/{vehicleId} | 차량 삭제 | **관리자 전용** |

## 카존 (CarZone)

| Method | 경로 | 설명 | 인증 |
|---|---|---|---|
| GET | /api/carzones | 전체 카존 목록 조회 | 로그인 필요 |
| POST | /api/admin/carzones | 카존 등록 | **관리자 전용** |

## 예약 (Reservation)

| Method | 경로 | 설명 | 인증 |
|---|---|---|---|
| POST | /api/reservations | 예약 생성 | 로그인 필요 |
| GET | /api/reservations/my | 내 예약 목록 조회 | 로그인 필요 |
| GET | /api/reservations/{reservationId} | 예약 상세 조회 (본인 예약만) | 로그인 필요 |
| GET | /api/reservations/estimate | 예약 전 요금 견적 조회 | 로그인 필요 |
| PATCH | /api/reservations/{reservationId}/cancel | 예약 취소 (본인 예약만) | 로그인 필요 |
| GET | /api/admin/reservations | 전체 예약 목록 조회 | **관리자 전용** |

## 결제 (Payment, Mock)

| Method | 경로 | 설명 | 인증 |
|---|---|---|---|
| POST | /api/payments/reservations/{reservationId} | 예약에 대한 결제 생성 | 로그인 필요 |
| GET | /api/payments/me | 내 결제 내역 조회 | 로그인 필요 |
| GET | /api/payments/{paymentId} | 결제 상세 조회 (본인 결제만) | 로그인 필요 |
| PATCH | /api/payments/{paymentId}/cancel | 결제 취소 (본인 결제만) | 로그인 필요 |
| GET | /api/admin/payments | 전체 결제 내역 조회 | **관리자 전용** |

## 접근 제어 요약 (SecurityConfig 기준)

- `/api/auth/signup`, `/api/auth/login` — 인증 없이 접근 가능
- `/api/admin/**` 전체 — ADMIN 역할 필요
- `/api/vehicles`(POST), `/api/vehicles/{id}`(PUT/DELETE), `/api/vehicles/{id}/status`(PATCH) — ADMIN 역할 필요
- 그 외 모든 엔드포인트 — 로그인(JWT) 필요
