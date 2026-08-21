// 이 파일의 뜻은 백엔드 기본 주소는 http://localhost:8080이고 ,
// localStorage에 accessToken이 있으면
// 모든 요청에 Authorization : Bearer 토큰 자동 추가


import axios from "axios";

const axiosInstance = axios.create({
    baseURL: "http://localhost:8080",
});

axiosInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem("accessToken");

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});

export default axiosInstance;