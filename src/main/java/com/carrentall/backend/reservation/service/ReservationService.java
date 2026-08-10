package com.carrentall.backend.reservation.service;

import com.carrentall.backend.reservation.dto.ReservationCreateRequest;
import com.carrentall.backend.reservation.dto.ReservationResponse;
import com.carrentall.backend.reservation.entity.Reservation;
import com.carrentall.backend.reservation.exception.VehicleNotAvailableException;
import com.carrentall.backend.reservation.repository.ReservationRepository;
import com.carrentall.backend.user.entity.User;
import com.carrentall.backend.user.repository.UserRepository;
import com.carrentall.backend.vehicle.entity.Vehicle;
import com.carrentall.backend.vehicle.entity.VehicleStatus;
import com.carrentall.backend.vehicle.exception.VehicleNotFoundException;
import com.carrentall.backend.vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository; // 예약 저장
    private final UserRepository userRepository; // 이메일로 로그인 사용자 조회
    private final VehicleRepository vehicleRepository; // ID로 예약 차량 조회

    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository, VehicleRepository vehicleRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public ReservationResponse createReservation(ReservationCreateRequest request , String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("예약할 수 없는 사용자입니다."));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException("해당 차량이 존재하지 않습니다."));

        if(VehicleStatus.AVAILABLE != vehicle.getStatus()){
            // 차량 상태가 AVAILABLE이 아니면 예약할 수 없는 차량이다
            throw new VehicleNotAvailableException("예약할 수 없는 차량입니다");
        }

        //     조건을 모두 통과하면
        //     User + Vehicle + 시작 시간 + 종료 시간으로
        //     Reservation 객체를 만든다
        Reservation reservation = new Reservation(user , vehicle , request.getStartAt() , request.getEndAt());
        reservationRepository.save(reservation);
        vehicle.changeStatus(VehicleStatus.RESERVED);

        ReservationResponse response = new ReservationResponse(reservation.getId() , user.getId() , vehicle.getId() , reservation.getStartAt() , reservation.getEndAt() , reservation.getStatus() , reservation.getCreatedAt());
        return response;
    }






}
