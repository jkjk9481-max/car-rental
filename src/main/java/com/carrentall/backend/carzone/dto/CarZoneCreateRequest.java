package com.carrentall.backend.carzone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CarZoneCreateRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String address;
}
