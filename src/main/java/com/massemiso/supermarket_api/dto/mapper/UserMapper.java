package com.massemiso.supermarket_api.dto.mapper;

import com.massemiso.supermarket_api.dto.UserRequestDto;
import com.massemiso.supermarket_api.dto.UserResponseDto;
import com.massemiso.supermarket_api.entity.RoleEntity;
import com.massemiso.supermarket_api.entity.UserEntity;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  public UserEntity toEntity(Set<RoleEntity> roles, UserRequestDto requestDto, String passwordEncoded){
    return UserEntity.builder()
        .username(requestDto.username())
        .password(passwordEncoded)
        .email(requestDto.email())
        .isAccountExpired(false)
        .isAccountLocked(false)
        .isCredentialsExpired(false)
        .roles(roles)
        .build();
  }

  public UserResponseDto toDto(UserEntity entity){
    return new UserResponseDto(
        entity.getId(),
        entity.getUsername(),
        entity.getEmail(),
        entity.getIsAccountExpired(),
        entity.getIsAccountLocked(),
        entity.getIsCredentialsExpired(),
        entity.getRoles().stream().map(RoleEntity::getRoleEnum).collect(Collectors.toSet())
    );
  }
}
