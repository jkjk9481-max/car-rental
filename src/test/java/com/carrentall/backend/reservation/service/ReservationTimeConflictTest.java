package com.carrentall.backend.reservation.service;

import com.carrentall.backend.carzone.entity.CarZone;
import com.carrentall.backend.carzone.repository.CarZoneRepository;
import com.carrentall.backend.reservation.dto.ReservationCreateRequest;
import com.carrentall.backend.reservation.dto.ReservationResponse;
import com.carrentall.backend.reservation.exception.ReservationTimeConflictException;
import com.carrentall.backend.user.entity.User;
import com.carrentall.backend.user.repository.UserRepository;
import com.carrentall.backend.vehicle.entity.FuelType;
import com.carrentall.backend.vehicle.entity.RentalType;
import com.carrentall.backend.vehicle.entity.Vehicle;
import com.carrentall.backend.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// 예약 시간 충돌 규칙을 실제 Service + Repository + 테스트 DB로 확인하는 통합 테스트다.
//
// @SpringBootTest : 실제 앱처럼 Spring 컨테이너(Service, Repository, DB 연결)를 띄운다.
// @ActiveProfiles("test") : application-test.properties를 적용해 car_rental_test DB를 사용한다.
// @Transactional : 각 테스트가 끝나면 DB 변경을 자동으로 롤백해 테스트끼리 데이터가 섞이지 않게 한다.
//   (동시성 테스트에서는 스레드마다 별도 트랜잭션이 필요하므로 이 어노테이션을 쓰지 않는다.)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReservationTimeConflictTest {

    // 테스트 대상(Service)과, 테스트 데이터를 준비할 Repository를 Spring이 주입해 준다.
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CarZoneRepository carZoneRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle vehicle;
    private LocalDateTime tomorrow10; // 내일 10:00. 시간 계산의 기준점으로 쓴다.

    // @BeforeEach : 각 @Test가 실행되기 직전에 매번 실행된다. 공통 준비(given)를 여기에 모은다.
    @BeforeEach
    void setUp() {
        userRepository.save(new User("tester@test.com", "password", "테스터", "010-0000-0000"));
        CarZone carZone = carZoneRepository.save(new CarZone("테스트 카존", "테스트 주소"));
        vehicle = vehicleRepository.save(new Vehicle(
                "현대", 10000L, 80000L, "99가9999", carZone, "아반떼",
                RentalType.CAR_SHARING, FuelType.GASOLINE));

        // 과거 시간 예약은 Service가 거절하므로 "내일"을 기준으로 시간을 만든다.
        tomorrow10 = LocalDateTime.now().plusDays(1)
                .withHour(10).withMinute(0).withSecond(0).withNano(0);

    }

    // 테스트 이름만 읽어도 "무엇을 하면 어떻게 되어야 하는지" 알 수 있게 짓는다.
    @Test
    void 기존_예약과_시간이_겹치면_예약이_거절된다() {
        // given: 내일 10:00 ~ 14:00 예약이 이미 있다.
        reservationService.createReservation(
                request(tomorrow10, tomorrow10.plusHours(4)), "tester@test.com");

        // when & then: 12:00 ~ 16:00은 12~14시가 겹치므로 충돌 예외가 발생해야 한다.
        // assertThrows는 "이 코드를 실행하면 해당 예외가 던져져야 한다"는 검증이다.
        // 예외가 안 나거나 다른 예외가 나면 테스트가 실패한다.
        assertThrows(ReservationTimeConflictException.class, () ->
                reservationService.createReservation(
                        request(tomorrow10.plusHours(2), tomorrow10.plusHours(6)), "tester@test.com"));
    }

    @Test
    void 기존_예약_종료시각에_바로_시작하는_예약은_허용된다() {
        // given: 내일 10:00 ~ 14:00 예약이 이미 있다.
        reservationService.createReservation(
                request(tomorrow10, tomorrow10.plusHours(4)), "tester@test.com");

        // when: 기존 종료(14:00)와 새 시작(14:00)이 같은 14:00 ~ 18:00을 예약한다.
        // 겹침 조건이 "기존 종료 > 새 시작"이라 같은 시각은 겹치지 않는다.
        ReservationResponse response = reservationService.createReservation(
                request(tomorrow10.plusHours(4), tomorrow10.plusHours(8)), "tester@test.com");

        // then: 예외 없이 예약이 만들어졌고, 시작 시각이 요청과 같다.
        // assertEquals(기대값, 실제값) 순서로 쓴다.
        assertEquals(tomorrow10.plusHours(4), response.getStartAt());
    }

    // 요청 DTO에는 setter와 모든 필드를 받는 생성자가 없다. 테스트에서는 ReflectionTestUtils로
    // private 필드에 값을 직접 넣어 요청 객체를 만든다. (운영 코드를 테스트 때문에 바꾸지 않기 위함)
    private ReservationCreateRequest request(LocalDateTime startAt, LocalDateTime endAt) {
        ReservationCreateRequest request = new ReservationCreateRequest();
        ReflectionTestUtils.setField(request, "vehicleId", vehicle.getId());
        ReflectionTestUtils.setField(request, "startAt", startAt);
        ReflectionTestUtils.setField(request, "endAt", endAt);
        return request;
    }


}
