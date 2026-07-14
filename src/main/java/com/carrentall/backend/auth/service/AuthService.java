package com.carrentall.backend.auth.service;

import com.carrentall.backend.auth.dto.LoginRequest;
import com.carrentall.backend.auth.dto.LoginResponse;
import com.carrentall.backend.auth.dto.SignupRequest;
import com.carrentall.backend.auth.exception.InvalidCredentialsException;
import com.carrentall.backend.auth.jwt.JwtTokenProvider;
import com.carrentall.backend.user.entity.User;
import com.carrentall.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthService(UserRepository userRepository ,  PasswordEncoder passwordEncoder ,  JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void signup(SignupRequest request){
        boolean exists = userRepository.existsByEmail(request.getEmail());

        if(exists){
            throw new RuntimeException("이미 존재하는 이메일입니다");
        }

        String encodePassword = passwordEncoder.encode(request.getPassword());

        User user = new User(request.getEmail(), encodePassword , request.getName(), request.getPhoneNumber());

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request){
         User user = userRepository.findByEmail(request.getEmail())
                 .orElseThrow(() -> new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다"));

         if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
             throw new InvalidCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다");
         }

         // user 이메일 , 권한으로 토큰을 생성
        // JWT는 비밀문서가아니라 서명된 토큰에 가깝습니다
         String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole());
         // DB에서 찾은 사용자의 이메일을 가져옴 , DB에 저장된 사용자의 권한을 가져옴
        // 이메일과 권한정보를 넣어 JWT를 생성함

        LoginResponse response = new LoginResponse(token);
        // 문자열 token -> LoginResponse 상자에 넣기 -> response 변수에 보관

        return response;
        // Request와 Response는 데이터를 담는 상자이고 , Spring이 상자에 넣고 꺼내면서 변환해 준다고 이해
    }


}
