package com.massemiso.supermarket_api;

import static io.restassured.RestAssured.*;
import static io.restassured.config.JsonConfig.jsonConfig;

import com.massemiso.supermarket_api.config.UserSeeder;
import com.massemiso.supermarket_api.dto.AuthRequestDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PropertySource("classpath:mock-users.properties")
public abstract class BaseIntegrationTest {

  /* Testcontainers configuration */
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

  /* Security Users Seeder */
  @Autowired
  private UserSeeder seeder;

  @BeforeEach
  void setup(){
    RestAssured.config = RestAssured.config()
        .jsonConfig(jsonConfig()
            .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
    RestAssured.port = port;
    RestAssured.baseURI = "http://localhost";
    seeder.createUsersIfNotExists();
    this.adminAuthHeader = "Bearer " + getJwtToken(ADMIN_USERNAME, ADMIN_PASSWORD);
    this.managerAuthHeader = "Bearer " +  getJwtToken(MANAGER_USERNAME, MANAGER_PASSWORD);
    this.cashierAuthHeader = "Bearer " + getJwtToken(CASHIER_USERNAME, CASHIER_PASSWORD);
  }

  private String getJwtToken(String username, String password){
    return given()
        .port(port)
        .contentType(ContentType.JSON)
        .body(new AuthRequestDto(username, password))
    .when()
        .post("/api/auth/login")
    .then()
        .statusCode(200)
        .extract()
        .path("content.token");
  }

  protected String adminAuthHeader;
  protected String managerAuthHeader;
  protected String cashierAuthHeader;

  @Value("${user.admin.username}") protected String ADMIN_USERNAME;
  @Value("${user.admin.password}") protected String ADMIN_PASSWORD;
  @Value("${user.admin.email}") protected String ADMIN_EMAIL;
  @Value("${user.manager.username}") protected String MANAGER_USERNAME;
  @Value("${user.manager.password}") protected String MANAGER_PASSWORD;
  @Value("${user.manager.email}") protected String MANAGER_EMAIL;
  @Value("${user.cashier.username}") protected String CASHIER_USERNAME;
  @Value("${user.cashier.password}") protected String CASHIER_PASSWORD;
  @Value("${user.cashier.email}") protected String CASHIER_EMAIL;
}
