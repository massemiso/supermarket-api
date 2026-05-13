package com.massemiso.supermarket_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.massemiso.supermarket_api.dto.AuthRequestDto;
import com.massemiso.supermarket_api.dto.AuthResponseDto;
import com.massemiso.supermarket_api.dto.UserRequestDto;
import com.massemiso.supermarket_api.dto.UserResponseDto;
import com.massemiso.supermarket_api.dto.mapper.UserMapper;
import com.massemiso.supermarket_api.entity.RoleEntity;
import com.massemiso.supermarket_api.entity.RoleEnum;
import com.massemiso.supermarket_api.entity.UserEntity;
import com.massemiso.supermarket_api.exception.UserAlreadyExists;
import com.massemiso.supermarket_api.exception.UserNotFoundException;
import com.massemiso.supermarket_api.repository.RoleRepository;
import com.massemiso.supermarket_api.repository.UserRepository;
import com.massemiso.supermarket_api.util.JwtUtil;
import com.massemiso.supermarket_api.util.TestDataFactory;
import java.time.LocalDateTime;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
  @Mock
  private JwtUtil jwtUtil;
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
        .extracting(GrantedAuthority::getAuthority)
        .containsExactly("ROLE_ADMIN");

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
    when(userRepository.findByUsername(requestDto.username()))
        .thenReturn(Optional.empty());
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

    verify(userRepository).findByUsername(requestDto.username());
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
    when(userRepository.findByUsername(requestDto.username()))
        .thenReturn(Optional.empty());
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

    verify(userRepository).findByUsername(requestDto.username());
    verify(roleRepository).findByRoleEnum(roleEnum);
    verify(roleRepository).save(any(RoleEntity.class));
    verify(passwordEncoder).encode(requestDto.password());
    verify(userMapper).toEntity(roles, requestDto, passwordEncoded);
    verify(userRepository).save(entity);
    verify(userMapper).toDto(entity);
  }

  @Test
  void create_GivenUserThatAlreadyExists_ShouldThrowUserAlreadyExists() {
    // arrange
    UserRequestDto requestDto =
        TestDataFactory.createDefaultUserRequestDto();
    UserEntity entity = TestDataFactory.createDefaultUserEntity();

    // mock
    when(userRepository.findByUsername(requestDto.username()))
        .thenReturn(Optional.of(entity));

    // act & assert
    assertThrows(UserAlreadyExists.class,
        () -> userService.create(requestDto)
    );

    // assert
    verify(userRepository).findByUsername(requestDto.username());
    verify(roleRepository, never()).findByRoleEnum(any(RoleEnum.class));
    verify(passwordEncoder, never()).encode(anyString());
    verify(userMapper, never()).toEntity(any(), any(UserRequestDto.class), anyString());
    verify(userRepository, never()).save(any(UserEntity.class));
    verify(userMapper, never()).toDto(any(UserEntity.class));
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

  @Test
  void login_GivenValidAuthRequestDto_ShouldReturnAuthResponseDtoWithJwtToken(){
    // arrange
    AuthRequestDto requestDto = new AuthRequestDto(
        "admin",
        "my_super_secret_password"
    );
    UserEntity user = UserEntity.builder()
        .username(requestDto.username())
        .email("admin@test.com")
        .password("my_super_secret_password_encoded")
        .roles(Set.of(new RoleEntity(RoleEnum.ADMIN)))
        .accountNonExpired(true)
        .accountNonLocked(true)
        .credentialsNonExpired(true)
        .build();
    Authentication auth = new UsernamePasswordAuthenticationToken(
        requestDto.username(), requestDto.password(), user.getAuthorities());
    String jwtToken = "some_super_secret_jwt_token";

    // mock
    when(userRepository.findByUsername(requestDto.username()))
        .thenReturn(Optional.of(user));
    when(passwordEncoder.matches(requestDto.password(), user.getPassword()))
        .thenReturn(true);
    when(jwtUtil.createToken(auth))
        .thenReturn(jwtToken);

    // act
    AuthResponseDto responseDto = userService.login(requestDto);

    // assert
    assertNotNull(responseDto);
    assertEquals(requestDto.username(), responseDto.username());
    assertThat(responseDto.token())
        .isNotNull()
        .isEqualTo(jwtToken);
    assertTrue(responseDto.status());

    verify(userRepository).findByUsername(requestDto.username());
    verify(passwordEncoder).matches(requestDto.password(), user.getPassword());
    verify(jwtUtil).createToken(auth);
  }

  @Test
  void login_GivenUserNotFound_ShouldThrowUsernameNotFoundException(){
    // arrange
    AuthRequestDto requestDto = new AuthRequestDto(
        "admin",
        "my_super_secret_password"
    );
    UserEntity user = UserEntity.builder()
        .username(requestDto.username())
        .email("admin@test.com")
        .password("my_super_secret_password_encoded")
        .roles(Set.of(new RoleEntity(RoleEnum.ADMIN)))
        .accountNonExpired(true)
        .accountNonLocked(true)
        .credentialsNonExpired(true)
        .build();

    // mock
    when(userRepository.findByUsername(requestDto.username()))
        .thenReturn(Optional.empty());

    // act & assert
    assertThrows(UsernameNotFoundException.class,
        () ->
            userService.login(requestDto)
    );

    verify(userRepository).findByUsername(requestDto.username());
    verify(passwordEncoder, never()).matches(requestDto.password(), user.getPassword());
    verify(jwtUtil, never()).createToken(any(Authentication.class));
  }

  @Test
  void login_GivenUserDisabled_ShouldThrowUsernameNotFoundException(){
    // arrange
    AuthRequestDto requestDto = new AuthRequestDto(
        "admin",
        "my_super_secret_password"
    );
    UserEntity user = UserEntity.builder()
        .username(requestDto.username())
        .email("admin@test.com")
        .password("my_super_secret_password_encoded")
        .roles(Set.of(new RoleEntity(RoleEnum.ADMIN)))
        .accountNonExpired(true)
        .accountNonLocked(true)
        .credentialsNonExpired(true)
        .build();
    ReflectionTestUtils.setField(user, "deletedAt", LocalDateTime.now());

    // mock
    when(userRepository.findByUsername(requestDto.username()))
        .thenReturn(Optional.of(user));

    // act & assert
    assertThrows(UsernameNotFoundException.class,
        () ->
            userService.login(requestDto)
    );

    verify(userRepository).findByUsername(requestDto.username());
    verify(passwordEncoder, never()).matches(requestDto.password(), user.getPassword());
    verify(jwtUtil, never()).createToken(any(Authentication.class));
  }

  @Test
  void login_GivenInvalidPassword_ShouldThrowBadCredentialsException(){
    // arrange
    AuthRequestDto requestDto = new AuthRequestDto(
        "admin",
        "my_super_secret_INVALID_password"
    );
    UserEntity user = UserEntity.builder()
        .username(requestDto.username())
        .email("admin@test.com")
        .password("my_super_secret_password_encoded")
        .roles(Set.of(new RoleEntity(RoleEnum.ADMIN)))
        .accountNonExpired(true)
        .accountNonLocked(true)
        .credentialsNonExpired(true)
        .build();

    // mock
    when(userRepository.findByUsername(requestDto.username()))
        .thenReturn(Optional.of(user));
    when(passwordEncoder.matches(requestDto.password(), user.getPassword()))
        .thenReturn(false);

    // act & assert
    assertThrows(BadCredentialsException.class,
        () ->
            userService.login(requestDto)
    );

    verify(userRepository).findByUsername(requestDto.username());
    verify(passwordEncoder).matches(requestDto.password(), user.getPassword());
    verify(jwtUtil, never()).createToken(any(Authentication.class));
  }
}