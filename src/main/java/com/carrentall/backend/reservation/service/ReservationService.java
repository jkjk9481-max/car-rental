package com.carrentall.backend.reservation.service;

import com.carrentall.backend.reservation.dto.ReservationCreateRequest;
import com.carrentall.backend.reservation.dto.ReservationResponse;
import com.carrentall.backend.reservation.entity.Reservation;
import com.carrentall.backend.reservation.entity.ReservationStatus;
import com.carrentall.backend.reservation.exception.ReservationAccessDeniedException;
import com.carrentall.backend.reservation.exception.ReservationCannotCancelException;
import com.carrentall.backend.reservation.exception.ReservationNotFoundException;
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

import java.util.List;

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

    private ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getUser().getId(),
                reservation.getVehicle().getId(),
                reservation.getStartAt(),
                reservation.getEndAt(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
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
        vehicle.changeStatus(VehicleStatus.RESERVED);
        reservationRepository.save(reservation);

        return toResponse(reservation);
    }

    @Transactional(readOnly = true) // 데이터를 변경하지 않고 조회만 하는 작업 ( 조회만 할떄 많이 사용함)
    // 저장 * 수정 * 삭제는 -> 붙히지않는다
    public List<ReservationResponse> getMyReservations(String email){
        // email로 User 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

        // 해당 User의 예약 목록 조회
        return reservationRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
        //  1. Controller에서 로그인 사용자의 email 전달
        //  2. email로 User 조회
        //  3. User가 없으면 예외 발생 → 메서드 즉시 종료
        //  4. User가 있으면 findByUser(user)로 예약 목록 조회
        //  5. 각각의 Reservation을 toResponse()로 변환
        //  6. List<ReservationResponse>로 모아서 반환
    }

    // 1. email로 현재 User 조회
    //  2. reservationId로 Reservation 조회
    //  3. 예약이 없으면 ReservationNotFoundException
    //  4. 예약의 user.id와 현재 user.id 비교
    //  5. 다르면 ReservationAccessDeniedException
    //  6. 같으면 toResponse(reservation) 반환
    @Transactional(readOnly = true)
    //  조회 전용 메서드라는 의미가 명확해짐
    //  불필요한 변경 감지 부담을 줄일 수 있음
    public ReservationResponse getMyReservation(Long reservationId , String email){
       User user = userRepository.findByEmail(email)
               .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

       Reservation reservation = reservationRepository.findById(reservationId)
               .orElseThrow(() -> new ReservationNotFoundException("예약을 찾을 수 없습니다."));

       if(!reservation.getUser().getId().equals(user.getId())){
           throw new ReservationAccessDeniedException("해당 예약에 접근할 수 없습니다");
       }

       return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse cancelReservation(Long reservationId , String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("예약을 찾을 수 없습니다."));

        if(!reservation.getUser().getId().equals(user.getId())){
            throw new ReservationAccessDeniedException("해당 예약에 접근할 수 없습니다");
        }

        if(!ReservationStatus.RESERVED.equals(reservation.getStatus())){
            throw new ReservationCannotCancelException("예약을 취소 할 수 없습니다.");
        }

        reservation.cancel();
        reservation.getVehicle().changeStatus(VehicleStatus.AVAILABLE);
        return toResponse(reservation);

        //  예약 취소 성공
        //  → 예약 상태 변경
        //  → 차량 상태 변경
        //  → 트랜잭션 정상 종료
        //  → DB 반영
    }






}
