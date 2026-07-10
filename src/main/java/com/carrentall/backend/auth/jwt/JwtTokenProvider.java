package com.carrentall.backend.auth.jwt;

import com.carrentall.backend.user.entity.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // JWT 서명과 검증에 사용할 비밀키
    private final SecretKey key;

    // 토큰이 유효한 시간 (밀리초 단위)
    private final long expirationMs;

    public JwtTokenProvider(
            // application.properties의 jwt.secret 값을 환경 변수에서 읽는다.
            @Value("${jwt.secret}") String secret,
            // application.properties의 jwt.expiration-ms 값을 읽는다.
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        // Base64 문자열로 보관한 비밀키를 실제 바이트 배열로 되돌린다.
        byte[] keyBytes = Decoders.BASE64.decode(secret);

        // 바이트 배열을 HS256 서명에 사용할 수 있는 SecretKey로 변환한다.
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    // 로그인에 성공한 사용자의 이메일과 역할을 담은 JWT 문자열을 만든다.
    public String createToken(String email, Role role) {
        // 토큰을 발급하는 현재 시각
        Date now = new Date();

        // 현재 시각에 설정한 유효 시간을 더해 만료 시각을 만든다.
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                // 토큰의 주인(subject): 이후 어떤 사용자인지 식별할 때 사용한다.
                .subject(email)
                // 사용자 역할을 토큰의 추가 정보(claim)로 저장한다.
                .claim("role", role.name())
                // 토큰 발급 시각
                .issuedAt(now)
                // 토큰 만료 시각
                .expiration(expiration)
                // 서버의 비밀키로 토큰에 서명한다.
                .signWith(key)
                // 완성된 JWT를 클라이언트에 전달할 문자열로 만든다.
                .compact();
    }
}
