import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import VehicleListPage from "./pages/VehicleListPage";
import VehicleDetailPage from "./pages/VehicleDetailPage";
import ReservationCreatePage from "./pages/ReservationCreatePage";
import MyReservationsPage from "./pages/MyReservationsPage";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/vehicles" element={<VehicleListPage />} />
                <Route path="/vehicles/:vehicleId" element={<VehicleDetailPage />} />
                <Route path="/vehicles/:vehicleId/reservation" element={<ReservationCreatePage />} />

                {/* 내 예약 목록 페이지 */}
                <Route path="/reservations/my" element={<MyReservationsPage />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;

// 프론트 요청 구조
// 1. 주소로 들어간다.
// 2. useParams로 주소 값을 꺼낸다.
// 3. useEffect로 페이지가 열릴 때 API를 호출한다.
// 4. response.data를 state에 저장한다.
// 5. state 값을 화면에 출력한다.

// 백엔드 요청 구조
// /vehicles/1 접속
// → vehicleId = 1 꺼냄
// → GET /api/vehicles/1 요청
// → 차량 1번 정보 받음
// → vehicle에 저장
// → 화면에 출력