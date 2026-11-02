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
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isForbidden());

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
