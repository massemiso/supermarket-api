package com.massemiso.supermarket_api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.massemiso.supermarket_api.BaseIntegrationTest;
import com.massemiso.supermarket_api.config.UserSeeder;
import com.massemiso.supermarket_api.dto.UserRequestDto;
import com.massemiso.supermarket_api.entity.RoleEnum;
import com.massemiso.supermarket_api.entity.UserEntity;
import com.massemiso.supermarket_api.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

class UserControllerTest extends BaseIntegrationTest {
  @Autowired
  private UserRepository repo;

  @Autowired
  private UserSeeder seeder;

  @BeforeEach
  void setup(){
    RestAssured.baseURI = "http://localhost:" + port + "/api/users";
  }

  @AfterEach
  void tearDown(){
    repo.deleteAll();
  }

  @Test
  void getAll_ShouldReturn200AndAJsonWithAPageOfDto() {
    given()
        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader)
        .contentType(ContentType.JSON)
    .when()
        .get()
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", hasSize(4))
        .body("content[0].username", is(ADMIN_USERNAME) )
        .body("content[0].email", is(ADMIN_EMAIL))
        .body("content[1].username", is(MANAGER_USERNAME))
        .body("content[1].email", is(MANAGER_EMAIL))
        .body("content[2].username", is(CASHIER_USERNAME))
        .body("content[2].email", is(CASHIER_EMAIL))
        .body("content[3].username", is(GUEST_USERNAME))
        .body("content[3].email", is(GUEST_EMAIL))
        .body("page.size", is(20))
        .body("page.number", is(0))
        .body("page.totalElements", is(4))
        .body("page.totalPages", is(1));
  }

  @Test
  void getAll_GivenUserNotAuthenticated_ShouldReturn401Unauthorized(){
    given()
        // No given user authenticated
        .contentType(ContentType.JSON)
    .when()
        .get()
    .then()
        .statusCode(HttpStatus.UNAUTHORIZED.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Authentication is required to"
            + " perform a GET on /api/users"))
        .body("status", is(401));
  }

  @Test
  void getAll_GivenUserNotAuthorized_ShouldReturn403Forbidden(){
    given()
        // cashier does not have permission to fetch users
        .header(HttpHeaders.AUTHORIZATION, cashierAuthHeader)
        .contentType(ContentType.JSON)
    .when()
        .get()
    .then()
        .statusCode(HttpStatus.FORBIDDEN.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("You do not have the required"
            + " permissions to perform a GET on /api/users"))
        .body("status", is(403));
  }

  @Test
  void getById_GivenValidId_ShouldReturn200AndApiResponseOfDto() {
    UserEntity manager = getUser(MANAGER_USERNAME);
    int validId = manager.getId().intValue();

    given()
        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader)
        .contentType(ContentType.JSON)
    .when()
        .get("/{id}", validId)
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", notNullValue())
        .body("content.id", is(validId))
        .body("content.username", is(manager.getUsername()))
        .body("content.email", is(manager.getEmail()))
        .body("content.accountNonExpired", is(manager.getAccountNonExpired()))
        .body("content.accountNonLocked", is(manager.getAccountNonLocked()))
        .body("content.credentialsNonExpired", is(manager.getCredentialsNonExpired()))
        .body("content.roles", notNullValue())
        .body("content.roles", hasSize(1))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("User retrieved successfully"))
        .body("status", is(200));
  }

  @Test
  void getById_GivenUserNotAuthenticated_ShouldReturn401Unauthorized(){
    int validId = getUser(MANAGER_USERNAME).getId().intValue();
    given()
        // No given user authenticated
        .contentType(ContentType.JSON)
    .when()
        .get("{id}", validId)
    .then()
        .statusCode(HttpStatus.UNAUTHORIZED.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Authentication is required to"
            + " perform a GET on /api/users/" + validId))
        .body("status", is(401));
  }

  @Test
  void getById_GivenUserNotAuthorized_ShouldReturn403Forbidden(){
    int validId = getUser(MANAGER_USERNAME).getId().intValue();
    given()
        // cashier does not have permission to fetch users
        .header(HttpHeaders.AUTHORIZATION, cashierAuthHeader)
        .contentType(ContentType.JSON)
    .when()
        .get("{id}", validId)
    .then()
        .statusCode(HttpStatus.FORBIDDEN.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("You do not have the required"
            + " permissions to perform a GET on /api/users/" + validId))
        .body("status", is(403));
  }

  @Test
  void create_GivenValidRequestDto_ShouldReturn201AndApiResponseOfDto() {
    UserRequestDto requestDto = new UserRequestDto(
        "newUser",
        "password123",
        "newUser@example.com",
        Set.of(RoleEnum.CASHIER)
    );

    given()
        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader)
        .contentType(ContentType.JSON)
        .body(requestDto)
    .when()
        .post()
    .then()
        .statusCode(201)
        .body("content", notNullValue())
        .body("content.id", notNullValue())
        .body("content.username", is(requestDto.username()))
        .body("content.email", is(requestDto.email()))
        .body("content.accountNonExpired", is(true))
        .body("content.accountNonLocked", is(true))
        .body("content.credentialsNonExpired", is(true))
        .body("content.roles", hasSize(1))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("User created successfully"))
        .body("status", is(201));
  }

  @Test
  void create_GivenInvalidRequestDto_ShouldReturn400AndApiResponseError() {
    UserRequestDto requestDto = new UserRequestDto(
        null,
        null,
        null,
        null
    );

    given()
        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader)
        .contentType(ContentType.JSON)
        .body(requestDto)
    .when()
        .post()
    .then()
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", notNullValue())
        .body("message", containsString("[username] must not be blank"))
        .body("message", containsString("[password] must not be blank"))
        .body("message", containsString("[email] must not be blank"))
        .body("message", containsString("[roles] must not be empty"))
        .body("status", is(HttpStatus.BAD_REQUEST.value()));
  }

  @Test
  void create_GivenUserNotAuthenticated_ShouldReturn401Unauthorized(){
    UserRequestDto requestDto = new UserRequestDto(
        "newUser",
        "password123",
        "newUser@example.com",
        Set.of(RoleEnum.CASHIER)
    );
    given()
        // given no authentication
        .contentType(ContentType.JSON)
        .body(requestDto)
    .when()
        .post()
    .then()
        .statusCode(HttpStatus.UNAUTHORIZED.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Authentication is required to"
            + " perform a POST on /api/users"))
        .body("status", is(401));
  }

  @Test
  void create_GivenUserNotAuthorized_ShouldReturn403Forbidden(){
    UserRequestDto requestDto = new UserRequestDto(
        "newUser",
        "password123",
        "newUser@example.com",
        Set.of(RoleEnum.CASHIER)
    );

    given()
        // cashier does not have permission to create users
        .header(HttpHeaders.AUTHORIZATION, cashierAuthHeader)
        .contentType(ContentType.JSON)
        .body(requestDto)
    .when()
        .post()
    .then()
        .statusCode(HttpStatus.FORBIDDEN.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("You do not have the required"
            + " permissions to perform a POST on /api/users"))
        .body("status", is(403));
  }

  @Test
  void update_GivenValidRequestDtoAndId_ShouldReturn200AndApiResponseOfDto() {
    UserRequestDto requestDto = new UserRequestDto(
        "newUser",
        "password123",
        "newUser@example.com",
        Set.of(RoleEnum.CASHIER)
    );
    UserEntity userEntity = getUser("cashier");
    int validId = userEntity.getId().intValue();

    given()
        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader)
        .contentType(ContentType.JSON)
        .body(requestDto)
    .when()
        .put("/{id}", validId)
    .then()
        .statusCode(200)
        .body("content", notNullValue())
        .body("content.id", is(validId))
        // username is not updated because it's unique
        .body("content.username", is(userEntity.getUsername()))
        .body("content.email", is(requestDto.email()))
        .body("content.accountNonExpired", is(true))
        .body("content.accountNonLocked", is(true))
        .body("content.credentialsNonExpired", is(true))
        .body("content.roles", hasSize(1))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("User updated successfully"))
        .body("status", is(200));
  }

  @Test
  void update_GivenInvalidId_ShouldReturn404AndApiResponseError() {
    int invalidId = -1;
    UserRequestDto requestDto = new UserRequestDto(
        "newUser",
        "password123",
        "newUser@example.com",
        Set.of(RoleEnum.CASHIER)
    );

    given()
        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader)
        .contentType(ContentType.JSON)
        .body(requestDto)
    .when()
        .put("/{id}", invalidId)
    .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("User not found with id: " + invalidId))
        .body("status", is(404));

  }

  @Test
  void update_GivenInvalidRequestDto_ShouldReturn400AndApiResponseError() {
    UserRequestDto requestDto = new UserRequestDto(
        null,
        null,
        null,
        null
    );
    UserEntity userEntity = getUser("cashier");
    int validId = userEntity.getId().intValue();

    given()
        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader)
        .contentType(ContentType.JSON)
        .body(requestDto)
     .when()
        .put("/{id}", validId)
     .then()
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", containsString("[username] must not be blank"))
        .body("message", containsString("[password] must not be blank"))
        .body("message", containsString("[email] must not be blank"))
        .body("message", containsString("[roles] must not be empty"))
        .body("status", is(HttpStatus.BAD_REQUEST.value()));
  }

  @Test
  void update_GivenUserNotAuthenticated_ShouldReturn401Unauthorized(){
    UserRequestDto requestDto = new UserRequestDto(
        "newUser",
        "password123",
        "newUser@example.com",
        Set.of(RoleEnum.CASHIER)
    );
    UserEntity userEntity = getUser("cashier");
    int validId = userEntity.getId().intValue();

    given()
        // given no authentication
        .contentType(ContentType.JSON)
        .body(requestDto)
    .when()
        .put("{id}", validId)
    .then()
        .statusCode(HttpStatus.UNAUTHORIZED.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Authentication is required to"
            + " perform a PUT on /api/users/" + validId))
        .body("status", is(401));
  }

  @Test
  void update_GivenUserNotAuthorized_ShouldReturn403Forbidden(){
    UserRequestDto requestDto = new UserRequestDto(
        "newUser",
        "password123",
        "newUser@example.com",
        Set.of(RoleEnum.CASHIER)
    );
    UserEntity userEntity = getUser("cashier");
    int validId = userEntity.getId().intValue();

    given()
        // Cashier does not have permission to update users
        .header(HttpHeaders.AUTHORIZATION, cashierAuthHeader)
        .contentType(ContentType.JSON)
        .body(requestDto)
    .when()
        .put("{id}", validId)
    .then()
        .statusCode(HttpStatus.FORBIDDEN.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("You do not have the required"
            + " permissions to perform a PUT on /api/users/" + validId))
        .body("status", is(403));
  }

  @Test
  void delete_GivenValidId_ShouldReturn204AndNoContent() {
    int validId = getUser(MANAGER_USERNAME).getId().intValue();
    given()
        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader)
        .contentType(ContentType.JSON)
    .when()
        .delete("/{id}", validId)
    .then()
        .statusCode(HttpStatus.NO_CONTENT.value());
  }

  @Test
  void delete_GivenInvalidId_ShouldReturn404AndApiResponseError() {
    int invalidId = -1;
    given()
        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader)
        .contentType(ContentType.JSON)
    .when()
        .delete("/{id}", invalidId)
    .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("User not found with id: " + invalidId))
        .body("status", is(404));
  }

  @Test
  void delete_GivenUserNotAuthenticated_ShouldReturn401Unauthorized(){
    int validId = getUser(MANAGER_USERNAME).getId().intValue();
    given()
        // given no authentication
        .contentType(ContentType.JSON)
    .when()
        .delete("{id}", validId)
    .then()
        .statusCode(HttpStatus.UNAUTHORIZED.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Authentication is required to"
            + " perform a DELETE on /api/users/" + validId))
        .body("status", is(401));
  }

  @Test
  void delete_GivenUserNotAuthorized_ShouldReturn403Forbidden(){
    int validId = getUser(MANAGER_USERNAME).getId().intValue();
    given()
        // Cashier does not have permission to delete products
        .header(HttpHeaders.AUTHORIZATION, cashierAuthHeader)
        .contentType(ContentType.JSON)
    .when()
        .delete("{id}", validId)
    .then()
        .statusCode(HttpStatus.FORBIDDEN.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("You do not have the required"
            + " permissions to perform a DELETE on /api/users/" + validId))
        .body("status", is(403));
  }

  private UserEntity getUser(String username){
    return repo
        .findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }
}