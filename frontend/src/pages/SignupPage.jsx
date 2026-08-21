import { useState } from "react";
// useState: input에 입력한 값을 기억하기 위해 사용

import { useNavigate } from "react-router-dom";
// useNavigate: 회원가입 성공 후 로그인 페이지로 이동하기 위해 사용

import axiosInstance from "../api/axiosInstance";
// axiosInstance: 백엔드 API를 호출하기 위해 사용

function SignupPage() {
    // 페이지 이동 함수 준비
    const navigate = useNavigate();

    // email: 사용자가 입력한 이메일
    // setEmail: email 값을 바꾸는 함수
    const [email, setEmail] = useState("");

    // password: 사용자가 입력한 비밀번호
    const [password, setPassword] = useState("");

    // name: 사용자가 입력한 이름
    const [name, setName] = useState("");

    // phoneNumber: 사용자가 입력한 전화번호
    const [phoneNumber, setPhoneNumber] = useState("");

    // errorMessage: 회원가입 실패 시 화면에 보여줄 메시지
    const [errorMessage, setErrorMessage] = useState("");

    // successMessage: 회원가입 성공 시 화면에 보여줄 메시지
    const [successMessage, setSuccessMessage] = useState("");

    // form이 제출될 때 실행되는 함수
    const handleSignup = async (event) => {
        // form 제출 시 브라우저가 새로고침되는 기본 동작을 막음
        event.preventDefault();

        // 회원가입을 다시 시도할 때 기존 메시지 초기화
        setErrorMessage("");
        setSuccessMessage("");

        try {
            // 백엔드 회원가입 API 호출
            // 실제 요청 주소: POST http://localhost:8080/api/auth/signup
            await axiosInstance.post("/api/auth/signup", {
                email: email,
                password: password,
                name: name,
                phoneNumber: phoneNumber,
            });

            // 회원가입 성공 메시지 저장
            setSuccessMessage("회원가입이 완료되었습니다.");

            // 회원가입 성공 후 로그인 페이지로 이동
            // 0.5초 뒤 이동하면 성공 메시지를 잠깐 볼 수 있음
            setTimeout(() => {
                navigate("/login");
            }, 500);
        } catch (error) {
            console.error(error);

            // 백엔드가 응답을 준 경우
            if (error.response) {
                console.log("상태 코드:", error.response.status);
                console.log("응답 데이터:", error.response.data);

                setErrorMessage(
                    typeof error.response.data === "string"
                        ? error.response.data
                        : "회원가입 요청이 실패했습니다."
                );
                return;
            }

            // 백엔드까지 요청이 못 간 경우
            if (error.request) {
                setErrorMessage("백엔드 서버에 연결할 수 없습니다.");
                return;
            }

            // 그 외 프론트 코드 자체 문제
            setErrorMessage("알 수 없는 오류가 발생했습니다.");
        }
    };

    return (
        <main>
            <h1>회원가입</h1>

            {/* form 제출 시 handleSignup 실행 */}
            <form onSubmit={handleSignup}>
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

                <div>
                    <label htmlFor="name">이름</label>
                    <input
                        id="name"
                        type="text"
                        value={name}
                        onChange={(event) => setName(event.target.value)}
                        required
                    />
                </div>

                <div>
                    <label htmlFor="phoneNumber">전화번호</label>
                    <input
                        id="phoneNumber"
                        type="text"
                        value={phoneNumber}
                        onChange={(event) => setPhoneNumber(event.target.value)}
                        required
                    />
                </div>

                {/* 에러 메시지가 있으면 화면에 출력 */}
                {errorMessage && <p>{errorMessage}</p>}

                {/* 성공 메시지가 있으면 화면에 출력 */}
                {successMessage && <p>{successMessage}</p>}

                <button type="submit">회원가입</button>
            </form>

            <button type="button" onClick={() => navigate("/login")}>
                로그인하러 가기
            </button>
        </main>
    );
}

export default SignupPage;

// 전체 흐름
// 이메일 입력
// 비밀번호 입력
// 이름 입력
// 전화번호 입력
// → 회원가입 버튼 클릭
// → handleSignup 실행
// → POST /api/auth/signup 요청
// → 성공하면 /login으로 이동


// 기억해야할것

// 1. 회원가입은 useState가 4개 필요하다.
//    email, password, name, phoneNumber
//
// 2. 버튼을 누르면 handleSignup이 실행된다.
//
// 3. axiosInstance.post("/api/auth/signup", {...})로 백엔드에 보낸다.
//
// 4. 성공하면 토큰 저장이 아니라 /login으로 이동한다.
//
// 5. 실패하면 errorMessage를 화면에 보여준다.