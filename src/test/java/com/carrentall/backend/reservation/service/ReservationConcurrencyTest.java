package com.carrentall.backend.reservation.service;

import com.carrentall.backend.carzone.entity.CarZone;
import com.carrentall.backend.carzone.repository.CarZoneRepository;
import com.carrentall.backend.reservation.dto.ReservationCreateRequest;
import com.carrentall.backend.reservation.repository.ReservationRepository;
import com.carrentall.backend.user.entity.User;
import com.carrentall.backend.user.repository.UserRepository;
import com.carrentall.backend.vehicle.entity.FuelType;
import com.carrentall.backend.vehicle.entity.RentalType;
import com.carrentall.backend.vehicle.entity.Vehicle;
import com.carrentall.backend.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

// 전체적인 틀 ->
//  > “스레드 풀로 같은 차량과 시간의 예약을 여러 번 실행한다.
//  각 요청은 별도 트랜잭션에서 차량 잠금을 잡고 시간 충돌을 검사한다.
//  먼저 처리된 요청이 예약을 저장하면 뒤의 요청은 그 예약을 보고 거부된다.
//  > 모든 작업이 끝난 뒤 성공·실패 횟수와 DB 저장 건수를 확인한다.”

@SpringBootTest // 진짜 앱처럼 Spring 컨테이너(DB 연결, Service, Repository 전부)를 띄워서 테스트한다는 뜻.
@ActiveProfiles("test") // 운영 DB가 아니라 car_rental_test 테스트 DB를
public class ReservationConcurrencyTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private CarZoneRepository carZoneRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    private Vehicle vehicle;
    private LocalDateTime tomorrow10;



    @BeforeEach // 매 테스트 전 준비물
    void setUp(){
        userRepository.save(new User("tester@test.com", "password", "테스터", "010-0000-0000"));
        CarZone carZone = carZoneRepository.save(new CarZone("테스트 카존", "테스트 주소"));
        vehicle = vehicleRepository.save(new Vehicle(
                "현대", 10000L, 80000L, "99가9999", carZone, "아반떼",
                RentalType.CAR_SHARING, FuelType.GASOLINE));

        // 과거 시간 예약은 Service가 거절하므로 "내일"을 기준으로 시간을 만든다.
        tomorrow10 = LocalDateTime.now().plusDays(1)
                .withHour(10).withMinute(0).withSecond(0).withNano(0);
    }

    @AfterEach // 수동 정리 , 매 테스트 후 뒷정리
    void deleteAll(){
        // 왜 이 순서로 지워야 하나: 예약(Reservation)은 유저와 차량을 참조(외래키)하고 있습니다. 
        // 차량/유저를 먼저 지우면 "참조하고 있는 데이터가 남아있는데 부모를 지운다"는 에러(외래키 제약 위반)가 납니다. 
        // 그래서 참조하는 쪽(예약)을 먼저 지우고, 참조당하는 쪽(차량 → 카존 → 유저)을 나중에 지워
        reservationRepository.deleteAll();
        vehicleRepository.deleteAll();
        carZoneRepository.deleteAll();
        userRepository.deleteAll();
    }
    
    @Test
    void 동시에_예약하면_하나만_성공한다() throws Exception{
        // //  - 스레드 = 동시에 실행되는 독립적인 실행 흐름
        //    //  - 스레드 풀(ExecutorService) = 여러 스레드를 쉽게 만들고 관리하는 도구
        
        //    //  - 작업을 넘기면 영수증(Future)을 받는다
        //    //  - 영수증으로 "끝날 때까지 기다리기"가 가능하다
        //    //  - invokeAll()을 쓰면 "작업 여러 개 넘기고, 다 끝날 때까지 기다리기"를 한 번에 할 수 있다
        int threadCount = 10; // 일꾼 수
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount); // 일꾼들을 담을 스레드 풀을 준비한다
        
        AtomicInteger successCount = new AtomicInteger(0); // 성공 셀 ( 안전한 카운터 )
        AtomicInteger failCount = new AtomicInteger(0); // 실패 셀

        // 일꾼들에게 실제로 시킬 일 만들기
        // 모든 일꾼이 끝날 때 까지 기다리고 , 최종 점검하기

        List<Future<?>> futures = new ArrayList<>();

        // for문을 10번 돌면서, 매번 "같은 차량, 같은 시간(내일 10~14시)으로 예약해봐라"
        //  submit은 그 일에 대한 영수증(Future)을 돌려줍니다 → 나중에 "다 끝났는지" 확인하려고 리스트(futures)에 모아둡니다.
        for(int i = 0; i < threadCount; i++){
            // 작업장에 "이 일 좀 해줘" 라고 작업 하나를 던진다
            Future<?> future = executorService.submit(() -> {
                try{
                    // try - catch로 감싸는 이유 - 일꾼이 예외를 던지면 그 작업은 조용히 끝나버리고 우리는 "실패했다"는 걸 못 세게 됩니다
                    // 그래서 직접 카운트해야한다
                    reservationService.createReservation(
                            //  각 일꾼이 하는 일: createReservation 호출 → 성공하면 successCount +1,
                            //  예외(충돌 등)가 터지면 failCount +1.
                            //  try-catch로 감싼 이유는 예외를 안 잡으면 그 일꾼의 작업이 조용히 죽어버려서 실패했다는 걸
                            //  셀 수 없기 때문.
                            request(tomorrow10 , tomorrow10.plusHours(4)) , "tester@test.com");
                    successCount.incrementAndGet(); 
                }catch (Exception e){
                    failCount.incrementAndGet();
                }
            });
            futures.add(future);
        }

        // 영수증 10개를 하나씩 확인하면서 "이 작업 끝났어?"를 묻고, 안 끝났으면 끝날 때까지 기다린다 전부 통과해야
        // 다음줄로 넘어간다
        for(Future<?> future : futures){
            future.get(); // 이 작업이 끝날때까지 기다린다
        }

        assertEquals(1, successCount.get());
        assertEquals(threadCount - 1, failCount.get());
        assertEquals(1, reservationRepository.findAll().size());

        // 10명 중 딱 1명만 성공했어야한다
        // 나머지 9며은 실패(시간 충돌 예외)했어야 한다
        // DB를 직접 들여다봐도 예약이 진짜 1건만 저장되어 있어야한다

    }

    private ReservationCreateRequest request(LocalDateTime startAt, LocalDateTime endAt) {
        ReservationCreateRequest request = new ReservationCreateRequest();
        ReflectionTestUtils.setField(request, "vehicleId", vehicle.getId());
        ReflectionTestUtils.setField(request, "startAt", startAt);
        ReflectionTestUtils.setField(request, "endAt", endAt);
        return request;
    }
}
