package com.massemiso.supermarket_api.dto;

import com.massemiso.supermarket_api.entity.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

public record UserResponseDto (
    @Schema(example = "1")
    Long id,

    @Schema(example = "awesker")
    String username,

    @Schema(example = "awesker@umbrella.com")
    String email,

    @Schema(example = "true")
    Boolean accountNonExpired,

    @Schema(example = "true")
    Boolean accountNonLocked,

    @Schema(example = "true")
    Boolean credentialsNonExpired,

    Set<RoleEnum> roles
){ }
