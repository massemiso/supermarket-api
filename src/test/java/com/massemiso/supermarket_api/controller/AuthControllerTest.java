package com.massemiso.supermarket_api.controller;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import com.massemiso.supermarket_api.BaseIntegrationTest;
import com.massemiso.supermarket_api.config.UserSeeder;
import com.massemiso.supermarket_api.dto.AuthRequestDto;
import com.massemiso.supermarket_api.repository.UserRepository;
import com.massemiso.supermarket_api.service.UserService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class AuthControllerTest extends BaseIntegrationTest {
  @Autowired
  private UserRepository repo;

  @Autowired
  private UserSeeder seeder;

  @Autowired
  private UserService userService;

  @BeforeEach
  void setup(){
    RestAssured.baseURI = "http://localhost:" + port + "/api/auth";
  }

  @AfterEach
  void tearDown(){
    repo.deleteAll();
  }

  @Test
  void login_GivenValidAuthRequestDto_ShouldReturn200AndApiResponseOfAuthResponseDto() {
    // arrange
    AuthRequestDto requestDto = new AuthRequestDto(
        ADMIN_USERNAME,
        ADMIN_PASSWORD
    );

    // act
    given()
        .contentType(ContentType.JSON)
        .body(requestDto)
    .when()
        .post("/login")
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", notNullValue())
        .body("content.username", is(requestDto.username()))
        .body("content.token", notNullValue())
        .body("content.status", is(true))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Login successfull"))
        .body("status", is(HttpStatus.OK.value()));
  }

  @Test
  void login_GivenInvalidUsername_ShouldReturn404NotFound() {
    // arrange
    AuthRequestDto requestDto = new AuthRequestDto(
        "not_a_valid_username",
        "some_password"
    );

    // act
    given()
        .contentType(ContentType.JSON)
        .body(requestDto)
    .when()
        .post("/login")
    .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("User '" + requestDto.username() + "' not found"))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("status", is(HttpStatus.NOT_FOUND.value()));
  }

  @Test
  void login_GivenUserDisabled_ShouldReturn404NotFound() {
    // arrange
    AuthRequestDto requestDto = new AuthRequestDto(
        MANAGER_USERNAME,
        MANAGER_PASSWORD
    );
    userService.delete(repo.findByUsername(MANAGER_USERNAME).get().getId());

    // act
    given()
        .contentType(ContentType.JSON)
        .body(requestDto)
    .when()
        .post("/login")
    .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("User '" + requestDto.username() + "' not found"))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("status", is(HttpStatus.NOT_FOUND.value()));
  }

  @Test
  void login_GivenInvalidPassword_ShouldReturn401Unauthorized() {
    // arrange
    AuthRequestDto requestDto = new AuthRequestDto(
        ADMIN_USERNAME,
        "wrong_password"
    );

    // act
    given()
        .contentType(ContentType.JSON)
        .body(requestDto)
    .when()
        .post("/login")
    .then()
        .statusCode(HttpStatus.UNAUTHORIZED.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Wrong password for user '" + requestDto.username() + "'"))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("status", is(HttpStatus.UNAUTHORIZED.value()));
  }
}