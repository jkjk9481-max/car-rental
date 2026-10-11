// 이 파일의 뜻은 백엔드 기본 주소를 .env의 VITE_API_BASE_URL에서 읽어오고 ,
// localStorage에 accessToken이 있으면
// 모든 요청에 Authorization : Bearer 토큰 자동 추가


import axios from "axios";

const axiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
});

axiosInstance.interceptors.request.use((config) => {
    const token = localStorage.getItem("accessToken");

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});

export default axiosInstance;