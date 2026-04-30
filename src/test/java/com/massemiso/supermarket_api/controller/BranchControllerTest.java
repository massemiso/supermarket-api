package com.massemiso.supermarket_api.controller;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import com.massemiso.supermarket_api.dto.BranchRequestDto;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.repository.BranchRepository;
import com.massemiso.supermarket_api.BaseIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class BranchControllerTest extends BaseIntegrationTest {

  @Autowired
  private BranchRepository repo;

  @BeforeEach
  void setup(){
    RestAssured.baseURI = "http://localhost:" + port + "/api/branches";
    repo.deleteAll();
  }

  @Test
  void getAll_ShouldReturn200AndAJsonWithAPageOfBranches() {
    List<Branch> entities = this.insertSomeDefaultValues();
    Branch entity1 = entities.getFirst();
    Branch entity2 = entities.getLast();

    given()
        .contentType(ContentType.JSON)
    .when()
        .get()
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", hasSize(2))
        .body("content[0].name", is(entity1.getName()))
        .body("content[0].address", is(entity1.getAddress()))
        .body("content[0].phoneNumber", is(entity1.getPhoneNumber()))
        .body("content[1].name", is(entity2.getName()))
        .body("content[1].address", is(entity2.getAddress()))
        .body("content[1].phoneNumber", is(entity2.getPhoneNumber()))
        .body("page.size", is(20))
        .body("page.number", is(0))
        .body("page.totalElements", is(2))
        .body("page.totalPages", is(1));
  }

  @Test
  void getById_GivenValidId_ShouldReturn200AndApiResponseOfBranchDto() {
    int validId = this.insertSomeDefaultValues().getFirst().getId().intValue();

    given()
        .contentType(ContentType.JSON)
    .when()
        .get("/{id}", validId)
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", notNullValue())
        .body("content.id", is(validId))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Branch retrieved successfully"))
        .body("status", is(200));
  }

  @Test
  void getById_GivenInvalidId_ShouldReturn404AndApiResponseError() {
    int invalidId = 1;
    given()
        .contentType(ContentType.JSON)
    .when()
        .get("/{id}", invalidId)
    .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Branch with id 1 not found"))
        .body("status", is(404));
  }
  @Test
  void create_GivenValidBranchRequestDto_ShouldReturn201AndApiResponseOfBranchDto() {
    BranchRequestDto branchRequestDto = new BranchRequestDto(
        "Branch 1",
        "Address 1",
        "123456789"
    );

    given()
        .contentType(ContentType.JSON)
        .body(branchRequestDto)
    .when()
        .post()
    .then()
        .statusCode(HttpStatus.CREATED.value())
        .body("content", notNullValue())
        .body("content.id", notNullValue())
        .body("content.name", is("Branch 1"))
        .body("content.address", is("Address 1"))
        .body("content.phoneNumber", is("123456789"))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Branch created successfully"))
        .body("status", is(201));
  }

  @Test
  void create_GivenInvalidBranchRequestDto_ShouldReturn500AndApiResponseError() {
    BranchRequestDto branchRequestDto = new BranchRequestDto(
        "",
        "",
        ""
    );

    given()
        .contentType(ContentType.JSON)
        .body(branchRequestDto)
    .when()
        .post()
    .then()
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        //.body("message", is("Branch with id 1 not found"))
        .body("status", is(500));
  }

  @Test
  void update_GivenValidIdAndBranchRequestDto_ShouldReturn200AndApiResponseOfBranchDto() {
    int validId = this.insertSomeDefaultValues().getFirst().getId().intValue();
    BranchRequestDto branchRequestDto = new BranchRequestDto(
        "New Branch 1",
        "New Address 1",
        "123498765"
    );

    given()
        .contentType(ContentType.JSON)
        .body(branchRequestDto)
    .when()
        .put("/{id}", validId)
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", notNullValue())
        .body("content.id", is(validId))
        .body("content.name", is(branchRequestDto.name()))
        .body("content.address", is(branchRequestDto.address()))
        .body("content.phoneNumber", is(branchRequestDto.phoneNumber()))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Branch updated successfully"))
        .body("status", is(200));
  }

  @Test
  void update_GivenInvalidId_ShouldReturn404AndApiResponseError() {
    int invalidId = 1;
    BranchRequestDto branchRequestDto = new BranchRequestDto(
        "New Branch 1",
        "New Address 1",
        "123498765"
    );

    given()
        .contentType(ContentType.JSON)
        .body(branchRequestDto)
    .when()
        .put("/{id}", invalidId)
    .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Branch with id " + invalidId + " not found"))
        .body("status", is(404));

  }

  @Test
  void update_GivenInvalidBranchRequestDto_ShouldReturn500AndApiResponseError() {
    int validId = this.insertSomeDefaultValues().getFirst().getId().intValue();
    BranchRequestDto branchRequestDto = new BranchRequestDto(
        "",
        "",
        ""
    );

    given()
        .contentType(ContentType.JSON)
        .body(branchRequestDto)
    .when()
        .put("/{id}", validId)
    .then()
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        //.body("message", is("Branch with id 1 not found"))
        .body("status", is(500));

  }

  @Test
  void delete_GivenValidId_ShouldReturn204AndNoContent() {
    Branch branch1 = Branch.builder()
        .name("Branch 1")
        .address("Address 1")
        .phoneNumber("123456789")
        .build();
    repo.save(branch1);
    int validId = branch1.getId().intValue();

    given()
        .contentType(ContentType.JSON)
    .when()
        .delete("/{id}", validId)
    .then()
        .statusCode(HttpStatus.NO_CONTENT.value());
  }

  @Test
  void delete_GivenInvalidId_ShouldReturn404AndApiResponseError() {
    int invalidId = 1;
    given()
        .contentType(ContentType.JSON)
    .when()
        .delete("/{id}", invalidId)
    .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Branch with id " + invalidId + " not found"))
        .body("status", is(404));
  }

  private List<Branch> insertSomeDefaultValues() {
    Branch branch1 = Branch.builder()
        .name("Branch 1")
        .address("Address 1")
        .phoneNumber("123456789")
        .build();
    Branch branch2 = Branch.builder()
        .name("Branch 2")
        .address("Address 2")
        .phoneNumber("987654321")
        .build();
   return repo.saveAll(List.of(branch1, branch2));
  }
}