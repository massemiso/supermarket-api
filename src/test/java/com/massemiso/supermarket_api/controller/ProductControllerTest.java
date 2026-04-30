package com.massemiso.supermarket_api.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.massemiso.supermarket_api.dto.ProductRequestDto;
import com.massemiso.supermarket_api.entity.Product;
import com.massemiso.supermarket_api.repository.ProductRepository;
import com.massemiso.supermarket_api.BaseIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class ProductControllerTest extends BaseIntegrationTest {
  @Autowired
  private ProductRepository repo;

  @BeforeEach
  void setup(){
    RestAssured.config = RestAssured.config()
        .jsonConfig(jsonConfig()
            .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
    RestAssured.baseURI = "http://localhost:" + port + "/api/products";
    repo.deleteAll();
  }

  @Test
  void getAll_ShouldReturn200AndAJsonWithAPageOfBranches() {
    List<Product> entities = this.insertSomeDefaultValues();
    Product entity1 = entities.getFirst();
    Product entity2 = entities.getLast();

    given()
        .port(port)
        .contentType(ContentType.JSON)
    .when()
        .get()
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", hasSize(2))
        .body("content[0].name", is(entity1.getName()))
        .body("content[0].category", is(entity1.getCategory()))
        .body("content[0].actualPrice",
            comparesEqualTo(entity1.getActualPrice()))
        .body("content[1].name", is(entity2.getName()))
        .body("content[1].category", is(entity2.getCategory()))
        .body("content[1].actualPrice",
            comparesEqualTo(entity2.getActualPrice()))
        .body("page.size", is(20))
        .body("page.number", is(0))
        .body("page.totalElements", is(2))
        .body("page.totalPages", is(1));
  }

  @Test
  void getById_GivenValidId_ShouldReturn200AndApiResponseOfBranchDto() {
    List<Product> entities = this.insertSomeDefaultValues();
    Product entity1 = entities.getFirst();
    int validId = entity1.getId().intValue();

    given()
        .port(port)
        .contentType(ContentType.JSON)
    .when()
        .get("/{id}", validId)
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", notNullValue())
        .body("content.id", is(validId))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Product retrieved successfully"))
        .body("status", is(200));
  }

  @Test
  void getById_GivenInvalidId_ShouldReturn404AndApiResponseError() {
    int invalidId = 1;
    given()
        .port(port)
        .contentType(ContentType.JSON)
    .when()
        .get("/{id}", invalidId)
    .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Product with id 1 not found"))
        .body("status", is(404));
  }
  @Test
  void create_GivenValidBranchRequestDto_ShouldReturn201AndApiResponseOfBranchDto() {
    ProductRequestDto dto = new ProductRequestDto(
        "Product 1",
        "Category 1",
        BigDecimal.ONE
    );

    given()
        .port(port)
        .contentType(ContentType.JSON)
        .body(dto)
    .when()
        .post()
    .then()
        .statusCode(HttpStatus.CREATED.value())
        .body("content", notNullValue())
        .body("content.id", notNullValue())
        .body("content.name", is(dto.name()))
        .body("content.category", is(dto.category()))
        .body("content.actualPrice",
            comparesEqualTo(dto.actualPrice().intValue()))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Product created successfully"))
        .body("status", is(201));
  }

  @Test
  void create_GivenInvalidBranchRequestDto_ShouldReturn500AndApiResponseError() {
    ProductRequestDto dto = new ProductRequestDto(
        "",
        "",
        null
    );

    given()
        .port(port)
        .contentType(ContentType.JSON)
        .body(dto)
    .when()
        .post()
    .then()
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("status", is(500));
  }

  @Test
  void update_GivenValidIdAndBranchRequestDto_ShouldReturn200AndApiResponseOfBranchDto() {
    List<Product> entities = this.insertSomeDefaultValues();
    Product entity1 = entities.getFirst();
    int validId = entity1.getId().intValue();

    ProductRequestDto dto = new ProductRequestDto(
        "New Product 1",
        "New Category 1",
        BigDecimal.TWO
    );

    given()
        .port(port)
        .contentType(ContentType.JSON)
        .body(dto)
    .when()
        .put("/{id}", validId)
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", notNullValue())
        .body("content.id", is(validId))
        .body("content.name", is(dto.name()))
        .body("content.category", is(dto.category()))
        .body("content.actualPrice",
            comparesEqualTo(dto.actualPrice().intValue()))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Product updated successfully"))
        .body("status", is(200));
  }

  @Test
  void update_GivenInvalidId_ShouldReturn404AndApiResponseError() {
    int invalidId = 1;
    ProductRequestDto dto = new ProductRequestDto(
        "New Product 1",
        "New Category 1",
        BigDecimal.TWO
    );

    given()
        .port(port)
        .contentType(ContentType.JSON)
        .body(dto)
    .when()
        .put("/{id}", invalidId)
    .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Product with id " + invalidId + " not found"))
        .body("status", is(404));

  }

  @Test
  void update_GivenInvalidBranchRequestDto_ShouldReturn500AndApiResponseError() {
    List<Product> entities = this.insertSomeDefaultValues();
    Product entity1 = entities.getFirst();
    int validId = entity1.getId().intValue();

    ProductRequestDto dto = new ProductRequestDto(
        "",
        "",
        null
    );

    given()
        .port(port)
        .contentType(ContentType.JSON)
        .body(dto)
    .when()
        .put("/{id}", validId)
    .then()
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("status", is(500));

  }

  @Test
  void delete_GivenValidId_ShouldReturn204AndNoContent() {
    List<Product> entities = this.insertSomeDefaultValues();
    Product entity1 = entities.getFirst();
    int validId = entity1.getId().intValue();

    given()
        .port(port)
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
        .port(port)
        .contentType(ContentType.JSON)
    .when()
        .delete("/{id}", invalidId)
    .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Product with id " + invalidId + " not found"))
        .body("status", is(404));
  }

  private List<Product> insertSomeDefaultValues() {
    Product entity1 = Product.builder()
        .name("Product 1")
        .category("Category 1")
        .actualPrice(BigDecimal.ONE)
        .build();
    Product entity2 = Product.builder()
        .name("Product 2")
        .category("Category 2")
        .actualPrice(BigDecimal.TWO)
        .build();
    return repo.saveAll(List.of(entity1, entity2));
  }
}