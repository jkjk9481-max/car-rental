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

    // 카존 목록
    const [carZones , setCarZones] = useState([]);

    // 카존 조회 오류
    const [carZoneError , setCarZoneError] = useState("");

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

    // carZones는 모든 장소의 목록, selectedCarZoneId는 현재 선택한 장소의 ID다.
    // ""는 전체 장소를 뜻한다. select에서 읽는 ID는 "1"처럼 문자열이다.
    const [selectedCarZoneId, setSelectedCarZoneId] = useState("");

    // onChange가 전달한 event에서 새 선택값을 꺼내 React state에 저장한다.
    // state가 바뀌면 React가 화면을 다시 계산하고 select의 선택 표시도 바뀐다.
    const handleCarZoneChange = (event) => {
        setSelectedCarZoneId(event.target.value);
    };

    // useEffect:
    // - 처음 화면에 들어왔을 때와 선택한 카존 ID가 바뀔 때 실행됨
    // - 차량 목록은 사용자가 버튼을 누르지 않아도 바로 보여야 하니까 여기서 API 호출
    useEffect(() => {
        // 장소를 바꾸거나 페이지를 떠나면 이전 요청의 결과는 사용하지 않는다.
        // 방학역 요청이 늦게 끝나도 나중에 선택한 노원역 목록을 덮어쓰지 않게 한다.
        let ignore = false;
        // 백엔드에서 차량 목록을 가져오는 함수
        const fetchVehicles = async () => {
            try {
                // 기존 에러 메시지 초기화
                setErrorMessage("");
                setLoading(true);
                // 새 장소를 조회하는 동안 이전 장소 차량이 남아 있지 않도록 비운다.
                setVehicles([]);

                // GET /api/vehicles 요청
                //
                // 실제 요청:
                // GET http://localhost:8080/api/vehicles
                //
                // 로그인 후 localStorage에 accessToken이 있으면
                // axiosInstance가 Authorization 헤더를 자동으로 붙여줌
                // 빈 문자열이면 전체 조회, ID가 있으면 해당 장소의 차량 조회다.
                // 백틱 문자열의 ${...} 자리에 실제 선택한 ID가 들어간다.
                const url = selectedCarZoneId === ""
                    ? "/api/vehicles"
                    : `/api/vehicles/carzone/${selectedCarZoneId}`;
                const response = await axiosInstance.get(url);
                if (ignore) return;

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
                if (ignore) return;
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
                // 성공하거나 실패해도 차량 요청은 끝났으므로 로딩을 종료한다.
                // false로 바꾸면 React가 화면을 다시 계산해 아래의 목록/오류 화면을 표시한다.
                // 이전 요청이 끝났다는 이유로 현재 요청의 로딩을 종료하면 안 된다.
                if (!ignore) setLoading(false);
            }
        };

        // 위에서 만든 차량 목록 조회 함수를 실제로 실행
        fetchVehicles();
        // effect가 다음 선택값으로 다시 실행되기 전과 페이지를 떠날 때 실행된다.
        // 요청 자체를 취소하는 것은 아니며, 늦게 도착한 결과의 화면 반영을 막는다.
        return () => {
            ignore = true;
        };
    }, [selectedCarZoneId]);
    // 배열에 넣은 selectedCarZoneId가 바뀌면 차량 목록을 다시 요청한다.

    // 차량 목록과 별도로, 장소 선택에 사용할 카존 목록을 불러온다.
    useEffect(() => {
        // 함수 정의: 여기서는 실행할 작업을 준비한다. 실제 호출은 아래에서 한다.
        // async 함수 안에서는 await로 API 응답을 기다릴 수 있다.
        const fetchCarZones = async () => {
            try {
                // 카존 전용 오류를 초기화한다. 차량 조회 오류에는 영향을 주지 않는다.
                setCarZoneError("");

                // GET으로 목록을 요청하고, 응답이 도착하면 다음 줄로 진행한다.
                const response = await axiosInstance.get("/api/carzones");

                // response.data는 서버가 보내 준 카존 배열이다.
                // React state에 저장하는 것이며, 서버 DB에 저장하는 작업은 아니다.
                // 저장된 carZones를 JSX에서 사용해야 실제 장소 목록이 화면에 나타난다.
                setCarZones(response.data);
            } catch (error) {
                console.error(error);
                // 실패 메시지도 카존 전용 state에 저장한다. 화면 표시는 JSX에서 연결한다.
                setCarZoneError("대여 장소를 불러오지 못했습니다.");
            }
        };

        // 함수 호출: 정의한 작업을 여기서 실행해야 API 요청이 전송된다.
        fetchCarZones();
    }, []);
    // []는 state 변경으로 다시 렌더링될 때 이 effect를 반복 실행하지 않도록 한다.
    // 컴포넌트가 새로 화면에 붙으면 실행된다. 개발 모드의 StrictMode에서는
    // 문제 확인을 위해 처음에 effect가 추가 실행될 수 있다.

    return (
        <main>
            <h1>차량 목록</h1>

            <div>
                {/* htmlFor와 select의 id를 같게 지정해 라벨과 선택 상자를 연결한다. */}
                <label htmlFor="car-zone">대여 장소</label>
                {/* value는 현재 선택값, onChange는 선택을 바꿀 때 실행할 함수다.
                    함수 이름만 전달하면 사용자가 선택을 바꿀 때 React가 호출한다. */}
                <select
                    id="car-zone"
                    value={selectedCarZoneId}
                    onChange={handleCarZoneChange}
                >
                    <option value="">전체 장소</option>
                    {/* map은 카존 하나마다 option 하나를 만든다.
                        화살표 뒤의 (...)는 그 안의 JSX를 바로 반환한다. */}
                    {carZones.map((carZone) => (
                        // key는 React의 항목 구분용, value는 선택했을 때 읽을 값이다.
                        // 중괄호 안의 carZone.name은 해당 카존의 실제 이름으로 표시된다.
                        <option key={carZone.id} value={carZone.id}>
                            {carZone.name}
                        </option>
                    ))}
                </select>
                {/* 오류 문자열이 있을 때만 메시지를 표시한다. */}
                {carZoneError && <p role="alert">{carZoneError}</p>}
            </div>

            {/* 로딩 중에도 장소 선택 상자는 유지해 다른 장소를 선택할 수 있다. */}
            {loading && <p role="status">차량 목록을 불러오는 중입니다...</p>}
            {/* 에러 메시지가 있으면 화면에 출력 */}
            {errorMessage && <p>{errorMessage}</p>}

            {/* 차량 목록이 비어 있고, 에러도 없으면 빈 목록 메시지 출력 */}
            {!loading && vehicles.length === 0 && !errorMessage && (
                <p>{selectedCarZoneId === ""
                    ? "등록된 차량이 없습니다."
                    : "선택한 장소에 등록된 차량이 없습니다."}</p>
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
                            <p>대여 장소: {vehicle.carZoneName}</p>
                            <p>주소: {vehicle.carZoneAddress}</p>
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
