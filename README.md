# Car Rental (쏘카형 차량 예약 서비스)

학습 및 취업 포트폴리오 목적으로 만든 차량 예약 웹 서비스입니다. 쏘카처럼 장소와 시간을 선택해 차량을 검색하고, 예약·결제(Mock)·취소·내역 조회까지의 흐름을 구현했습니다. (실제 업체 제휴, 수익화, 실결제 연동은 범위 밖입니다.)

## 소개

- 가상의 카셰어링/렌터카 업체 한 곳을 가정하고, 가상 차량 데이터로 예약 흐름을 구현했습니다.
- 사용자는 차량을 검색해 상세 정보를 보고, 원하는 시간으로 예약한 뒤 Mock 결제를 진행합니다. 예약은 언제든 조회·취소할 수 있습니다.
- 관리자는 전체 예약/결제 현황을 조회할 수 있습니다.
- 예약 시간 충돌 방지, 동시 예약 요청 시 하나만 성공하도록 하는 동시성 제어, 본인 예약만 조회·취소 가능하도록 하는 접근 제어를 핵심 과제로 삼아 구현했습니다.
- 데이터 구조는 [ERD.md](./ERD.md), 전체 엔드포인트는 [API.md](./API.md)에 정리되어 있습니다.

## 기술 스택

**백엔드**
- Java 17, Spring Boot 4.1
- Spring Data JPA, Spring Security, Spring Validation
- PostgreSQL
- JWT (jjwt)
- JUnit 5 (통합 테스트, 동시성 테스트)
- Gradle

**프론트엔드**
- React 19, React Router 7
- Axios
- Vite

## 실행 방법

### 1. 사전 준비
- JDK 17
- PostgreSQL (로컬 실행 중이어야 함)
- Node.js (프론트엔드 실행용)

### 2. 데이터베이스 준비
PostgreSQL에 운영용 DB와 테스트용 DB를 각각 만듭니다.

```sql
CREATE DATABASE car_rental;
CREATE DATABASE car_rental_test;
```

`src/main/resources/application.properties`의 접속 정보(URL/계정/비밀번호)를 본인 환경에 맞게 확인하세요.

### 3. 환경변수(.env) 설정
프로젝트 루트에 `.env` 파일을 만들고 JWT 서명 키를 설정합니다. (`.env`는 `.gitignore`에 포함되어 커밋되지 않습니다.)

```
JWT_SECRET=임의의_32바이트_이상_랜덤_문자열
```

### 4. 백엔드 실행

```bash
./gradlew bootRun
```

기본적으로 `http://localhost:8080`에서 API 서버가 뜹니다. `spring.jpa.hibernate.ddl-auto=update` 설정으로 테이블은 자동 생성됩니다.

### 5. 프론트엔드 실행

`frontend/.env.example`을 복사해 `frontend/.env`를 만들고, 백엔드 API 주소를 설정합니다.

```bash
cd frontend
cp .env.example .env
```

```
VITE_API_BASE_URL=http://localhost:8080
```

그 다음 의존성 설치 후 개발 서버를 실행합니다.

```bash
npm install
npm run dev
```

Vite 개발 서버(기본 `http://localhost:5173`)가 뜨고, `.env`에 설정한 주소(기본 `http://localhost:8080`)로 API를 호출합니다.

### 6. 테스트 실행

```bash
./gradlew test
```

예약 시간 충돌 거부, 동시 예약 시 하나만 성공, 타인 예약 접근 거부 — 3가지 자동화 통합 테스트가 `car_rental_test` DB를 대상으로 실행됩니다.

## 주요 화면

| 화면 | 설명 |
|---|---|
| 로그인 / 회원가입 | 이메일·비밀번호 기반 인증, JWT 발급 |
| 차량 목록 | 예약 가능한 차량 리스트 조회 |
| 차량 상세 | 차량 정보 확인 후 예약 페이지로 이동 |
| 예약 생성 | 시작/종료 시간 입력 후 예약 생성, 요금 산정 |
| 내 예약 목록 | 본인 예약 조회, 취소 |
| 결제 생성 / 내 결제 목록 | Mock 결제 진행 및 내역 조회 |
| 관리자: 전체 예약 / 전체 결제 | 관리자 권한으로 전체 현황 조회 |

> 화면 스크린샷은 추후 추가 예정입니다.

## 핵심 문제 해결

### 1. 예약 시간 충돌 방지
같은 차량에 대해 시간이 겹치는 예약이 이미 있으면 새 예약을 거절합니다. `existsByVehicleAndStatusAndStartAtLessThanAndEndAtGreaterThan` 쿼리로 "기존 시작 < 새 종료 AND 기존 종료 > 새 시작" 조건을 검사해 겹침 여부를 판단합니다.

### 2. 동시 예약 요청 제어 (동시성)
여러 사용자가 같은 차량·같은 시간을 동시에 예약 시도해도 하나만 성공해야 합니다. 차량 조회 시 비관적 락(`PESSIMISTIC_WRITE`)을 걸어 동시 접근 시 한 트랜잭션씩 순차적으로 처리되도록 했습니다. 스레드 풀로 여러 요청을 동시에 발생시켜 정확히 1건만 성공하고 DB에도 1건만 저장되는지 검증하는 통합 테스트로 확인했습니다.

### 3. 본인 예약 접근 제어
예약 조회·취소 시 요청자의 이메일로 조회한 사용자와 예약 소유자가 일치하는지 확인하고, 다르면 접근을 거부합니다(`ReservationAccessDeniedException`). 타인이 내 예약을 조회하거나 취소할 수 없도록 통합 테스트로 검증했습니다.

### 4. JWT 기반 인증/인가
로그인 성공 시 JWT를 발급하고, 이후 모든 요청은 `Authorization: Bearer <token>` 헤더로 인증합니다. 요청마다 필터에서 토큰을 검증해 로그인한 사용자의 이메일을 꺼내 서비스 로직에 전달합니다.

## 현재 진행 상황 / 알려진 제한사항

- 결제는 Mock이며 실제 PG사 연동은 되어 있지 않습니다.
- 모바일 반응형, 배포, 시드 데이터 투입은 진행 예정입니다.
- 상세 진행 기록은 `codex.note`를 참고하세요.
