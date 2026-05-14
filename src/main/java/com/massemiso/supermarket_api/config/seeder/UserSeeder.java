package com.massemiso.supermarket_api.config;

import com.massemiso.supermarket_api.dto.UserRequestDto;
import com.massemiso.supermarket_api.entity.RoleEnum;
import com.massemiso.supermarket_api.repository.UserRepository;
import com.massemiso.supermarket_api.service.UserService;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
@PropertySource("classpath:mock-users.properties")
@Slf4j
@Order(1)
public class UserSeeder implements CommandLineRunner {
  @Value("${user.admin.username}")
  private String ADMIN_USERNAME;
  @Value("${user.admin.password}")
  private String ADMIN_PASSWORD;
  @Value("${user.admin.email}")
  private String ADMIN_EMAIL;
  @Value("${user.manager.username}")
  private String MANAGER_USERNAME;
  @Value("${user.manager.password}")
  private String MANAGER_PASSWORD;
  @Value("${user.manager.email}")
  private String MANAGER_EMAIL;
  @Value("${user.cashier.username}")
  private String CASHIER_USERNAME;
  @Value("${user.cashier.password}")
  private String CASHIER_PASSWORD;
  @Value("${user.cashier.email}")
  private String CASHIER_EMAIL;
  @Value("${user.guest.username}")
  private String GUEST_USERNAME;
  @Value("${user.guest.password}")
  private String GUEST_PASSWORD;
  @Value("${user.guest.email}")
  private String GUEST_EMAIL;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserService userService;

  public void createUsersIfNotExists() {
    log.info("USER_SEEDER: Seeding some mock accounts if needed...");
    if (userRepository.findByUsername(ADMIN_USERNAME).isEmpty()){
      saveUser(RoleEnum.ADMIN, ADMIN_USERNAME, ADMIN_PASSWORD, ADMIN_EMAIL);
    }
    if (userRepository.findByUsername(MANAGER_USERNAME).isEmpty()){
      saveUser(RoleEnum.MANAGER, MANAGER_USERNAME, MANAGER_PASSWORD, MANAGER_EMAIL);
    }
    if (userRepository.findByUsername(CASHIER_USERNAME).isEmpty()){
      saveUser(RoleEnum.CASHIER, CASHIER_USERNAME, CASHIER_PASSWORD, CASHIER_EMAIL);
    }
    if (userRepository.findByUsername(GUEST_USERNAME).isEmpty()){
      saveUser(RoleEnum.GUEST, GUEST_USERNAME, GUEST_PASSWORD, GUEST_EMAIL);
    }
  }

  private void saveUser(RoleEnum roleEnum, String username,
      String password, String email){
    UserRequestDto userRequestDto = new UserRequestDto(
        username,
        password,
        email,
        Set.of(roleEnum)
    );
    userService.create(userRequestDto);
    log.info("USER_SEEDER: {} user '{}' with password '{}' created successfully.",
        roleEnum.name(), username, password);
  }

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    createUsersIfNotExists();
  }
}
