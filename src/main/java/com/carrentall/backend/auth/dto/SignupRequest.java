package com.carrentall.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank // 비어있거나 공백만 있는 값을 막기위해 필요
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(min = 10)
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;


}
