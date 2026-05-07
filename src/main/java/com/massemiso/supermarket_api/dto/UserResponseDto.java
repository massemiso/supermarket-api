package com.massemiso.supermarket_api.dto;

import com.massemiso.supermarket_api.entity.RoleEnum;
import java.util.Set;

public record UserResponseDto (
    Long id,
    String username,
    String email,
    Boolean isAccountExpired,
    Boolean isAccountLocked,
    Boolean isCredentialsExpired,
    Set<RoleEnum> roles
){ }
