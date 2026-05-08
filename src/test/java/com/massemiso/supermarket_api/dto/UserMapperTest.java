package com.massemiso.supermarket_api.dto;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import com.massemiso.supermarket_api.dto.mapper.UserMapper;
import com.massemiso.supermarket_api.entity.RoleEntity;
import com.massemiso.supermarket_api.entity.UserEntity;
import com.massemiso.supermarket_api.util.TestDataFactory;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Set;

class UserMapperTest {

  @Test
  void toEntity_GivenValidUserRequestDto_ShouldReturnUserEntity() {
    //given
    UserMapper userMapper = new UserMapper();
    Set<RoleEntity> roles = Set.of(TestDataFactory.createDefaultRoleEntity());
    UserRequestDto requestDto = TestDataFactory.createDefaultUserRequestDto();
    String passwordEncoded = new BCryptPasswordEncoder().encode(requestDto.password());

    //when
    UserEntity entity = userMapper.toEntity(roles, requestDto, passwordEncoded);

    //then
    assertNotNull(entity);
    assertEquals(requestDto.username(), entity.getUsername());
    assertEquals(passwordEncoded, entity.getPassword());
    assertEquals(requestDto.email(), entity.getEmail());
    assertFalse(entity.getIsAccountExpired());
    assertFalse(entity.getIsAccountLocked());
    assertFalse(entity.getIsCredentialsExpired());
    assertThat(entity.getRoles())
        .isNotNull()
        .hasSize(roles.size())
        .containsExactlyElementsOf(roles);
  }

  @Test
  void toDto_GivenValidUserEntity_ShouldReturnUserResponseDto() {
    //given
    UserMapper userMapper = new UserMapper();
    UserEntity entity = TestDataFactory.createDefaultUserEntity();

    //when
    UserResponseDto dto = userMapper.toDto(entity);

    //then
    assertNotNull(dto);
    assertEquals(entity.getUsername(), dto.username());
    /* assertEquals(entity.getPassword(), dto.password()); UserResponseDto don't have password */
    assertEquals(entity.getEmail(), dto.email());
    assertEquals(entity.getIsAccountExpired(), dto.isAccountExpired());
    assertEquals(entity.getIsAccountLocked(), dto.isAccountLocked());
    assertEquals(entity.getIsCredentialsExpired(), dto.isCredentialsExpired());
    assertThat(dto.roles())
        .isNotNull()
        .hasSize(entity.getRoles().size())
        .containsExactlyElementsOf(
            entity.getRoles()
                .stream()
                .map(RoleEntity::getRoleEnum)
                .collect(Collectors.toSet())
        );
  }
}