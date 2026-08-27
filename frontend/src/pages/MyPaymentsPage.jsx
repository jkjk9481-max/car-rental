import { useEffect, useState } from "react";
// useState:
// - 결제 목록, 에러 메시지, 로딩 상태를 기억하기 위해 사용
//
// useEffect:
// - 페이지가 처음 열렸을 때 내 결제 목록 API를 호출하기 위해 사용

import { useNavigate } from "react-router-dom";
// useNavigate:
// - 다른 페이지로 이동할 때 사용
// - 예: 내 예약 목록, 차량 목록으로 이동

import axiosInstance from "../api/axiosInstance";
// axiosInstance:
// - 백엔드 API 호출 도구
// - localStorage에 저장된 accessToken을
//   Authorization: Bearer 토큰 형태로 자동으로 붙여줌

function MyPaymentsPage() {
    // 다른 페이지로 이동하기 위한 함수
    const navigate = useNavigate();

    // payments:
    // - 백엔드에서 받아온 내 결제 목록을 저장
    //
    // []:
    // - 처음에는 결제 목록이 없으므로 빈 배열로 시작
    const [payments, setPayments] = useState([]);

    // 에러 메시지 저장
    const [errorMessage, setErrorMessage] = useState("");

    // 데이터를 불러오는 중인지 저장
    const [loading, setLoading] = useState(true);

    // ------------------------------------------------
    // 내 결제 목록 조회 함수
    // ------------------------------------------------
    const fetchMyPayments = async () => {
        try {
            // 기존 에러 메시지 초기화
            setErrorMessage("");

            // 백엔드에 내 결제 목록 요청
            //
            // GET http://localhost:8080/api/payments/me
            const response = await axiosInstance.get("/api/payments/me");

            // response.data에는 결제 목록 배열이 들어있음
            //
            // 예:
            // [
            //   {
            //     paymentId: 1,
            //     reservationId: 1,
            //     amount: 24000,
            //     status: "PAID",
            //     paidAt: "2026-08-27T14:00:00"
            //   }
            // ]
            //
            // 받아온 결제 목록을 payments에 저장
            setPayments(response.data);

        } catch (error) {
            console.error(error);

            setErrorMessage("결제 목록을 불러오지 못했습니다.");

        } finally {
            // 성공하거나 실패해도 로딩은 끝남
            setLoading(false);
        }
    };

    // ------------------------------------------------
    // 페이지가 처음 열렸을 때 실행
    // ------------------------------------------------
    useEffect(() => {
        fetchMyPayments();
    }, []);
    // []:
    // 페이지가 처음 열렸을 때 한 번만 실행


    // ------------------------------------------------
    // 결제 취소 함수
    // ------------------------------------------------
    const handleCancelPayment = async (paymentId) => {

        // 진짜 취소할 건지 사용자에게 확인
        const confirmed = window.confirm(
            "정말 결제를 취소하시겠습니까?"
        );

        if (!confirmed) {
            return;
        }

        try {
            setErrorMessage("");

            // 결제 취소 API 호출
            //
            // paymentId가 1이면:
            // PATCH http://localhost:8080/api/payments/1/cancel
            await axiosInstance.patch(
                `/api/payments/${paymentId}/cancel`
            );

            // 결제 취소 성공 후 목록 다시 조회
            //
            // 그래야 화면에서도
            // PAID → CANCELED 변경된 것을 확인할 수 있음
            await fetchMyPayments();

        } catch (error) {
            console.error(error);

            // 백엔드가 에러 메시지를 보내준 경우
            if (error.response) {
                const message =
                    typeof error.response.data === "string"
                        ? error.response.data
                        : "결제 취소에 실패했습니다.";

                setErrorMessage(message);
                return;
            }

            setErrorMessage("결제 취소에 실패했습니다.");
        }
    };


    // ------------------------------------------------
    // 아직 결제 목록을 불러오는 중
    // ------------------------------------------------
    if (loading) {
        return (
            <main>
                <h1>내 결제 목록</h1>
                <p>결제 목록을 불러오는 중입니다...</p>
            </main>
        );
    }


    // ------------------------------------------------
    // 실제 화면
    // ------------------------------------------------
    return (
        <main>
            <h1>내 결제 목록</h1>

            {/* 내 예약 목록으로 이동 */}
            <button
                type="button"
                onClick={() => navigate("/reservations/my")}
            >
                내 예약 목록
            </button>

            {/* 차량 목록으로 이동 */}
            <button
                type="button"
                onClick={() => navigate("/vehicles")}
            >
                차량 목록
            </button>


            {/* 에러 메시지가 있을 경우 출력 */}
            {errorMessage && (
                <p>{errorMessage}</p>
            )}


            {/* 결제 내역이 하나도 없는 경우 */}
            {payments.length === 0 && !errorMessage && (
                <p>결제 내역이 없습니다.</p>
            )}


            <div>
                {/* payments 배열의 결제를 하나씩 화면에 출력 */}
                {payments.map((payment) => (
                    <div key={payment.paymentId}>

                        <h2>
                            결제 번호: {payment.paymentId}
                        </h2>

                        <p>
                            예약 번호: {payment.reservationId}
                        </p>

                        <p>
                            결제 금액: {payment.amount}원
                        </p>

                        <p>
                            결제 상태: {payment.status}
                        </p>

                        <p>
                            결제 시간: {payment.paidAt}
                        </p>


                        {/* PAID 상태의 결제만 취소 가능 */}
                        {payment.status === "PAID" && (
                            <button
                                type="button"
                                onClick={() =>
                                    handleCancelPayment(
                                        payment.paymentId
                                    )
                                }
                            >
                                결제 취소
                            </button>
                        )}

                    </div>
                ))}
            </div>

        </main>
    );
}

export default MyPaymentsPage;