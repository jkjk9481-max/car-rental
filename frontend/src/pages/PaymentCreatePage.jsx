import { useState } from "react";
// useState:
// - 결제 성공 결과, 에러 메시지, 로딩 상태를 기억하기 위해 사용

import { useNavigate, useParams } from "react-router-dom";
// useParams:
// - URL에서 reservationId를 꺼내기 위해 사용
// - 예: /payments/reservations/1 에서 1을 꺼냄
//
// useNavigate:
// - 결제 후 다른 페이지로 이동할 때 사용

import axiosInstance from "../api/axiosInstance";
// axiosInstance:
// - 백엔드 API 호출 도구
// - localStorage에 accessToken이 있으면 Authorization 헤더를 자동으로 붙여줌

function PaymentCreatePage() {
    // URL에서 reservationId 꺼내기
    //
    // 예:
    // 현재 주소가 /payments/reservations/1 이면
    // reservationId는 "1"
    const { reservationId } = useParams();

    // 페이지 이동 함수
    const navigate = useNavigate();

    // payment:
    // - 결제 성공 후 백엔드가 보내준 결제 결과를 저장
    // - 처음에는 결제 전이니까 null
    const [payment, setPayment] = useState(null);

    // errorMessage:
    // - 결제 실패 시 화면에 보여줄 메시지
    const [errorMessage, setErrorMessage] = useState("");

    // loading:
    // - 결제 요청 중인지 표시
    // - 결제 버튼 여러 번 누르는 걸 막는 데 사용
    const [loading, setLoading] = useState(false);

    // 결제 버튼 클릭 시 실행되는 함수
    const handlePayment = async () => {
        // 결제 전에 한 번 확인
        const confirmed = window.confirm("정말 결제하시겠습니까?");

        if (!confirmed) {
            return;
        }

        try {
            // 결제 시작
            setLoading(true);

            // 이전 에러 메시지 초기화
            setErrorMessage("");

            // POST /api/payments/reservations/{reservationId} 요청
            //
            // 실제 요청 예:
            // POST http://localhost:8080/api/payments/reservations/1
            //
            // Body는 보내지 않음
            // 결제 금액은 백엔드에서 reservation.totalPrice를 사용함
            const response = await axiosInstance.post(
                `/api/payments/reservations/${reservationId}`
            );

            // 결제 성공 결과를 payment state에 저장
            //
            // 예:
            // {
            //   paymentId: 1,
            //   reservationId: 1,
            //   amount: 24000,
            //   status: "PAID",
            //   paidAt: "2026-08-27T13:40:00"
            // }
            setPayment(response.data);
        } catch (error) {
            console.error(error);

            // 백엔드가 에러 응답을 준 경우
            // 예:
            // - 이미 결제된 예약입니다.
            // - 결제할 수 있는 예약 상태가 아닙니다.
            // - 해당 예약에 접근할 수 없습니다.
            if (error.response) {
                const message =
                    typeof error.response.data === "string"
                        ? error.response.data
                        : "결제에 실패했습니다.";

                setErrorMessage(message);
                return;
            }

            // 백엔드 서버에 요청 자체가 안 간 경우
            if (error.request) {
                setErrorMessage("백엔드 서버에 연결할 수 없습니다.");
                return;
            }

            // 그 외 알 수 없는 오류
            setErrorMessage("알 수 없는 오류가 발생했습니다.");
        } finally {
            // 성공하든 실패하든 결제 요청 종료
            setLoading(false);
        }
    };

    return (
        <main>
            <h1>결제하기</h1>

            {/* 어떤 예약을 결제하는지 확인용 */}
            <p>예약 ID: {reservationId}</p>

            {/* 에러 메시지가 있으면 화면에 출력 */}
            {errorMessage && <p>{errorMessage}</p>}

            {/* 아직 결제 성공 결과가 없을 때 */}
            {!payment && (
                <button
                    type="button"
                    onClick={handlePayment}
                    disabled={loading}
                >
                    {loading ? "결제 처리 중..." : "결제하기"}
                </button>
            )}

            {/* 결제 성공 결과가 있으면 결제 정보 출력 */}
            {payment && (
                <div>
                    <h2>결제 완료</h2>

                    <p>결제 번호: {payment.paymentId}</p>
                    <p>예약 번호: {payment.reservationId}</p>
                    <p>결제 금액: {payment.amount}원</p>
                    <p>결제 상태: {payment.status}</p>
                    <p>결제 시간: {payment.paidAt}</p>
                </div>
            )}

            <button
                type="button"
                onClick={() => navigate("/reservations/my")}
            >
                내 예약 목록으로 돌아가기
            </button>

            <button
                type="button"
                onClick={() => navigate("/payments/my")}
            >
                내 결제 목록 보기
            </button>
            
        </main>
    );
}

export default PaymentCreatePage;