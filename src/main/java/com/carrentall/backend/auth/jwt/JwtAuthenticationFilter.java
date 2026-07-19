package com.carrentall.backend.auth.jwt;


import com.carrentall.backend.user.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            // 1.Authorization 헤더읽기
            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                // 2. 헤더가 없는지 , Bearer로 시작하는지
                filterChain.doFilter(request, response);
                return;
                // -> 다음 필터로 넘겼으니 , 현재 doFilterInternal() 메서드는 여기서 끝내라
            }
            String token = header.substring(7);

            boolean validToken = jwtTokenProvider.validateToken(token);
            if (validToken) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                Role role = jwtTokenProvider.getRoleFromToken(token);

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.name());
                // role이 Role.User인 경우 -> role.name()은 "USER" -> "ROLE_"을 붙이면 "ROLE_USER" -> SimpleGrantedAuthority가 Spring Security용 권한으로 감싸줌

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null,  Collections.singletonList(authority));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        filterChain.doFilter(request, response);
        }
}
