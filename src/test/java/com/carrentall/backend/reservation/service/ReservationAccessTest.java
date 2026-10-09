package com.carrentall.backend.reservation.service;

import com.carrentall.backend.carzone.entity.CarZone;
import com.carrentall.backend.carzone.repository.CarZoneRepository;
import com.carrentall.backend.reservation.dto.ReservationCreateRequest;
import com.carrentall.backend.reservation.dto.ReservationResponse;
import com.carrentall.backend.reservation.exception.ReservationAccessDeniedException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest // 테스트에서는 Spring 컨테이너를 띄우기
@ActiveProfiles("test") // test 프로필을 켜라는 뜻 , 이걸 켜면 application-test-properties에 적힌 설정을 덮어써서 진짜 DB는 안건드리고 테스트 DB만 건들임
@Transactional // 테스트 메서드 하나가 끝낼떄마다 그 안에서 한 DB 변경을 자동으로 롤백
public class ReservationAccessTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private CarZoneRepository carZoneRepository;

    private Vehicle vehicle;
    private LocalDateTime tomorrow10;

    // 요청 DTO에는 setter와 모든 필드를 받는 생성자가 없다. 테스트에서는 ReflectionTestUtils로
    // private 필드에 값을 직접 넣어 요청 객체를 만든다. (운영 코드를 테스트 때문에 바꾸지 않기 위함)
    private ReservationCreateRequest request(LocalDateTime startAt, LocalDateTime endAt) {
        ReservationCreateRequest request = new ReservationCreateRequest();
        ReflectionTestUtils.setField(request, "vehicleId", vehicle.getId());
        ReflectionTestUtils.setField(request, "startAt", startAt);
        ReflectionTestUtils.setField(request, "endAt", endAt);
        return request;
    }

    @BeforeEach // 테스트 메서드 하나가 실행되기 직전마다 자동으로 실행 , 그래서 "모든 테스트가 공통으로 필요한 준비물을 모아두는 역할"
    public void setUp(){
        userRepository.save(new User("tester@test.com" , "password" , "테스터훈" , "010-1111-2222"));
        // 예약 주인이 될 유저
        userRepository.save(new User("other@test.com" , "password" , "테스터박" , "010-1111-2222"));
        // 남의 예약에 접근할 유저

        CarZone carZone = carZoneRepository.save(new CarZone("테스트 카존", "테스트 주소"));
        vehicle = vehicleRepository.save(new Vehicle(
                "현대", 10000L, 80000L, "99가9999", carZone, "아반떼",
                RentalType.CAR_SHARING, FuelType.GASOLINE));

        tomorrow10 = LocalDateTime.now().plusDays(1)
                .withHour(10).withMinute(0).withSecond(0).withNano(0); 
        // 내일 10:00 이라는 고정된 기준 시각을 하나 만들어둠
    }

    @Test
    // 흐름 given - when - then
    public void 타인의_예약을_조회하면_접근이_거부된다(){
        // // given: 내일 10:00 ~ 14:00 예약이 이미 있다.
        ReservationResponse response = reservationService.createReservation(
                request(tomorrow10.plusHours(2) , tomorrow10.plusHours(6)) , "tester@test.com");
        // 서버가 방금 만든 예약 정보를 돌려주는 DTO



        assertThrows(ReservationAccessDeniedException.class, () ->
                reservationService.getMyReservation(response.getReservationId() , "other@test.com"));
        // 이 예약의 주인이 요청한 사람과 다르면 던지는 예외

    }

    @Test
    public void 타인의_예약을_취소하면_접근이_거부된다(){
        ReservationResponse response = reservationService.createReservation(
                request(tomorrow10.plusHours(2) , tomorrow10.plusHours(6)) , "tester@test.com");

        assertThrows(ReservationAccessDeniedException.class, () ->
                reservationService.cancelReservation(response.getReservationId() , "other@test.com"));
    }
}
