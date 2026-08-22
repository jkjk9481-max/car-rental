import { useState } from "react";
// useState:
// - 사용자가 입력한 예약 시작 시간, 종료 시간을 기억하기 위해 사용
// - 에러 메시지, 성공 메시지도 기억함

import { useNavigate, useParams } from "react-router-dom";
// useParams:
// - URL 주소에서 vehicleId 값을 꺼낼 때 사용
// - 예: /vehicles/1/reservation 에서 1을 꺼냄
//
// useNavigate:
// - 예약 성공 후 다른 페이지로 이동할 때 사용
// - 차량 상세 페이지로 돌아갈 때도 사용

import axiosInstance from "../api/axiosInstance";
// axiosInstance:
// - 백엔드 API 호출 도구
// - localStorage에 accessToken이 있으면 Authorization 헤더를 자동으로 붙여줌

function ReservationCreatePage() {
    // URL에서 vehicleId 꺼내기
    //
    // 예:
    // 현재 주소가 /vehicles/1/reservation 이면
    // vehicleId는 "1"
    const { vehicleId } = useParams();

    // 페이지 이동 함수
    const navigate = useNavigate();

    // startAt:
    // - 예약 시작 시간
    // - input type="datetime-local"에서 사용자가 입력한 값이 들어감
    const [startAt, setStartAt] = useState("");

    // endAt:
    // - 예약 종료 시간
    const [endAt, setEndAt] = useState("");

    // errorMessage:
    // - 예약 실패 시 화면에 보여줄 에러 메시지
    const [errorMessage, setErrorMessage] = useState("");

    // successMessage:
    // - 예약 성공 시 화면에 보여줄 성공 메시지
    const [successMessage, setSuccessMessage] = useState("");

    // 예약 생성 form 제출 시 실행되는 함수
    const handleCreateReservation = async (event) => {
        // form 제출 시 브라우저 새로고침 방지
        event.preventDefault();

        // 이전 에러/성공 메시지 초기화
        setErrorMessage("");
        setSuccessMessage("");

        try {
            // 백엔드 예약 생성 API 호출
            //
            // 실제 요청:
            // POST http://localhost:8080/api/reservations
            //
            // Body:
            // {
            //   vehicleId: 1,
            //   startAt: "2026-08-22T15:00",
            //   endAt: "2026-08-22T18:00"
            // }
            const response = await axiosInstance.post("/api/reservations", {
                vehicleId: Number(vehicleId),
                startAt: startAt,
                endAt: endAt,
            });

            // 성공 메시지 출력
            setSuccessMessage("예약이 완료되었습니다.");

            // 개발자 도구 Console에서 응답 확인
            console.log("예약 생성 결과:", response.data);

            // 잠깐 성공 메시지를 보여준 뒤 차량 목록으로 이동
            // 나중에 내 예약 목록 페이지를 만들면 "/reservations/my"로 바꿀 예정
            setTimeout(() => {
                navigate("/reservations/my");
            }, 500);
        } catch (error) {
            console.error(error);

            // 백엔드가 에러 응답을 준 경우
            // 예:
            // - 과거 시간으로 예약할 수 없습니다.
            // - 예약 시작 시간은 종료 시간보다 이전이어야 합니다.
            // - 이미 해당 시간에 예약된 차량입니다.
            // - 예약할 수 없는 차량입니다.
            if (error.response) {
                const message =
                    typeof error.response.data === "string"
                        ? error.response.data
                        : "예약 생성에 실패했습니다.";

                setErrorMessage(message);
                return;
            }

            // 백엔드 서버에 요청 자체가 못 간 경우
            if (error.request) {
                setErrorMessage("백엔드 서버에 연결할 수 없습니다.");
                return;
            }

            // 그 외 알 수 없는 오류
            setErrorMessage("알 수 없는 오류가 발생했습니다.");
        }
    };

    return (
        <main>
            <h1>예약하기</h1>

            {/* 지금 어떤 차량을 예약하는지 확인용 */}
            <p>차량 ID: {vehicleId}</p>

            {/* 예약 생성 form */}
            <form onSubmit={handleCreateReservation}>
                <div>
                    <label htmlFor="startAt">예약 시작 시간</label>
                    <input
                        id="startAt"
                        type="datetime-local"
                        value={startAt}
                        onChange={(event) => setStartAt(event.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="endAt">예약 종료 시간</label>
                    <input
                        id="endAt"
                        type="datetime-local"
                        value={endAt}
                        onChange={(event) => setEndAt(event.target.value)}
                        required
                    />
                </div>

                {/* 에러 메시지가 있으면 화면에 출력 */}
                {errorMessage && <p>{errorMessage}</p>}

                {/* 성공 메시지가 있으면 화면에 출력 */}
                {successMessage && <p>{successMessage}</p>}

                <button type="submit">예약 생성</button>
            </form>

            {/* 차량 상세 페이지로 돌아가기 */}
            <button
                type="button"
                onClick={() => navigate(`/vehicles/${vehicleId}`)}
            >
                차량 상세로 돌아가기
            </button>
        </main>
    );
}

export default ReservationCreatePage;

// useParams()
// → URL에서 vehicleId를 꺼낸다.
//
// useState()
// → startAt, endAt 입력값을 기억한다.
//
// datetime-local
// → 날짜 + 시간을 입력받는 input이다.
//
// axiosInstance.post("/api/reservations", ...)
// → 백엔드 예약 생성 API를 호출한다.
//
// Number(vehicleId)
// → URL에서 꺼낸 vehicleId는 문자열이라 숫자로 바꿔준다.