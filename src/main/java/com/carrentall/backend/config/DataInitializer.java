package com.carrentall.backend.config;

import com.carrentall.backend.carzone.entity.CarZone;
import com.carrentall.backend.carzone.repository.CarZoneRepository;
import com.carrentall.backend.user.entity.Role;
import com.carrentall.backend.user.entity.User;
import com.carrentall.backend.user.repository.UserRepository;
import com.carrentall.backend.vehicle.entity.FuelType;
import com.carrentall.backend.vehicle.entity.RentalType;
import com.carrentall.backend.vehicle.entity.Vehicle;
import com.carrentall.backend.vehicle.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
// 서버가 다 켜진 직후에 자동으로 실행되는 코드를 만들려면 정해둔 규칙을 따라야한다
// 그게 CommandLingRunner다
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CarZoneRepository carZoneRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, CarZoneRepository carZoneRepository, VehicleRepository vehicleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.carZoneRepository = carZoneRepository;
        this.vehicleRepository = vehicleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    // run은 서버를 켤 떄마다 매번 실행된다
    // 아무 조건 없이 저장 코드를 넣으면
    // 서버를 껐다 켤 때마다 똑같은 카존 , 차량 , 유저가 계속 중복으로 쌓인다
    // 그래서 이미 데이터가 있으면 아무것도 안하고 바로 끝내기 조건을 맨 처음에 둬야한다
    public void run(String... args) throws Exception {
        //데이터 흐름: run() 시작 → userRepository.count()로 현재 users 테이블 행 개수를 조회 →
        // 0보다 크면(이미 누군가 있으면) return으로 메서드 종료
        // → 0이면 아래로 내려가서 시드 데이터 생성(다음 단계에서 작성).
        if (userRepository.count() > 0) {
            return;
        }

        CarZone carZone = carZoneRepository.save(new CarZone("강남 카존", "서울시 강남구 테헤란"));

        Vehicle vehicle1 = vehicleRepository.save(new Vehicle(
                "현대", 3000L, 50000L, "12가3456",
                carZone, "아반떼", RentalType.CAR_SHARING, FuelType.GASOLINE
        ));

        Vehicle vehicle2 = vehicleRepository.save(new Vehicle(
                "기아", 4000L, 60000L, "34가5678",
                carZone, "스포티지", RentalType.CAR_SHARING, FuelType.GASOLINE
        ));

        User admin = new User("admin@test.com", passwordEncoder.encode("admin1234"), "관리자", "0101111222");
        admin.changeRole(Role.ADMIN);
        userRepository.save(admin);

        User user = new User("user@test.com", passwordEncoder.encode("user1234"), "홍길동", "01033334444");
        userRepository.save(user);

    }
}
