// 사용자가 이메일 입력
// → 사용자가 비밀번호 입력
// → 로그인 버튼 클릭
// → handleLogin 실행
// → POST /api/auth/login 요청
// → 백엔드가 accessToken 반환
// → localStorage에 토큰 저장
// → /vehicles 화면으로 이동

// 1. useState
// → 입력값이나 화면 데이터를 기억한다.
//
// 2. onChange
// → input에 입력한 값을 state에 저장한다.
//
// 3. onSubmit
// → 버튼 눌렀을 때 로그인 함수를 실행한다.
//
// 4. axios
// → 백엔드 API를 호출한다.
//
// 5. localStorage + navigate
// → 토큰 저장하고 다음 화면으로 이동한다.

import { useState } from "react"; // 입력값을 기억
import { useNavigate } from "react-router-dom"; // 다른 주소로 이동
import axiosInstance from "../api/axiosInstance"; // 백엔드 API 호출

function LoginPage() {
    const navigate = useNavigate();

    const [email, setEmail] = useState(""); // email : 현재 입력된 이메일 , setEmail : 이메일을 변경하는 함수 , "" : 처음에는 빈 문자열
    const [password, setPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    const handleLogin = async (event) => {
        event.preventDefault();
        setErrorMessage("");

        try {
            const response = await axiosInstance.post("/api/auth/login", {
                email: email,
                password: password,
            });

            const accessToken = response.data.accessToken;

            localStorage.setItem("accessToken", accessToken); // 브라우저에 토큰을 저장하는 곳

            navigate("/vehicles"); // 로그인 성공하면 다른 화면으로 이동
        } catch (error) {
            console.error(error);
            setErrorMessage("로그인에 실패했습니다.");
        }
    };

    // 사용자가 input에 입력
    //           ↓
    //   onChange 실행
    //           ↓
    //   event.target.value는 "abc@test.com"
    //           ↓
    //   setEmail("abc@test.com") 실행
    //           ↓
    //   email 상태가 "abc@test.com"으로 변경
    return (
        <main>
            <h1>로그인</h1>

            <form onSubmit={handleLogin}>
                <div>
                    <label htmlFor="email">이메일</label>
                    <input
                        id="email"
                        type="email"
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="password">비밀번호</label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        required
                    />
                </div>

                {errorMessage && <p>{errorMessage}</p>}

                <button type="submit">로그인</button>
            </form>
        </main>
    );
}

export default LoginPage;