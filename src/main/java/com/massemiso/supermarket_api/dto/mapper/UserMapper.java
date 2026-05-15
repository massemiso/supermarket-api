package com.massemiso.supermarket_api.dto.mapper;

import com.massemiso.supermarket_api.dto.AuthRegisterRequestDto;
import com.massemiso.supermarket_api.dto.AuthResponseDto;
import com.massemiso.supermarket_api.dto.UserRequestDto;
import com.massemiso.supermarket_api.dto.UserResponseDto;
import com.massemiso.supermarket_api.entity.RoleEntity;
import com.massemiso.supermarket_api.entity.RoleEnum;
import com.massemiso.supermarket_api.entity.UserEntity;
import java.util.Set;
import java.util.stream.Collectors;
import javax.management.relation.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "password", source = "passwordEncoded")
  @Mapping(target = "accountNonExpired", ignore = true)
  @Mapping(target = "accountNonLocked", ignore = true)
  @Mapping(target = "credentialsNonExpired", ignore = true)
  public UserEntity toEntity(
      Set<RoleEntity> roles, UserRequestDto requestDto, String passwordEncoded);

  @Mapping(target = "password", source = "passwordEncoded")
  @Mapping(target = "accountNonExpired", ignore = true)
  @Mapping(target = "accountNonLocked", ignore = true)
  @Mapping(target = "credentialsNonExpired", ignore = true)
  public UserEntity toEntity
      (Set<RoleEntity> roles, AuthRegisterRequestDto requestDto, String passwordEncoded);

  public UserResponseDto toDto(UserEntity entity);

  default RoleEnum mapRoleEntityToEnum(RoleEntity roleEntity){
    return roleEntity.getRoleEnum();
  }
}
