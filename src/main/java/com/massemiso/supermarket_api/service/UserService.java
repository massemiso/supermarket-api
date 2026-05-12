package com.massemiso.supermarket_api.service;

import com.massemiso.supermarket_api.dto.AuthRequestDto;
import com.massemiso.supermarket_api.dto.AuthResponseDto;
import com.massemiso.supermarket_api.dto.UserRequestDto;
import com.massemiso.supermarket_api.dto.UserResponseDto;
import com.massemiso.supermarket_api.dto.mapper.UserMapper;
import com.massemiso.supermarket_api.entity.RoleEntity;
import com.massemiso.supermarket_api.entity.RoleEnum;
import com.massemiso.supermarket_api.entity.UserEntity;
import com.massemiso.supermarket_api.exception.UserNotFoundException;
import com.massemiso.supermarket_api.repository.RoleRepository;
import com.massemiso.supermarket_api.repository.UserRepository;
import com.massemiso.supermarket_api.util.JwtUtil;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Autowired
  public UserService(
      UserRepository userRepository,
      UserMapper userMapper,
      RoleRepository roleRepository,
      PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil
  ){
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {
    UserEntity user =  userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));

    return User.builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .disabled(user.isDeleted())
        .accountExpired(!user.getAccountNonExpired())
        .accountLocked(!user.getAccountNonLocked())
        .credentialsExpired(!user.getCredentialsNonExpired())
        .authorities(user.getAuthorities())
        .build();
  }

  public Page<UserResponseDto> getAll(Pageable pageable) {
    return userRepository
        .findByDeletedAtIsNull(pageable)
        .map(userMapper::toDto);
  }

  public UserResponseDto getById(Long id) {
    UserEntity entity = findById(id);
    return userMapper.toDto(entity);
  }

  @Transactional
  public UserResponseDto create(UserRequestDto requestDto) {
    Set<RoleEntity> roles = requestDto.roles()
        .stream()
        .map(this::findOrCreateRole)
        .collect(Collectors.toSet());
    UserEntity entity = userMapper.toEntity(roles, requestDto,
        passwordEncoder.encode(requestDto.password()));
    entity = userRepository.save(entity);
    return userMapper.toDto(entity);
  }


  @Transactional
  public UserResponseDto update(Long id, UserRequestDto requestDto) {
    Set<RoleEntity> roles = requestDto.roles()
        .stream()
        .map(this::findOrCreateRole)
        .collect(Collectors.toSet());
    UserEntity entity = findById(id);

    entity.update(
        encodePassword(requestDto.password()),
        requestDto.email(),
        roles
    );
    entity = userRepository.save(entity);

    return userMapper.toDto(entity);
  }

  @Transactional
  public void delete(Long id) {
    UserEntity entity = findById(id);
    entity.delete();

    userRepository.save(entity);
  }

  private UserEntity findById(Long id){
    return userRepository
        .findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  private String encodePassword(String password) {
    return passwordEncoder.encode(password);
  }

  private RoleEntity findOrCreateRole(RoleEnum roleEnum){
    return roleRepository.findByRoleEnum(roleEnum)
        .orElseGet(() ->
            roleRepository.save(
                RoleEntity.builder().roleEnum(roleEnum).build()));
  }

  public AuthResponseDto login(AuthRequestDto authRequestDto) {
    Authentication auth = this.authenticate(
        authRequestDto.username(),
        authRequestDto.password());
    SecurityContextHolder.getContext().setAuthentication(auth);
    return new AuthResponseDto(
        authRequestDto.username(),
        jwtUtil.createToken(auth),
        true
    );
  }

  private Authentication authenticate(String username, String password){
    UserDetails userDetails = loadUserByUsername(username);
    if (!userDetails.isEnabled()){
      throw new UsernameNotFoundException("User '" + username + "' not found");
    }

    if (!passwordEncoder.matches(password, userDetails.getPassword())){
      throw new BadCredentialsException("Wrong password for user '" + username + "'");
    }

    return new UsernamePasswordAuthenticationToken(
        username, password, userDetails.getAuthorities());
  }
}
