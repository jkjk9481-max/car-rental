package com.carrentall.backend.config;


import com.carrentall.backend.auth.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호 암호화하는 역할
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 우리 서버에 들어오는 요청들을 어떻게 검사하는지 정하는 곳
        http
                .csrf(csrf -> csrf.disable()) // POST 요청이 막히지 않게 하겠다 잠시동안
                .authorizeHttpRequests(auth -> auth // 요청 주소별 접근 권한을 정하겠다
                        .requestMatchers("/api/auth/signup" , "/api/auth/login").permitAll() // 이 주소로는 아무나 접근가능
                        .anyRequest()
                        .authenticated()); // 위에서 허용한 주소말고 나머지 모든 요청은 로그인한 사용자만 가능

        http.addFilterBefore(jwtAuthenticationFilter , UsernamePasswordAuthenticationFilter.class);
        // HTTP 요청 -> JWTAuthenticationFilter 실행 -> UsernamePasswordAuthenticationFilter 실행 -> 나머지 보안 처리
        // 어떤 필터보다 먼저 실행할지 정하는 메서드
        // 사용자 요청 도착
        //→ JwtAuthenticationFilter 실행
        //→ JWT 토큰 확인
        //→ 정상 토큰이면 SecurityContext에 사용자 저장
        //→ UsernamePasswordAuthenticationFilter 쪽으로 이동
        //→ 나머지 보안 처리
        //→ Controller 도착

        return http.build(); // 위에서 만든 보안 설정을 완성해서 Spring Security에 넘긴다


    }





}
