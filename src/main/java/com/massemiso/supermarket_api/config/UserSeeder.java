package com.massemiso.supermarket_api.config;

import com.massemiso.supermarket_api.dto.UserRequestDto;
import com.massemiso.supermarket_api.entity.RoleEnum;
import com.massemiso.supermarket_api.repository.UserRepository;
import com.massemiso.supermarket_api.service.UserService;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:mock-users.properties")
@Slf4j
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

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private UserService userService;

  public void createUsersIfNotExists() {
    log.info("Seeding some mock accounts if needed...");
    if (userRepository.findByUsername(ADMIN_USERNAME).isEmpty()){
      saveUser(RoleEnum.ADMIN, ADMIN_USERNAME, ADMIN_PASSWORD, ADMIN_EMAIL);
    }
    if (userRepository.findByUsername(MANAGER_USERNAME).isEmpty()){
      saveUser(RoleEnum.MANAGER, MANAGER_USERNAME, MANAGER_PASSWORD, MANAGER_EMAIL);
    }
    if (userRepository.findByUsername(CASHIER_USERNAME).isEmpty()){
      saveUser(RoleEnum.CASHIER, CASHIER_USERNAME, CASHIER_PASSWORD, CASHIER_EMAIL);
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
    log.info(roleEnum.name() + " user '" + username
        + "' with password '" + password + "' created succesfully.");
  }

  @Override
  public void run(String... args) throws Exception {
    createUsersIfNotExists();
  }
}
