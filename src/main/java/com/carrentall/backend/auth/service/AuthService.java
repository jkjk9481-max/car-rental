package com.carrentall.backend.auth.service;

import com.carrentall.backend.auth.dto.SignupRequest;
import com.carrentall.backend.user.entity.User;
import com.carrentall.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository ,  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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


}
