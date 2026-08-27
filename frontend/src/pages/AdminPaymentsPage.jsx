import { useEffect, useState } from "react";
// useState:
// - 전체 결제 목록, 에러 메시지, 로딩 상태 저장
//
// useEffect:
// - 페이지 처음 열릴 때 전체 결제 API 호출

import { useNavigate } from "react-router-dom";
// 관리자 예약 화면이나 차량 화면으로 이동

import axiosInstance from "../api/axiosInstance";
// JWT 토큰이 자동으로 붙는 API 호출 도구

function AdminPaymentsPage() {
    const navigate = useNavigate();

    // 전체 결제 목록
    const [payments, setPayments] = useState([]);

    // 에러 메시지
    const [errorMessage, setErrorMessage] = useState("");

    // 로딩 상태
    const [loading, setLoading] = useState(true);

    // 관리자 전체 결제 조회
    const fetchPayments = async () => {
        try {
            setErrorMessage("");

            // GET /api/admin/payments
            const response = await axiosInstance.get(
                "/api/admin/payments"
            );

            // 백엔드 응답을 payments에 저장
            setPayments(response.data);

        } catch (error) {
            console.error(error);

            // USER가 관리자 페이지에 접근하면 보통 403
            if (error.response?.status === 403) {
                setErrorMessage("관리자만 접근할 수 있습니다.");
                return;
            }

            setErrorMessage("전체 결제 목록을 불러오지 못했습니다.");

        } finally {
            setLoading(false);
        }
    };

    // 페이지가 처음 열릴 때 실행
    useEffect(() => {
        fetchPayments();
    }, []);

    // 로딩 화면
    if (loading) {
        return (
            <main>
                <h1>관리자 - 전체 결제</h1>
                <p>결제 목록을 불러오는 중입니다...</p>
            </main>
        );
    }

    return (
        <main>
            <h1>관리자 - 전체 결제</h1>

            {/* 관리자 전체 예약 페이지로 이동 */}
            <button
                type="button"
                onClick={() => navigate("/admin/reservations")}
            >
                전체 예약 보기
            </button>

            {/* 차량 목록으로 이동 */}
            <button
                type="button"
                onClick={() => navigate("/vehicles")}
            >
                차량 목록
            </button>

            {/* 에러 메시지 */}
            {errorMessage && <p>{errorMessage}</p>}

            {/* 결제 데이터가 없는 경우 */}
            {payments.length === 0 && !errorMessage && (
                <p>결제 내역이 없습니다.</p>
            )}

            {/* 전체 결제 목록 출력 */}
            <div>
                {payments.map((payment) => (
                    <div key={payment.paymentId}>
                        <h2>
                            결제 번호: {payment.paymentId}
                        </h2>

                        <p>예약 번호: {payment.reservationId}</p>
                        <p>결제 금액: {payment.amount}원</p>
                        <p>결제 상태: {payment.status}</p>
                        <p>결제 시간: {payment.paidAt}</p>
                    </div>
                ))}
            </div>
        </main>
    );
}

export default AdminPaymentsPage;