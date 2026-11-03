package com.carrentall.backend.vehicle.controller;

import com.carrentall.backend.carzone.entity.CarZone;
import com.carrentall.backend.carzone.repository.CarZoneRepository;
import com.carrentall.backend.vehicle.dto.VehicleCreateRequest;
import com.carrentall.backend.vehicle.entity.FuelType;
import com.carrentall.backend.vehicle.entity.RentalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // 실제 Spring 컨테이너 전체를 띄움
@AutoConfigureMockMvc // MockMvc 빈을 자동으로 만들어서 주입받을 수 있게함
@ActiveProfiles("test") // 테스트 DB사용
public class VehicleAccessControlTest {

    @Autowired 
    private MockMvc mockMvc; // HTTP 요청 보낼 도구
    @Autowired
    private ObjectMapper objectMapper; // VehicleCreateRequest를 JSON 문자열로 바꿀도구
    @Autowired
    private CarZoneRepository carZoneRepository; // 테스트용 카존 하나 저장할 용도

    private Long carZoneId;


    @BeforeEach
    void setUp(){
        CarZone carZone = carZoneRepository.save(new CarZone("테스트 카존" , "테스트 주소"));
        carZoneId = carZone.getId();

        // CarZone 생성자는 ReservationTimeConflictTest에서 쓴 것과 동일합니다 (이름, 주소 두 개 받음).
        // carZoneId를 필드로 빼두는 이유는, 각 @Test 메서드에서 요청 JSON을 만들음
    }

    @Test
    @WithMockUser(roles = "USER")
    // JUnit 테스트에서는 보통 그냥 메서드에 throws Exception을 붙여서 써야함
    void USER_권한으로_차량_등록을_시도하면_거부된다() throws Exception {
        mockMvc.perform(post("/api/vehicles") // 진짜 서버를 켜지않고 , URL로 POST 요청을 보내는걸 흉내낸다 하지만 Security필터 , Controller , Service까지 실제 코드가 다 동작한다 "HTTP 요청만 실제로 안보내쓸뿐" 나머지는 진짜
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request()))) // VehicleCreateRequest 객체(정보)를 JSON 문자열로 바꿔서 요청본문에 담는다
                .andExpect(status().isForbidden());
                // 응답이 403이어야 테스트 통과 , 위에 붙은 @WithMockUser(roles = "USER") 때문에
                // 이 요청은 USER 권한으로 인증된 상태로 들어가고, SecurityConfig에서 POST /api/vehicles는 ADMIN만 허용이라 403으로
                //  막혀야 맞습니다.

    }

    // 데이터 흐름 (두 테스트 공통)
    //request() 호출 → VehicleCreateRequest 객체 생성
    //→ JSON 문자열로 변환
    //→ mockMvc가 POST /api/vehicles로 "요청"
    //→ Security 필터가 @WithMockUser의 role을 확인
    //   ├─ USER면: 여기서 바로 403 반환, Controller까지 못 감
    //   └─ ADMIN이면: 통과 → Controller → Service → DB 저장 → 200 반환
    //→ andExpect가 실제 응답 상태코드와

    @Test
    @WithMockUser(roles = "ADMIN")
    // "USER 테스트와 같은 이유로 throws Exception 필요"
    void ADMIN_권한으로_차량_등록을_시도하면_성공한다() throws Exception{
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk());
    }

    private VehicleCreateRequest request(){
        VehicleCreateRequest request = new VehicleCreateRequest();
        // ReflectionTestUtils.setField(대상, "필드이름", 넣을값)
        ReflectionTestUtils.setField(request , "manufacturer" , "현대");
        ReflectionTestUtils.setField(request , "modelName" , "아반떼");
        ReflectionTestUtils.setField(request , "vehicleNumber" , "99가9999");
        ReflectionTestUtils.setField(request , "carZoneId" , carZoneId);
        ReflectionTestUtils.setField(request , "rentalType" , RentalType.CAR_SHARING);
        ReflectionTestUtils.setField(request , "fuelType" , FuelType.GASOLINE);
        ReflectionTestUtils.setField(request , "hourlyRate" , 10000L);
        ReflectionTestUtils.setField(request , "dailyRate" , 80000L);

        return request;
    }
}
