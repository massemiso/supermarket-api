package com.massemiso.supermarket_api.config;

import com.massemiso.supermarket_api.entity.RoleEntity;
import com.massemiso.supermarket_api.entity.RoleEnum;
import com.massemiso.supermarket_api.entity.UserEntity;
import com.massemiso.supermarket_api.repository.RoleRepository;
import com.massemiso.supermarket_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:mock-users.properties")
@Slf4j
public class UserSeeder implements CommandLineRunner {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${user.admin.username}")
  private String adminUsername;
  @Value("${user.admin.password}")
  private String adminPassword;
  @Value("${user.admin.email")
  private String adminEmail;
  @Value("${user.manager.username}")
  private String managerUsername;
  @Value("${user.manager.password}")
  private String managerPassword;
  @Value("${user.manager.email")
  private String managerEmail;
  @Value("${user.cashier.username}")
  private String cashierUsername;
  @Value("${user.cashier.password}")
  private String cashierPassword;
  @Value("${user.cashier.email")
  private String cashierEmail;

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

    log.info("No users found. Seeding some mock accounts...");
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
        .username(adminUsername)
        .password(passwordEncoder.encode(adminPassword))
        .email(adminEmail)
        .accountNonExpired(true)
        .accountNonLocked(true)
        .credentialsNonExpired(true)
        .roles(Set.of(adminRole))
        .build();
    userRepository.save(admin);
    log.info("Admin user '" + adminUsername
        + "'  with password '" + adminPassword + "' created succesfully.");

    // creating default manager user
    UserEntity manager = UserEntity.builder()
        .username(managerUsername)
        .password(passwordEncoder.encode(managerPassword))
        .email(managerEmail)
        .accountNonExpired(true)
        .accountNonLocked(true)
        .credentialsNonExpired(true)
        .roles(Set.of(managerRole))
        .build();
    userRepository.save(manager);
    log.info("Manager user '" + managerUsername
        + "'  with password '" + managerPassword + "' created succesfully.");

    // creating default cashier user
    UserEntity cashier = UserEntity.builder()
        .username(cashierUsername)
        .password(passwordEncoder.encode(cashierPassword))
        .email(cashierEmail)
        .accountNonExpired(true)
        .accountNonLocked(true)
        .credentialsNonExpired(true)
        .roles(Set.of(cashierRole))
        .build();
    userRepository.save(cashier);
    log.info("Cashier user '" + cashierUsername
        + "'  with password '" + cashierPassword + "' created succesfully.");
  }
}
