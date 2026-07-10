package com.carrentall.backend.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호 암호화하는 역할
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // POST 요청이 막히지 않게 하겠다 잠시동안
                .authorizeHttpRequests(auth -> auth // 요청 주소별 접근 권한을 정하겠다
                        .requestMatchers("/api/auth/signup" , "/api/auth/login").permitAll() // 이 주소로는 아무나 접근가능
                        .anyRequest()
                        .authenticated()); // 위에서 허용한 주소말고 나머지 모든 요청은 로그인한 사용자만 가능

        return http.build(); // 위에서 만든 보안 설정을 완성해서 Spring Security에 넘긴다
    }


}
