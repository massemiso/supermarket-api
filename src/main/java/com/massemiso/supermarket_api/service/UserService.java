package com.massemiso.supermarket_api.service;

import com.massemiso.supermarket_api.entity.UserEntity;
import com.massemiso.supermarket_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository){
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {
    UserEntity user =  userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));

    return User.builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .disabled(user.isDeleted())
        .accountExpired(user.getIsAccountExpired())
        .accountLocked(user.getIsAccountLocked())
        .credentialsExpired(user.getIsCredentialsExpired())
        .authorities(user.getAuthorities())
        .build();
  }
}
