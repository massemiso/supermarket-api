package com.massemiso.supermarket_api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PropertySource("classpath:mock-users.properties")
public abstract class BaseIntegrationTest {

  @LocalServerPort
  protected Integer port;

  static PostgreSQLContainer postgres = new PostgreSQLContainer(
      "postgres:15"
  );

  static  {
    postgres.start();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Value("${user.admin.username}")
  protected String adminUsername;
  @Value("${user.admin.password}")
  protected String adminPassword;
  @Value("${user.admin.email}")
  protected String adminEmail;
  @Value("${user.manager.username}")
  protected String managerUsername;
  @Value("${user.manager.password}")
  protected String managerPassword;
  @Value("${user.manager.email}")
  protected String managerEmail;
  @Value("${user.cashier.username}")
  protected String cashierUsername;
  @Value("${user.cashier.password}")
  protected String cashierPassword;
  @Value("${user.cashier.email}")
  protected String cashierEmail;
}
