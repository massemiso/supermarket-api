package com.massemiso.supermarket_api.mapper;

import com.massemiso.supermarket_api.dto.AuthRegisterRequestDto;
import com.massemiso.supermarket_api.dto.UserRequestDto;
import com.massemiso.supermarket_api.dto.UserResponseDto;
import com.massemiso.supermarket_api.entity.RoleEntity;
import com.massemiso.supermarket_api.entity.RoleEnum;
import com.massemiso.supermarket_api.entity.UserEntity;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "roles", source = "roles")
  @Mapping(target = "password", source = "passwordEncoded")
  @Mapping(target = "accountNonExpired", expression = "java(true)")
  @Mapping(target = "accountNonLocked", expression = "java(true)")
  @Mapping(target = "credentialsNonExpired", expression = "java(true)")
  public UserEntity toEntity(
      Set<RoleEntity> roles, UserRequestDto requestDto, String passwordEncoded);

  @Mapping(target = "roles", source = "roles")
  @Mapping(target = "password", source = "passwordEncoded")
  @Mapping(target = "accountNonExpired", expression = "java(true)")
  @Mapping(target = "accountNonLocked", expression = "java(true)")
  @Mapping(target = "credentialsNonExpired", expression = "java(true)")
  public UserEntity toEntity
      (Set<RoleEntity> roles, AuthRegisterRequestDto requestDto, String passwordEncoded);

  public UserResponseDto toDto(UserEntity entity);

  default RoleEnum mapRoleEntityToEnum(RoleEntity roleEntity){
    return roleEntity.getRoleEnum();
  }
}
