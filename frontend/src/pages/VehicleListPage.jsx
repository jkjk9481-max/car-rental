import { useEffect, useState } from "react";
// useState:
// - 화면에서 기억해야 하는 값을 저장할 때 사용
// - 여기서는 vehicles, errorMessage, loading 값을 기억함
//
// useEffect:
// - 페이지가 처음 열렸을 때 특정 코드를 실행할 때 사용
// - 여기서는 차량 목록 API를 호출할 때 사용

import { Link } from "react-router-dom";
// Link:
// - 다른 페이지로 이동할 수 있는 링크를 만들 때 사용
// - 예: 차량 목록에서 "상세 보기"를 누르면 /vehicles/1 로 이동

import axiosInstance from "../api/axiosInstance";
// axiosInstance:
// - 백엔드 API 호출 도구
// - baseURL이 http://localhost:8080이면
//   axiosInstance.get("/api/vehicles")
//   → 실제 요청은 http://localhost:8080/api/vehicles 로 나감
//
// - localStorage에 accessToken이 있으면
//   Authorization: Bearer 토큰값
//   헤더를 자동으로 붙여줌

function VehicleListPage() {
    // vehicles:
    // - 백엔드에서 받아온 차량 목록을 저장하는 state
    //
    // setVehicles:
    // - vehicles 값을 바꾸는 함수
    //
    // []:
    // - 처음에는 차량 목록이 없으니까 빈 배열로 시작
    const [vehicles, setVehicles] = useState([]);

    // errorMessage:
    // - 차량 목록 조회 실패 시 화면에 보여줄 에러 메시지
    //
    // 처음에는 에러가 없으니까 빈 문자열
    const [errorMessage, setErrorMessage] = useState("");

    // loading:
    // - 차량 목록을 불러오는 중인지 표시하는 값
    //
    // true:
    // - 지금 데이터를 불러오는 중
    //
    // false:
    // - 불러오기가 끝남
    const [loading, setLoading] = useState(true);

    // useEffect:
    // - /vehicles 페이지가 처음 열렸을 때 실행됨
    // - 차량 목록은 사용자가 버튼을 누르지 않아도 바로 보여야 하니까 여기서 API 호출
    useEffect(() => {
        // 백엔드에서 차량 목록을 가져오는 함수
        const fetchVehicles = async () => {
            try {
                // 기존 에러 메시지 초기화
                setErrorMessage("");

                // GET /api/vehicles 요청
                //
                // 실제 요청:
                // GET http://localhost:8080/api/vehicles
                //
                // 로그인 후 localStorage에 accessToken이 있으면
                // axiosInstance가 Authorization 헤더를 자동으로 붙여줌
                const response = await axiosInstance.get("/api/vehicles");

                // response.data:
                // - 백엔드가 응답으로 보내준 차량 목록
                //
                // 예:
                // [
                //   {
                //     id: 1,
                //     manufacturer: "Hyundai",
                //     modelName: "Avante",
                //     vehicleNumber: "12가3456",
                //     rentalType: "CAR_SHARING",
                //     fuelType: "GASOLINE",
                //     status: "AVAILABLE",
                //     hourlyRate: 8000,
                //     dailyRate: 70000
                //   }
                // ]
                //
                // 이 데이터를 vehicles state에 저장
                setVehicles(response.data);
            } catch (error) {
                // API 호출 실패 시 개발자 도구 Console에 에러 출력
                //
                // 실패 원인 예:
                // - 백엔드 서버 꺼짐
                // - 토큰 없음
                // - API 주소 틀림
                // - CORS 문제
                // - 권한 문제
                console.error(error);

                // 화면에 보여줄 에러 메시지 저장
                setErrorMessage("차량 목록을 불러오지 못했습니다.");
            } finally {
                // 성공하든 실패하든 로딩은 끝났으니까 false로 변경
                setLoading(false);
            }
        };

        // 위에서 만든 차량 목록 조회 함수를 실제로 실행
        fetchVehicles();
    }, []);
    // [] 의미:
    // - 이 useEffect는 페이지가 처음 열릴 때 한 번만 실행됨

    // 차량 목록을 불러오는 중이면 로딩 메시지 표시
    if (loading) {
        return (
            <main>
                <h1>차량 목록</h1>
                <p>차량 목록을 불러오는 중입니다...</p>
            </main>
        );
    }

    return (
        <main>
            <h1>차량 목록</h1>

            {/* 에러 메시지가 있으면 화면에 출력 */}
            {errorMessage && <p>{errorMessage}</p>}

            {/* 차량 목록이 비어 있고, 에러도 없으면 빈 목록 메시지 출력 */}
            {vehicles.length === 0 && !errorMessage && (
                <p>등록된 차량이 없습니다.</p>
            )}

            <div>
                {/* vehicles 배열 안에 있는 차량들을 하나씩 꺼내서 화면에 출력 */}
                {vehicles.map((vehicle) => {
                    // 백엔드 응답 필드명이 id일 수도 있고 vehicleId일 수도 있어서 둘 다 대응
                    // 네 백엔드 응답이 확실히 id라면 vehicle.id만 써도 됨
                    const vehicleId = vehicle.id ?? vehicle.vehicleId;

                    return (
                        // key:
                        // - React가 차량 목록의 각 항목을 구분하기 위한 고유값
                        // - 보통 DB id를 사용함
                        <div key={vehicleId}>
                            <h2>
                                {vehicle.manufacturer} {vehicle.modelName}
                            </h2>

                            <p>차량 번호: {vehicle.vehicleNumber}</p>
                            <p>대여 타입: {vehicle.rentalType}</p>
                            <p>연료 타입: {vehicle.fuelType}</p>
                            <p>상태: {vehicle.status}</p>
                            <p>시간당 요금: {vehicle.hourlyRate}원</p>
                            <p>일일 요금: {vehicle.dailyRate}원</p>

                            {/* 상세 보기 클릭 시 /vehicles/{vehicleId} 로 이동 */}
                            {/* 예: vehicleId가 1이면 /vehicles/1 로 이동 */}
                            <Link to={`/vehicles/${vehicleId}`}>
                                상세 보기
                            </Link>
                        </div>
                    );
                })}
            </div>
        </main>
    );
}

// 다른 파일에서 VehicleListPage를 import해서 쓸 수 있게 내보냄
export default VehicleListPage;


// 전체 흐름 -->>
// /vehicles 페이지 접속
// → VehicleListPage 실행
// → useEffect 실행
// → GET /api/vehicles 요청
// → 백엔드에서 차량 목록 받음
// → vehicles state에 저장
// → vehicles.map()으로 화면에 출력




// 1. useEffect
// → 페이지가 처음 열릴 때 API 호출할 때 사용
//
// 2. useState([])
// → 목록 데이터를 저장할 때 사용
//
// 3. axiosInstance.get("/api/vehicles")
// → 백엔드 차량 목록 API 호출
//
// 4. setVehicles(response.data)
// → 백엔드 응답을 화면 데이터로 저장
//
// 5. vehicles.map()
// → 배열 데이터를 화면에 반복 출력
