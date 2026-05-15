package com.massemiso.supermarket_api.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.massemiso.supermarket_api.dto.AuthRegisterRequestDto;
import com.massemiso.supermarket_api.dto.UserRequestDto;
import com.massemiso.supermarket_api.dto.UserResponseDto;
import com.massemiso.supermarket_api.entity.RoleEntity;
import com.massemiso.supermarket_api.entity.UserEntity;
import com.massemiso.supermarket_api.util.TestDataFactory;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class UserMapperTest {

  private final UserMapper mapper = UserMapper.INSTANCE;

  @Test
  void toEntity_GivenValidUserRequestDto_ShouldReturnUserEntity() {
    //given
    Set<RoleEntity> roles = Set.of(TestDataFactory.createDefaultRoleEntity());
    UserRequestDto requestDto = TestDataFactory.createDefaultUserRequestDto();
    String passwordEncoded = new BCryptPasswordEncoder().encode(requestDto.password());

    //when
    UserEntity entity = mapper.toEntity(roles, requestDto, passwordEncoded);

    //then
    assertNotNull(entity);
    assertEquals(requestDto.username(), entity.getUsername());
    assertEquals(passwordEncoded, entity.getPassword());
    assertEquals(requestDto.email(), entity.getEmail());
    assertTrue(entity.getAccountNonExpired());
    assertTrue(entity.getAccountNonLocked());
    assertTrue(entity.getCredentialsNonExpired());
    assertThat(entity.getRoles())
        .isNotNull()
        .hasSize(roles.size())
        .anyMatch(role ->
            roles.iterator().next().getRoleEnum().equals(role.getRoleEnum()));
  }

  @Test
  void toEntity_GivenValidAuthRegisterRequestDto_ShouldReturnUserEntity() {
    //given
    Set<RoleEntity> roles = Set.of(TestDataFactory.createDefaultRoleEntity());
    UserRequestDto userDto = TestDataFactory.createDefaultUserRequestDto();
    AuthRegisterRequestDto requestDto = new AuthRegisterRequestDto(
        userDto.username(),
        userDto.password(),
        userDto.email()
    );
    String passwordEncoded = new BCryptPasswordEncoder().encode(requestDto.password());

    //when
    UserEntity entity = mapper.toEntity(roles, requestDto, passwordEncoded);

    //then
    assertNotNull(entity);
    assertEquals(requestDto.username(), entity.getUsername());
    assertEquals(passwordEncoded, entity.getPassword());
    assertEquals(requestDto.email(), entity.getEmail());
    assertTrue(entity.getAccountNonExpired());
    assertTrue(entity.getAccountNonLocked());
    assertTrue(entity.getCredentialsNonExpired());
    assertThat(entity.getRoles())
        .isNotNull()
        .hasSize(roles.size())
        .containsExactlyElementsOf(roles);
  }

  @Test
  void toDto_GivenValidUserEntity_ShouldReturnUserResponseDto() {
    //given
    UserEntity entity = TestDataFactory.createDefaultUserEntity();

    //when
    UserResponseDto dto = mapper.toDto(entity);

    //then
    assertNotNull(dto);
    assertEquals(entity.getUsername(), dto.username());
    /* assertEquals(entity.getPassword(), dto.password()); UserResponseDto don't have password */
    assertEquals(entity.getEmail(), dto.email());
    assertEquals(entity.getAccountNonExpired(), dto.accountNonExpired());
    assertEquals(entity.getAccountNonLocked(), dto.accountNonLocked());
    assertEquals(entity.getCredentialsNonExpired(), dto.credentialsNonExpired());
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