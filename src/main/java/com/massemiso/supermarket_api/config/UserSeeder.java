package com.massemiso.supermarket_api.config;

import com.massemiso.supermarket_api.entity.RoleEntity;
import com.massemiso.supermarket_api.entity.RoleEnum;
import com.massemiso.supermarket_api.entity.UserEntity;
import com.massemiso.supermarket_api.repository.RoleRepository;
import com.massemiso.supermarket_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserSeeder implements CommandLineRunner {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserSeeder(
      UserRepository userRepository,
      RoleRepository roleRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    if (userRepository.count()!=0){
      return;
    }

    log.info("No users found. Seeding initial Admin account...");
    // creating default roles
    RoleEntity adminRole = roleRepository.save(
        RoleEntity
            .builder()
            .roleEnum(RoleEnum.ADMIN)
            .build());
    RoleEntity managerRole = roleRepository.save(
        RoleEntity
            .builder()
            .roleEnum(RoleEnum.MANAGER)
            .build());
    RoleEntity cashierRole = roleRepository.save(
        RoleEntity
            .builder()
            .roleEnum(RoleEnum.CASHIER)
            .build());

    // creating default admin user
    UserEntity admin = UserEntity.builder()
        .username("admin")
        .password(passwordEncoder.encode("admin123"))
        .email("admin@supermarket.com")
        .isAccountExpired(false)
        .isAccountLocked(false)
        .isCredentialsExpired(false)
        .roles(Set.of(adminRole))
        .build();
    userRepository.save(admin);
    log.info("Admin user 'admin' with password 'admin123' created succesfully.");

    // creating default manager user
    UserEntity manager = UserEntity.builder()
        .username("manager")
        .password(passwordEncoder.encode("manager123"))
        .email("manager@supermarket.com")
        .isAccountExpired(false)
        .isAccountLocked(false)
        .isCredentialsExpired(false)
        .roles(Set.of(managerRole))
        .build();
    userRepository.save(manager);
    log.info("Manager user 'manager' with password 'manager123' created succesfully.");

    // creating default cashier user
    UserEntity cashier = UserEntity.builder()
        .username("cashier")
        .password(passwordEncoder.encode("cashier123"))
        .email("cashier@supermarket.com")
        .isAccountExpired(false)
        .isAccountLocked(false)
        .isCredentialsExpired(false)
        .roles(Set.of(cashierRole))
        .build();
    userRepository.save(cashier);
    log.info("Cashier user 'cashier' with password 'cashier123' created succesfully.");
  }
}
