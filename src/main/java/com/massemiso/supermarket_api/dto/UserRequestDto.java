package com.massemiso.supermarket_api.dto;

import com.massemiso.supermarket_api.entity.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record UserRequestDto (
    @NotBlank String username,
    @NotBlank String password,
    @NotBlank @Email String email,
    @NotEmpty Set<RoleEnum> roles
    ){ }
