import { useEffect, useState } from "react";
// useState:
// - 전체 예약 목록, 에러 메시지, 로딩 상태를 기억
//
// useEffect:
// - 관리자 예약 페이지가 처음 열릴 때 API 호출

import { useNavigate } from "react-router-dom";
// 다른 페이지로 이동할 때 사용

import axiosInstance from "../api/axiosInstance";
// 백엔드 API 호출 도구
// 저장된 accessToken을 Authorization 헤더에 자동으로 붙여줌

function AdminReservationsPage() {
    const navigate = useNavigate();

    // 관리자가 조회한 전체 예약 목록
    const [reservations, setReservations] = useState([]);

    // 에러 메시지
    const [errorMessage, setErrorMessage] = useState("");

    // API 호출 중인지 표시
    const [loading, setLoading] = useState(true);

    // 전체 예약 조회
    const fetchReservations = async () => {
        try {
            setErrorMessage("");

            // 관리자 전체 예약 조회 API
            // GET http://localhost:8080/api/admin/reservations
            const response = await axiosInstance.get(
                "/api/admin/reservations"
            );

            // 백엔드가 보내준 전체 예약 목록 저장
            setReservations(response.data);

        } catch (error) {
            console.error(error);

            // 403이면 로그인은 했지만 ADMIN 권한이 없는 경우
            if (error.response?.status === 403) {
                setErrorMessage("관리자만 접근할 수 있습니다.");
                return;
            }

            setErrorMessage("전체 예약 목록을 불러오지 못했습니다.");

        } finally {
            setLoading(false);
        }
    };

    // 페이지가 처음 열릴 때 전체 예약 조회
    useEffect(() => {
        fetchReservations();
    }, []);

    // 로딩 화면
    if (loading) {
        return (
            <main>
                <h1>관리자 - 전체 예약</h1>
                <p>예약 목록을 불러오는 중입니다...</p>
            </main>
        );
    }

    return (
        <main>
            <h1>관리자 - 전체 예약</h1>

            {/* 관리자 전체 결제 페이지로 이동 */}
            <button
                type="button"
                onClick={() => navigate("/admin/payments")}
            >
                전체 결제 보기
            </button>

            {/* 일반 차량 목록으로 이동 */}
            <button
                type="button"
                onClick={() => navigate("/vehicles")}
            >
                차량 목록
            </button>

            {/* 에러 메시지 */}
            {errorMessage && <p>{errorMessage}</p>}

            {/* 예약이 하나도 없을 경우 */}
            {reservations.length === 0 && !errorMessage && (
                <p>예약 내역이 없습니다.</p>
            )}

            {/* 전체 예약 목록 출력 */}
            <div>
                {reservations.map((reservation) => (
                    <div key={reservation.reservationId}>
                        <h2>
                            예약 번호: {reservation.reservationId}
                        </h2>

                        {/* 관리자는 모든 사용자의 예약을 보므로 userId도 중요 */}
                        <p>사용자 ID: {reservation.userId}</p>

                        <p>차량 ID: {reservation.vehicleId}</p>
                        <p>시작 시간: {reservation.startAt}</p>
                        <p>종료 시간: {reservation.endAt}</p>
                        <p>예약 상태: {reservation.status}</p>
                        <p>총 금액: {reservation.totalPrice}원</p>
                        <p>예약 생성일: {reservation.createdAt}</p>
                    </div>
                ))}
            </div>
        </main>
    );
}

export default AdminReservationsPage;