import { useEffect, useState } from "react";
// useState: 차량 목록 같은 화면 데이터를 기억하는 기능
// useEffect: 페이지가 처음 열릴 때 특정 코드를 실행하는 기능

import axiosInstance from "../api/axiosInstance";
// 우리가 만든 백엔드 API 호출 도구
// localStorage에 accessToken이 있으면 Authorization 헤더를 자동으로 붙여줌

function VehicleListPage() {
    // vehicles: 백엔드에서 받아온 차량 목록을 저장하는 state
    // setVehicles: vehicles 값을 바꾸는 함수
    // []: 처음에는 차량 목록이 없으니까 빈 배열로 시작
    const [vehicles, setVehicles] = useState([]);

    // errorMessage: 차량 목록 조회 실패 시 화면에 보여줄 에러 메시지
    // 처음에는 에러가 없으니까 빈 문자열
    const [errorMessage, setErrorMessage] = useState("");

    // useEffect는 페이지가 처음 화면에 나타났을 때 실행됨
    // 여기서는 /vehicles 페이지에 들어오자마자 차량 목록 API를 호출하기 위해 사용
    useEffect(() => {
        // 백엔드에서 차량 목록을 가져오는 함수
        const fetchVehicles = async () => {
            try {
                // GET /api/vehicles 요청
                // 실제 요청 주소는 axiosInstance의 baseURL 때문에
                // http://localhost:8080/api/vehicles 가 됨
                const response = await axiosInstance.get("/api/vehicles");

                // response.data에는 백엔드가 보내준 차량 목록 배열이 들어있음
                // 그 차량 목록을 vehicles state에 저장
                setVehicles(response.data);
            } catch (error) {
                // API 호출 실패 시 콘솔에 에러 출력
                // 서버가 꺼져 있거나, 토큰이 없거나, API 주소가 틀렸을 때 여기로 올 수 있음
                console.error(error);

                // 화면에 보여줄 에러 메시지 저장
                setErrorMessage("차량 목록을 불러오지 못했습니다.");
            }
        };

        // 위에서 만든 fetchVehicles 함수를 실제로 실행
        fetchVehicles();
    }, []);
    // []는 이 useEffect를 페이지 처음 열릴 때 한 번만 실행하겠다는 의미

    return (
        <main>
            <h1>차량 목록</h1>

            {/* errorMessage가 있으면 화면에 에러 메시지를 보여줌 */}
            {errorMessage && <p>{errorMessage}</p>}

            {/* 차량 목록이 비어 있고, 에러도 없으면 등록된 차량이 없다고 보여줌 */}
            {vehicles.length === 0 && !errorMessage && (
                <p>등록된 차량이 없습니다.</p>
            )}

            <div>
                {/* vehicles 배열 안에 있는 차량들을 하나씩 꺼내서 화면에 보여줌 */}
                {vehicles.map((vehicle) => (
                    <div key={vehicle.id}>
                        <h2>
                            {vehicle.manufacturer} {vehicle.modelName}
                        </h2>

                        <p>차량 번호: {vehicle.vehicleNumber}</p>
                        <p>대여 타입: {vehicle.rentalType}</p>
                        <p>연료 타입: {vehicle.fuelType}</p>
                        <p>상태: {vehicle.status}</p>
                        <p>시간당 요금: {vehicle.hourlyRate}원</p>
                        <p>일일 요금: {vehicle.dailyRate}원</p>
                    </div>
                ))}
            </div>
        </main>
    );
}

// 다른 파일에서 VehicleListPage를 import해서 사용할 수 있게 내보냄
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
