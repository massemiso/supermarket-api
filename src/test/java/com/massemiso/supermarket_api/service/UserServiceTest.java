package com.massemiso.supermarket_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.massemiso.supermarket_api.dto.UserRequestDto;
import com.massemiso.supermarket_api.dto.UserResponseDto;
import com.massemiso.supermarket_api.dto.mapper.UserMapper;
import com.massemiso.supermarket_api.entity.RoleEntity;
import com.massemiso.supermarket_api.entity.RoleEnum;
import com.massemiso.supermarket_api.entity.UserEntity;
import com.massemiso.supermarket_api.exception.UserNotFoundException;
import com.massemiso.supermarket_api.repository.RoleRepository;
import com.massemiso.supermarket_api.repository.UserRepository;
import com.massemiso.supermarket_api.util.TestDataFactory;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @Mock
  private RoleRepository roleRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @InjectMocks
  private UserService userService;

  @Test
  void loadUserByUsername_GivenValidUsername_ShouldReturnUserDetails() {
    // Arrange
    String username = TestDataFactory.getDEFAULT_USER_USERNAME();
    UserEntity entity = TestDataFactory.createDefaultUserEntity();

    when(userRepository.findByUsername(username))
        .thenReturn(Optional.of(entity));

    // Act
    UserDetails result = userService.loadUserByUsername(username);

    // Assert
    assertNotNull(result);
    assertEquals(username, result.getUsername());
    assertEquals(entity.getPassword(), result.getPassword());
    /* assertEquals(entity.getEmail(), result.getEmail()); UserDetails don't have email */
    assertEquals(entity.getAccountNonExpired(), result.isAccountNonExpired());
    assertEquals(entity.getAccountNonLocked(), result.isAccountNonLocked());
    assertEquals(entity.getCredentialsNonExpired(), result.isCredentialsNonExpired());
    assertEquals(!entity.isDeleted(), result.isEnabled());
    assertThat(result.getAuthorities())
        .isNotNull()
        .hasSize(entity.getRoles().size());

    verify(userRepository).findByUsername(username);
  }

  @Test
  void loadUserByUsername_GivenInvalidUsername_ShouldThrowUsernameNotFoundException() {
    // Arrange
    String username = "invalid_username";

    when(userRepository.findByUsername(username))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UsernameNotFoundException.class,
        () -> userService.loadUserByUsername(username));

    verify(userRepository).findByUsername(anyString());
  }

  @Test
  void getAll_GivenPageable_ShouldReturnSomeUserResponseDtoInAPage() {
    // arrange
    Pageable pageable = PageRequest.of(0, 10);
    UserResponseDto responseDto =
        TestDataFactory.createDefaultUserResponseDto();
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    ReflectionTestUtils.setField(entity, "id", responseDto.id());

    List<UserEntity> entities = List.of(entity);
    Page<UserEntity> page = new PageImpl<>(entities, pageable, entities.size());

    // mock
    when(userRepository.findByDeletedAtIsNull(pageable))
        .thenReturn(page);
    when(userMapper.toDto(entity))
        .thenReturn(responseDto);

    // act
    Page<UserResponseDto> actual = userService.getAll(pageable);

    // assert
    assertNotNull(actual);
    assertEquals(entities.size(), actual.getContent().size());
    assertEquals(responseDto.id(), actual.getContent().getFirst().id());
    assertEquals(responseDto.username(), actual.getContent().getFirst().username());
    assertEquals(responseDto.email(), actual.getContent().getFirst().email());
    assertEquals(responseDto.accountNonExpired(), actual.getContent().getFirst().accountNonExpired());
    assertEquals(responseDto.accountNonLocked(), actual.getContent().getFirst().accountNonLocked());
    assertEquals(responseDto.credentialsNonExpired(), actual.getContent().getFirst().credentialsNonExpired());
    assertThat(actual.getContent().getFirst().roles())
        .isNotNull()
        .hasSize(entity.getRoles().size());

    verify(userRepository).findByDeletedAtIsNull(pageable);
    verify(userMapper, times(1)).toDto(entity);
  }

  @Test
  void getById_GivenValidId_ShouldReturnAUserResponseDto() {
    // arrange
    UserResponseDto responseDto = TestDataFactory.createDefaultUserResponseDto();
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    Long validId = responseDto.id();
    ReflectionTestUtils.setField(entity, "id", validId);

    // mock
    when(userRepository.findByIdAndDeletedAtIsNull(validId))
        .thenReturn(Optional.of(entity));
    when(userMapper.toDto(entity))
        .thenReturn(responseDto);

    // act
    UserResponseDto actual = userService.getById(validId);

    // assert
    assertNotNull(actual);
    assertEquals(responseDto.id(), actual.id());
    assertEquals(responseDto.username(), actual.username());
    assertEquals(responseDto.email(), actual.email());
    assertEquals(responseDto.accountNonExpired(), actual.accountNonExpired());
    assertEquals(responseDto.accountNonLocked(), actual.accountNonLocked());
    assertEquals(responseDto.credentialsNonExpired(), actual.credentialsNonExpired());
    assertThat(actual.roles())
        .isNotNull()
        .hasSize(entity.getRoles().size());

    verify(userRepository).findByIdAndDeletedAtIsNull(validId);
    verify(userMapper).toDto(entity);
  }

  @Test
  void getById_GivenInvalidId_ShouldThrowUserNotFoundException() {
    // arrange
    Long invalidId = -1L;

    // mock
    when(userRepository.findByIdAndDeletedAtIsNull(invalidId))
        .thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () ->
            userService.getById(invalidId)
    );

    verify(userRepository).findByIdAndDeletedAtIsNull(invalidId);
    verify(userMapper, never()).toDto(any(UserEntity.class));
  }

  @Test
  void create_GivenValidUserRequestDtoAndRoleExistsInDb_ShouldReturnAUserResponseDto() {
    // arrange
    UserResponseDto responseDto =
        TestDataFactory.createDefaultUserResponseDto();
    UserRequestDto requestDto =
        TestDataFactory.createDefaultUserRequestDto();
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    Long validId = responseDto.id();
    ReflectionTestUtils.setField(entity, "id", validId);

    RoleEnum roleEnum = requestDto.roles().iterator().next();
    RoleEntity roleEntity = TestDataFactory.createDefaultRoleEntity();
    Set<RoleEntity> roles = Set.of(roleEntity);
    String passwordEncoded = "password_super_protected";

    // mock
    when(roleRepository.findByRoleEnum(roleEnum))
        .thenReturn(Optional.of(roleEntity));
    when(passwordEncoder.encode(requestDto.password()))
        .thenReturn(passwordEncoded);
    when(userMapper.toEntity(roles, requestDto, passwordEncoded))
        .thenReturn(entity);
    when(userRepository.save(entity))
        .thenReturn(entity);
    when(userMapper.toDto(entity))
        .thenReturn(responseDto);

    // act
    UserResponseDto actual = userService.create(requestDto);

    // assert
    assertNotNull(actual);
    assertEquals(responseDto.id(), actual.id());
    assertEquals(responseDto.username(), actual.username());
    assertNotEquals(passwordEncoded, requestDto.password());
    assertEquals(responseDto.email(), actual.email());
    assertEquals(responseDto.accountNonExpired(), actual.accountNonExpired());
    assertEquals(responseDto.accountNonLocked(), actual.accountNonLocked());
    assertEquals(responseDto.credentialsNonExpired(), actual.credentialsNonExpired());
    assertThat(actual.roles())
        .isNotNull()
        .hasSize(entity.getRoles().size());

    verify(roleRepository).findByRoleEnum(roleEnum);
    verify(passwordEncoder).encode(requestDto.password());
    verify(userMapper).toEntity(roles, requestDto, passwordEncoded);
    verify(userRepository).save(entity);
    verify(userMapper).toDto(entity);
  }

  @Test
  void create_GivenValidUserRequestDtoAndRoleDontExistsInDb_ShouldReturnAUserResponseDto() {
    // arrange
    UserResponseDto responseDto =
        TestDataFactory.createDefaultUserResponseDto();
    UserRequestDto requestDto =
        TestDataFactory.createDefaultUserRequestDto();
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    Long validId = responseDto.id();
    ReflectionTestUtils.setField(entity, "id", validId);

    RoleEnum roleEnum = requestDto.roles().iterator().next();
    RoleEntity roleEntity = TestDataFactory.createDefaultRoleEntity();
    Set<RoleEntity> roles = Set.of(roleEntity);
    String passwordEncoded = "password_super_protected";

    // mock
    when(roleRepository.findByRoleEnum(roleEnum))
        .thenReturn(Optional.empty());
    when(roleRepository.save(any(RoleEntity.class)))
        .thenReturn(roleEntity);
    when(passwordEncoder.encode(requestDto.password()))
        .thenReturn(passwordEncoded);
    when(userMapper.toEntity(roles, requestDto, passwordEncoded))
        .thenReturn(entity);
    when(userRepository.save(entity))
        .thenReturn(entity);
    when(userMapper.toDto(entity))
        .thenReturn(responseDto);

    // act
    UserResponseDto actual = userService.create(requestDto);

    // assert
    assertNotNull(actual);
    assertEquals(responseDto.id(), actual.id());
    assertEquals(responseDto.username(), actual.username());
    assertNotEquals(passwordEncoded, requestDto.password());
    assertEquals(responseDto.email(), actual.email());
    assertEquals(responseDto.accountNonExpired(), actual.accountNonExpired());
    assertEquals(responseDto.accountNonLocked(), actual.accountNonLocked());
    assertEquals(responseDto.credentialsNonExpired(), actual.credentialsNonExpired());
    assertThat(actual.roles())
        .isNotNull()
        .hasSize(entity.getRoles().size());

    verify(roleRepository).findByRoleEnum(roleEnum);
    verify(roleRepository).save(any(RoleEntity.class));
    verify(passwordEncoder).encode(requestDto.password());
    verify(userMapper).toEntity(roles, requestDto, passwordEncoded);
    verify(userRepository).save(entity);
    verify(userMapper).toDto(entity);
  }

  @Test
  void update_GivenValidIdAndUserRequestDtoAndRoleExistsInDb_ShouldReturnAUserResponseDto() {
    // arrange
    UserResponseDto responseDto =
        TestDataFactory.createDefaultUserResponseDto();
    UserRequestDto requestDto =
        TestDataFactory.createDefaultUserRequestDto();
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    Long validId = responseDto.id();
    ReflectionTestUtils.setField(entity, "id", validId);

    RoleEnum roleEnum = requestDto.roles().iterator().next();
    RoleEntity roleEntity = TestDataFactory.createDefaultRoleEntity();
    Set<RoleEntity> roles = Set.of(roleEntity);
    String passwordEncoded = "password_super_protected";

    // mock
    when(roleRepository.findByRoleEnum(roleEnum))
        .thenReturn(Optional.of(roleEntity));
    when(userRepository.findByIdAndDeletedAtIsNull(validId))
        .thenReturn(Optional.of(entity));
    when(passwordEncoder.encode(requestDto.password()))
        .thenReturn(passwordEncoded);
    when(userRepository.save(entity))
        .thenReturn(entity);
    when(userMapper.toDto(entity))
        .thenReturn(responseDto);

    // act
    UserResponseDto actual = userService.update(validId, requestDto);

    // assert
    assertNotNull(actual);
    assertEquals(responseDto.id(), actual.id());
    assertEquals(responseDto.username(), actual.username());
    assertNotEquals(passwordEncoded, requestDto.password());
    assertEquals(responseDto.email(), actual.email());
    assertEquals(responseDto.accountNonExpired(), actual.accountNonExpired());
    assertEquals(responseDto.accountNonLocked(), actual.accountNonLocked());
    assertEquals(responseDto.credentialsNonExpired(), actual.credentialsNonExpired());
    assertThat(actual.roles())
        .isNotNull()
        .hasSize(entity.getRoles().size());

    verify(roleRepository).findByRoleEnum(roleEnum);
    verify(userRepository).findByIdAndDeletedAtIsNull(validId);
    verify(passwordEncoder).encode(requestDto.password());
    verify(userRepository).save(entity);
    verify(userMapper).toDto(entity);
  }

  @Test
  void update_GivenValidIdAndUserRequestDtoAndRoleDontExistsInDb_ShouldReturnAUserResponseDto() {
    // arrange
    UserResponseDto responseDto =
        TestDataFactory.createDefaultUserResponseDto();
    UserRequestDto requestDto =
        TestDataFactory.createDefaultUserRequestDto();
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    Long validId = responseDto.id();
    ReflectionTestUtils.setField(entity, "id", validId);

    RoleEnum roleEnum = requestDto.roles().iterator().next();
    RoleEntity roleEntity = TestDataFactory.createDefaultRoleEntity();
    Set<RoleEntity> roles = Set.of(roleEntity);
    String passwordEncoded = "password_super_protected";

    // mock
    when(roleRepository.findByRoleEnum(roleEnum))
        .thenReturn(Optional.empty());
    when(roleRepository.save(any(RoleEntity.class)))
        .thenReturn(roleEntity);
    when(userRepository.findByIdAndDeletedAtIsNull(validId))
        .thenReturn(Optional.of(entity));
    when(passwordEncoder.encode(requestDto.password()))
        .thenReturn(passwordEncoded);
    when(userRepository.save(entity))
        .thenReturn(entity);
    when(userMapper.toDto(entity))
        .thenReturn(responseDto);

    // act
    UserResponseDto actual = userService.update(validId, requestDto);

    // assert
    assertNotNull(actual);
    assertEquals(responseDto.id(), actual.id());
    assertEquals(responseDto.username(), actual.username());
    assertNotEquals(passwordEncoded, requestDto.password());
    assertEquals(responseDto.email(), actual.email());
    assertEquals(responseDto.accountNonExpired(), actual.accountNonExpired());
    assertEquals(responseDto.accountNonLocked(), actual.accountNonLocked());
    assertEquals(responseDto.credentialsNonExpired(), actual.credentialsNonExpired());
    assertThat(actual.roles())
        .isNotNull()
        .hasSize(entity.getRoles().size());

    verify(roleRepository).findByRoleEnum(roleEnum);
    verify(roleRepository).save(any(RoleEntity.class));
    verify(userRepository).findByIdAndDeletedAtIsNull(validId);
    verify(passwordEncoder).encode(requestDto.password());
    verify(userRepository).save(entity);
    verify(userMapper).toDto(entity);
  }

  @Test
  void update_GivenInvalidId_ShouldThrowUserNotFoundException() {
    // arrange
    UserRequestDto requestDto =
        TestDataFactory.createDefaultUserRequestDto();
    Long invalidId = -1L;

    RoleEnum roleEnum = requestDto.roles().iterator().next();
    RoleEntity roleEntity = TestDataFactory.createDefaultRoleEntity();

    // mock
    when(roleRepository.findByRoleEnum(roleEnum))
        .thenReturn(Optional.of(roleEntity));
    when(userRepository.findByIdAndDeletedAtIsNull(invalidId))
        .thenReturn(Optional.empty());

    // act & assert
    assertThrows(UserNotFoundException.class,
        () -> userService.update(invalidId, requestDto));

    verify(roleRepository).findByRoleEnum(roleEnum);
    verify(userRepository).findByIdAndDeletedAtIsNull(invalidId);
    verify(passwordEncoder, never()).encode(requestDto.password());
    verify(userRepository, never()).save(any(UserEntity.class));
    verify(userMapper, never()).toDto(any(UserEntity.class));
  }

  @Test
  void delete_GivenValidId_ShouldSoftDeleteUser() {
    // arrange
    Long validId = TestDataFactory.getDEFAULT_USER_ID();
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    ReflectionTestUtils.setField(entity, "id", validId);

    // mock
    when(userRepository.findByIdAndDeletedAtIsNull(validId))
        .thenReturn(Optional.of(entity));

    // act
    userService.delete(validId);

    // assert
    assertNotNull(entity);
    assertTrue(entity.isDeleted());

    verify(userRepository).findByIdAndDeletedAtIsNull(validId);
    verify(userRepository).save(entity);
  }

  @Test
  void delete_GivenInvalidId_ShouldThrowUserNotFoundException() {
    // arrange
    Long invalidId = -1L;

    // mock
    when(userRepository.findByIdAndDeletedAtIsNull(invalidId))
        .thenReturn(Optional.empty());

    // act & assert
    assertThrows(UserNotFoundException.class,
        () ->
            userService.delete(invalidId)
    );
    verify(userRepository).findByIdAndDeletedAtIsNull(invalidId);
    verify(userRepository, never()).save(any(UserEntity.class));
  }
}