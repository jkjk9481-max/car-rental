import { useEffect, useState } from "react";
// useState:
// - 내 예약 목록, 에러 메시지, 로딩 상태를 기억하기 위해 사용
//
// useEffect:
// - 페이지가 처음 열렸을 때 내 예약 목록 API를 호출하기 위해 사용

import { useNavigate } from "react-router-dom";
// useNavigate:
// - 차량 목록 페이지로 이동하거나
// - 결제 페이지로 이동할 때 사용 예정

import axiosInstance from "../api/axiosInstance";
// axiosInstance:
// - 백엔드 API 호출 도구
// - localStorage에 accessToken이 있으면 Authorization 헤더를 자동으로 붙여줌

function MyReservationsPage() {
    // 페이지 이동 함수
    const navigate = useNavigate();

    // reservations:
    // - 백엔드에서 받아온 내 예약 목록을 저장하는 state
    //
    // setReservations:
    // - reservations 값을 변경하는 함수
    //
    // []:
    // - 처음에는 예약 목록이 없으니까 빈 배열로 시작
    const [reservations, setReservations] = useState([]);

    // errorMessage:
    // - 내 예약 목록 조회 실패나 예약 취소 실패 시 화면에 보여줄 메시지
    const [errorMessage, setErrorMessage] = useState("");

    // loading:
    // - 예약 목록을 불러오는 중인지 표시하는 값
    const [loading, setLoading] = useState(true);

    // 내 예약 목록을 가져오는 함수
    const fetchMyReservations = async () => {
        try {
            // 기존 에러 메시지 초기화
            setErrorMessage("");

            // GET /api/reservations/my 요청
            //
            // 실제 요청:
            // GET http://localhost:8080/api/reservations/my
            //
            // axiosInstance가 Authorization: Bearer 토큰을 자동으로 붙여줌
            const response = await axiosInstance.get("/api/reservations/my");

            // response.data에는 내 예약 목록 배열이 들어있음
            //
            // 예:
            // [
            //   {
            //     reservationId: 1,
            //     userId: 1,
            //     vehicleId: 1,
            //     startAt: "2026-08-23T10:00:00",
            //     endAt: "2026-08-23T13:00:00",
            //     status: "RESERVED",
            //     totalPrice: 24000,
            //     createdAt: "2026-08-22T15:00:00"
            //   }
            // ]
            setReservations(response.data);
        } catch (error) {
            console.error(error);
            setErrorMessage("내 예약 목록을 불러오지 못했습니다.");
        } finally {
            // 성공하든 실패하든 로딩 종료
            setLoading(false);
        }
    };

    // 페이지가 처음 열렸을 때 내 예약 목록 조회
    useEffect(() => {
        fetchMyReservations();
    }, []);
    // [] 의미:
    // - 처음 페이지가 열릴 때 한 번만 실행

    // 예약 취소 버튼 클릭 시 실행되는 함수
    const handleCancelReservation = async (reservationId) => {
        // confirm:
        // - 진짜 취소할 건지 사용자에게 한 번 물어봄
        const confirmed = window.confirm("정말 예약을 취소하시겠습니까?");

        if (!confirmed) {
            return;
        }

        try {
            setErrorMessage("");

            // PATCH /api/reservations/{reservationId}/cancel 요청
            //
            // 예:
            // reservationId가 1이면
            // PATCH http://localhost:8080/api/reservations/1/cancel
            await axiosInstance.patch(`/api/reservations/${reservationId}/cancel`);

            // 취소 성공 후 목록을 다시 불러옴
            // 그래야 화면에서 status가 CANCELED로 바뀐 걸 확인할 수 있음
            fetchMyReservations();
        } catch (error) {
            console.error(error);

            // 백엔드에서 문자열 에러 메시지를 보내준 경우 그걸 보여줌
            if (error.response) {
                const message =
                    typeof error.response.data === "string"
                        ? error.response.data
                        : "예약 취소에 실패했습니다.";

                setErrorMessage(message);
                return;
            }

            setErrorMessage("예약 취소에 실패했습니다.");
        }
    };

    // 로딩 중 화면
    if (loading) {
        return (
            <main>
                <h1>내 예약 목록</h1>
                <p>예약 목록을 불러오는 중입니다...</p>
            </main>
        );
    }

    return (
        <main>
            <h1>내 예약 목록</h1>

            {/* 차량 목록으로 돌아가기 */}
            <button type="button" onClick={() => navigate("/vehicles")}>
                차량 목록으로 이동
            </button>

            {/* 에러 메시지가 있으면 출력 */}
            {errorMessage && <p>{errorMessage}</p>}

            {/* 예약이 하나도 없을 때 */}
            {reservations.length === 0 && !errorMessage && (
                <p>예약 내역이 없습니다.</p>
            )}

            <div>
                {/* 예약 목록 반복 출력 */}
                {reservations.map((reservation) => (
                    <div key={reservation.reservationId}>
                        <h2>예약 번호: {reservation.reservationId}</h2>

                        <p>차량 ID: {reservation.vehicleId}</p>
                        <p>예약 시작: {reservation.startAt}</p>
                        <p>예약 종료: {reservation.endAt}</p>
                        <p>예약 상태: {reservation.status}</p>
                        <p>총 금액: {reservation.totalPrice}원</p>
                        <p>예약 생성일: {reservation.createdAt}</p>

                        {/* RESERVED 상태일 때만 취소 버튼 표시 */}
                        {reservation.status === "RESERVED" && (
                            <button
                                type="button"
                                onClick={() =>
                                    handleCancelReservation(reservation.reservationId)
                                }
                            >
                                예약 취소
                            </button>
                        )}

                        {/* 다음 단계에서 여기에 결제하기 버튼을 붙일 예정 */}
                        {/* RESERVED 상태일 때 결제 가능 */}
                    </div>
                ))}
            </div>
        </main>
    );
}

export default MyReservationsPage;

// GET /api/reservations/my
// → 내가 만든 예약 목록 조회
//
// useEffect
// → 페이지 열리자마자 내 예약 목록 가져오기
//
// reservations.map()
// → 예약 목록을 화면에 반복 출력
//
// PATCH /api/reservations/{id}/cancel
// → 예약 취소
//
// fetchMyReservations()
// → 취소 후 목록 새로고침