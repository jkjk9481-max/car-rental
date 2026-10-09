import { useEffect, useState } from "react";
// useState:
// - 사용자가 입력한 예약 시작 시간, 종료 시간을 기억하기 위해 사용
// - 에러 메시지, 성공 메시지도 기억함

import { useNavigate, useParams, useSearchParams } from "react-router-dom";
// useParams:
// - URL 주소에서 vehicleId 값을 꺼낼 때 사용
// - 예: /vehicles/1/reservation 에서 1을 꺼냄
//
// useNavigate:
// - 예약 성공 후 다른 페이지로 이동할 때 사용
// - 차량 상세 페이지로 돌아갈 때도 사용

import axiosInstance from "../api/axiosInstance";
import { createTimeSearch, readTimeSearch, validateReservationTime } from "../utils/reservationTime";
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

    // 상세 화면이 URL에 담아 준 시간을 읽는다. 새로고침해도 URL의 값은 유지된다.
    const [searchParams] = useSearchParams();
    const initialTime = readTimeSearch(searchParams);

    // startAt:
    // - 예약 시작 시간
    // - input type="datetime-local"에서 사용자가 입력한 값이 들어감
    const [startAt, setStartAt] = useState(initialTime.startAt);

    // endAt:
    // - 예약 종료 시간
    const [endAt, setEndAt] = useState(initialTime.endAt);

    // URL에서 받은 값은 초기값이며, 사용자는 아래 입력창에서 자유롭게 수정할 수 있다.
    const [timeNotice, setTimeNotice] = useState(initialTime.message);

    // 예상 요금 응답, 조회 중 상태, 조회 오류를 각각 화면 state에 보관한다.
    const [priceEstimate, setPriceEstimate] = useState(null);
    const [estimatingPrice, setEstimatingPrice] = useState(false);
    const [estimateError, setEstimateError] = useState("");

    // 뒤로/앞으로 이동하여 같은 예약 화면의 URL 조건이 바뀌는 경우도 반영한다.
    // 입력창 수정은 URL을 바꾸지 않으므로 사용자가 입력 중인 값을 덮어쓰지 않는다.
    useEffect(() => {
        const received = readTimeSearch(searchParams);
        setStartAt(received.startAt);
        setEndAt(received.endAt);
        setTimeNotice(received.message);
    }, [searchParams, vehicleId]);

    // 예상 요금은 조회 버튼을 눌렀을 때만 서버에 요청한다.
    const handleEstimatePrice = async () => {
        setEstimateError("");
        setPriceEstimate(null);

        // 브라우저에서 먼저 안내하고, 서버 Service도 같은 조건을 다시 검증한다.
        const timeError = validateReservationTime(startAt, endAt);
        if (timeError) {
            setEstimateError(timeError);
            return;
        }

        try {
            setEstimatingPrice(true);
            // GET 조회 요청: 차량 ID와 대여 시간을 쿼리 파라미터로 보낸다.
            // 서버 응답의 data에는 vehicleId, startAt, endAt, totalPrice가 들어온다.
            const response = await axiosInstance.get("/api/reservations/estimate", {
                params: {
                    vehicleId: Number(vehicleId),
                    startAt,
                    endAt,
                },
            });
            setPriceEstimate(response.data);
        } catch (error) {
            console.error(error);
            const data = error.response?.data;
            const message = typeof data === "string" ? data : data?.message;
            setEstimateError(message || "예상 요금을 조회하지 못했습니다. 잠시 후 다시 시도해 주세요.");
        } finally {
            setEstimatingPrice(false);
        }
    };

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

        // 전달받은 뒤 시간이 지났거나 사용자가 수정했을 수 있어 제출 시 다시 검사한다.
        const timeError = validateReservationTime(startAt, endAt);
        if (timeError) {
            setErrorMessage(timeError);
            return;
        }
        setTimeNotice("");

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

            // 잠깐 성공 메시지를 보여준 뒤 내 예약 목록으로 이동한다.
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

            <p>검색 시간이 전달되면 아래에 자동으로 채워집니다. 필요하면 수정해 주세요.</p>
            <p>예약 생성 시 선택한 시간의 예약 가능 여부를 다시 확인합니다.</p>
            {timeNotice && <p role="alert">전달된 시간을 다시 입력해 주세요. {timeNotice}</p>}

            {/* 예약 생성 form */}
            <form onSubmit={handleCreateReservation} noValidate>
                <div>
                    <label htmlFor="startAt">예약 시작 시간</label>
                    <input
                        id="startAt"
                        type="datetime-local"
                        value={startAt}
                        disabled={estimatingPrice}
                        onChange={(event) => {
                            setStartAt(event.target.value);
                            // 시간 입력이 바뀌면 이전 조건으로 받은 예상 요금은 지운다.
                            setPriceEstimate(null);
                            setEstimateError("");
                        }}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="endAt">예약 종료 시간</label>
                    <input
                        id="endAt"
                        type="datetime-local"
                        value={endAt}
                        disabled={estimatingPrice}
                        onChange={(event) => {
                            setEndAt(event.target.value);
                            setPriceEstimate(null);
                            setEstimateError("");
                        }}
                        required
                    />
                </div>

                {/* 예상 요금 조회는 GET 요청이라 예약을 생성하거나 저장하지 않는다. */}
                <button type="button" onClick={handleEstimatePrice} disabled={estimatingPrice}>
                    {estimatingPrice ? "요금 계산 중..." : "예상 요금 조회"}
                </button>
                {estimateError && <p role="alert">{estimateError}</p>}
                {priceEstimate && (
                    <section aria-live="polite">
                        <h2>예상 요금</h2>
                        <p>대여 시간: {priceEstimate.startAt.replace("T", " ")}</p>
                        <p>반납 시간: {priceEstimate.endAt.replace("T", " ")}</p>
                        <p>예상 총요금: {Number(priceEstimate.totalPrice).toLocaleString("ko-KR")}원</p>
                    </section>
                )}

                {/* 에러 메시지가 있으면 화면에 출력 */}
                {errorMessage && <p role="alert">{errorMessage}</p>}

                {/* 성공 메시지가 있으면 화면에 출력 */}
                {successMessage && <p>{successMessage}</p>}

                <button type="submit">예약 생성</button>
            </form>

            {/* 수정한 시간이 유효하면 상세로 돌아갈 때도 함께 전달한다.
                잘못된 값은 넘기지 않으며, 서버 요청이나 예약 저장은 발생하지 않는다. */}
            <button
                type="button"
                onClick={() => {
                    const timeSearch = validateReservationTime(startAt, endAt)
                        ? "" : createTimeSearch(startAt, endAt);
                    navigate(`/vehicles/${vehicleId}${timeSearch}`);
                }}
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
