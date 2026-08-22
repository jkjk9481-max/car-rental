import { useEffect, useState } from "react";
// useState:
// - 화면에서 기억해야 하는 값을 저장할 때 사용
// - 여기서는 vehicle, errorMessage를 기억함
//
// useEffect:
// - 페이지가 처음 열렸을 때 특정 코드를 실행할 때 사용
// - 여기서는 차량 상세 API를 호출할 때 사용

import { useNavigate, useParams } from "react-router-dom";
// useParams:
// - URL 주소에 들어있는 값을 꺼낼 때 사용
// - 예: /vehicles/1 에서 1을 꺼냄
//
// useNavigate:
// - 버튼 클릭 후 다른 페이지로 이동할 때 사용
// - 예: 차량 목록으로 돌아가기

import axiosInstance from "../api/axiosInstance";
// axiosInstance:
// - 백엔드 API를 호출하기 위해 만든 도구
// - baseURL이 http://localhost:8080으로 설정되어 있다면
//   axiosInstance.get("/api/vehicles/1")
//   → 실제 요청은 http://localhost:8080/api/vehicles/1 로 나감
//
// - localStorage에 accessToken이 있으면
//   Authorization: Bearer 토큰값
//   헤더를 자동으로 붙여주는 역할도 함

function VehicleDetailPage() {
    // 현재 URL에서 vehicleId 값을 꺼냄
    //
    // 예를 들어 현재 주소가:
    // http://localhost:5173/vehicles/1
    //
    // App.jsx에 이런 라우팅이 있다면:
    // <Route path="/vehicles/:vehicleId" element={<VehicleDetailPage />} />
    //
    // 여기서 vehicleId는 "1"이 됨
    const { vehicleId } = useParams();

    // 페이지 이동 함수
    // navigate("/vehicles")를 실행하면 차량 목록 페이지로 이동함
    const navigate = useNavigate();

    // vehicle:
    // - 백엔드에서 받아온 차량 상세 정보를 저장하는 state
    //
    // setVehicle:
    // - vehicle 값을 바꾸는 함수
    //
    // null:
    // - 처음에는 아직 백엔드에서 차량 정보를 받아오기 전이므로 null로 시작
    const [vehicle, setVehicle] = useState(null);

    // errorMessage:
    // - 차량 상세 조회 실패 시 화면에 보여줄 에러 메시지
    //
    // 처음에는 에러가 없으므로 빈 문자열로 시작
    const [errorMessage, setErrorMessage] = useState("");

    // useEffect:
    // - 이 페이지가 처음 열렸을 때 실행됨
    // - vehicleId가 바뀌면 다시 실행됨
    //
    // 예:
    // /vehicles/1 → 1번 차량 조회
    // /vehicles/2 → 2번 차량 조회
    useEffect(() => {
        // 백엔드에서 차량 한 대의 상세 정보를 가져오는 함수
        const fetchVehicle = async () => {
            try {
                // GET /api/vehicles/{vehicleId} 요청
                //
                // vehicleId가 1이면:
                // GET http://localhost:8080/api/vehicles/1
                //
                // vehicleId가 2이면:
                // GET http://localhost:8080/api/vehicles/2
                const response = await axiosInstance.get(`/api/vehicles/${vehicleId}`);

                // response.data에는 백엔드가 보내준 차량 상세 정보가 들어있음
                //
                // 예:
                // {
                //   id: 1,
                //   manufacturer: "Hyundai",
                //   modelName: "Avante",
                //   vehicleNumber: "12가3456",
                //   rentalType: "CAR_SHARING",
                //   fuelType: "GASOLINE",
                //   status: "AVAILABLE",
                //   hourlyRate: 8000,
                //   dailyRate: 70000
                // }
                //
                // 이 데이터를 vehicle state에 저장
                setVehicle(response.data);
            } catch (error) {
                // API 요청 실패 시 개발자 도구 Console에 에러 출력
                //
                // 실패 원인 예:
                // - 백엔드 서버 꺼짐
                // - 토큰 없음
                // - vehicleId에 해당하는 차량 없음
                // - API 주소 틀림
                // - 권한 문제
                console.error(error);

                // 사용자 화면에 보여줄 에러 메시지 저장
                setErrorMessage("차량 상세 정보를 불러오지 못했습니다.");
            }
        };

        // 위에서 만든 차량 상세 조회 함수를 실제로 실행
        fetchVehicle();
    }, [vehicleId]);
    // [vehicleId] 의미:
    // - vehicleId가 바뀔 때마다 useEffect를 다시 실행한다.
    // - 예: /vehicles/1에서 /vehicles/2로 바뀌면 다시 API 호출

    // 에러 메시지가 있으면 아래 화면을 먼저 보여줌
    //
    // return을 여기서 해버리면 아래쪽 정상 화면은 실행되지 않음
    if (errorMessage) {
        return (
            <main>
                <h1>차량 상세</h1>

                {/* 에러 메시지 출력 */}
                <p>{errorMessage}</p>

                {/* 클릭하면 차량 목록 페이지로 이동 */}
                <button type="button" onClick={() => navigate("/vehicles")}>
                    차량 목록으로 돌아가기
                </button>
            </main>
        );
    }

    // 아직 vehicle 데이터가 없으면 로딩 화면을 보여줌
    //
    // 왜 필요하냐?
    // - API 요청은 시간이 조금 걸림
    // - 처음 렌더링 시점에는 vehicle이 아직 null임
    // - 그런데 vehicle.manufacturer 같은 걸 바로 쓰면 에러가 날 수 있음
    //
    // 그래서 vehicle이 없을 때는 먼저 "불러오는 중"을 보여줌
    if (!vehicle) {
        return (
            <main>
                <h1>차량 상세</h1>
                <p>차량 정보를 불러오는 중입니다...</p>
            </main>
        );
    }

    // vehicle 데이터가 정상적으로 들어온 후 보여줄 실제 차량 상세 화면
    return (
        <main>
            <h1>차량 상세</h1>

            {/* 차량 제조사 + 모델명 */}
            <h2>
                {vehicle.manufacturer} {vehicle.modelName}
            </h2>

            {/* 차량 상세 정보 출력 */}
            <p>차량 번호: {vehicle.vehicleNumber}</p>
            <p>대여 타입: {vehicle.rentalType}</p>
            <p>연료 타입: {vehicle.fuelType}</p>
            <p>상태: {vehicle.status}</p>
            <p>시간당 요금: {vehicle.hourlyRate}원</p>
            <p>일일 요금: {vehicle.dailyRate}원</p>

            {/* 차량 목록 페이지로 돌아가는 버튼 */}
            <button type="button" onClick={() => navigate("/vehicles")}>
                차량 목록으로 돌아가기
            </button>

            {/* 다음 단계에서 여기에 예약하기 버튼을 추가할 예정 */}
            {/* 예: 예약 페이지로 이동 */}

            <button type="button" onClick={() => navigate(`/vehicles/${vehicle.id}/reservation`)}>
                예약하기
            </button>
        </main>
    );
}

// 다른 파일에서 VehicleDetailPage를 import해서 쓸 수 있게 내보냄
export default VehicleDetailPage;

// 1. useParams()
// → 주소에서 vehicleId를 꺼낸다.
//
// 2. useEffect()
// → 페이지가 열리면 차량 상세 API를 호출한다.
//
// 3. axiosInstance.get(`/api/vehicles/${vehicleId}`)
// → 백엔드에 특정 차량 조회 요청을 보낸다.
//
// 4. setVehicle(response.data)
// → 백엔드에서 받은 차량 정보를 화면 state에 저장한다.
//
// 5. if (!vehicle)
// → 아직 데이터가 없을 때 로딩 화면을 보여준다.
//
// 6. return (...)
// → 차량 상세 정보를 화면에 출력한다.