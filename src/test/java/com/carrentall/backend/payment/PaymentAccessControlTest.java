package com.carrentall.backend.payment;


import com.carrentall.backend.carzone.entity.CarZone;
import com.carrentall.backend.carzone.repository.CarZoneRepository;
import com.carrentall.backend.payment.dto.PaymentResponse;
import com.carrentall.backend.payment.service.PaymentService;
import com.carrentall.backend.reservation.dto.ReservationCreateRequest;
import com.carrentall.backend.reservation.service.ReservationService;
import com.carrentall.backend.user.entity.User;
import com.carrentall.backend.user.repository.UserRepository;
import com.carrentall.backend.vehicle.entity.FuelType;
import com.carrentall.backend.vehicle.entity.RentalType;
import com.carrentall.backend.vehicle.entity.Vehicle;
import com.carrentall.backend.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PaymentAccessControlTest {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private CarZoneRepository carZoneRepository;
    @Autowired
    private ReservationService reservationService;


    private Long reservationId;
    private Long paymentId;
    private Vehicle vehicle;
    private LocalDateTime tomorrow10;

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

       reservationId  = reservationService.createReservation(
               request(tomorrow10.plusHours(2) , tomorrow10.plusHours(6)) , "tester@test.com").getReservationId();
       // reservationService.createReservation(...)실행 -> ReservationResponse 상자 하나가 나옴
        // 그 상자가 바로 .getReservationId()를 붙여서 호출 -> 상자 안의 예약번호만나옴
        // 꺼낸 그 숫자를 reservationId에 저장

        //  "tester@test.com 명의로, 내일 10시+2시간 ~ 내일 10시+6시간짜리 예약을 하나 만들고, 그 결과로 돌아온 응답 상자에서 예약 번호만 뽑아 reservationId 필드에 저장한다."

        paymentId = paymentService.pay(reservationId , "tester@test.com").getPaymentId();
    }

    private ReservationCreateRequest request(LocalDateTime startAt, LocalDateTime endAt) {
        ReservationCreateRequest request = new ReservationCreateRequest();
        ReflectionTestUtils.setField(request, "vehicleId", vehicle.getId());
        ReflectionTestUtils.setField(request, "startAt", startAt);
        ReflectionTestUtils.setField(request, "endAt", endAt);
        return request;
    }
}
