package com.massemiso.supermarket_api.controller;

import static io.restassured.RestAssured.*;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.hamcrest.Matchers.*;

import com.massemiso.supermarket_api.dto.DetailSaleRequestDto;
import com.massemiso.supermarket_api.dto.SaleRequestDto;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.entity.DetailSale;
import com.massemiso.supermarket_api.entity.Product;
import com.massemiso.supermarket_api.entity.Sale;
import com.massemiso.supermarket_api.repository.BranchRepository;
import com.massemiso.supermarket_api.repository.DetailSaleRepository;
import com.massemiso.supermarket_api.repository.ProductRepository;
import com.massemiso.supermarket_api.repository.SaleRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SaleControllerTest {

  @LocalServerPort
  private Integer port;

  static PostgreSQLContainer postgres = new PostgreSQLContainer(
      "postgres:15"
  );

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private SaleRepository repo;

  @Autowired
  private BranchRepository branchRepo;

  @Autowired
  private ProductRepository productRepo;

  @Autowired
  private DetailSaleRepository detailSaleRepo;

  @BeforeEach
  void setup(){
    RestAssured.config = RestAssured.config()
        .jsonConfig(jsonConfig()
            .numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
    RestAssured.baseURI = "http://localhost:" + port + "/api/sales";
    detailSaleRepo.deleteAll();
    repo.deleteAll();
    branchRepo.deleteAll();
    productRepo.deleteAll();
  }

  @Test
  void getAll_ShouldReturn200AndAJsonWithAPageOfSales() {
    List<Sale> entities = this.insertSomeDefaultValues();
    Sale entity1 = entities.getFirst();
    Sale entity2 = entities.getLast();

    given()
        .port(port)
        .contentType(ContentType.JSON)
    .when()
        .get()
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", hasSize(2))
        .body("content[0].id",
            is(entity1.getId().intValue()))
        .body("content[0].branchId",
            is(entity1.getBranch().getId().intValue()))
        .body("content[0].detailSaleList", hasSize(1))
        .body("content[1].id",
            is(entity2.getId().intValue()))
        .body("content[1].branchId",
            is(entity2.getBranch().getId().intValue()))
        .body("content[1].detailSaleList", hasSize(2))
        .body("page.size", is(20))
        .body("page.number", is(0))
        .body("page.totalElements", is(2))
        .body("page.totalPages", is(1));
  }

  @Test
  void getAll_GivenBranchId_ShouldReturn200AndAJsonWithAPageOfSalesFilteredByBranchId() {
    int branchId = this.insertSomeDefaultValues()
        .getFirst()
        .getBranch()
        .getId()
        .intValue();

    given()
        .port(port)
        .contentType(ContentType.JSON)
        .queryParam("branchId", branchId)
    .when()
        .get()
    .then()
        .statusCode(200)
        .body("content", hasSize(1))
        .body("content[0].branchId", is(branchId))
        .body("page.size", is(20))
        .body("page.number", is(0))
        .body("page.totalElements", is(1))
        .body("page.totalPages", is(1));
  }

  @Test
  void getAll_GivenDate_ShouldReturn200AndAJsonWithAPageOfSalesFilteredByDate() {
    List<Sale> entities = this.insertSomeDefaultValues();
    Sale entity = entities.getLast();
    ReflectionTestUtils.setField(entity,
        "date",
        LocalDate.of(2020, 1, 1)
    );
    repo.save(entity);

    given()
        .port(port)
        .contentType(ContentType.JSON)
        .queryParam("date", LocalDate.now().toString())
    .when()
        .get()
    .then()
        .statusCode(200)
        .body("content", hasSize(1))
        .body("content[0].date", is(LocalDate.now().toString()))
        .body("page.size", is(20))
        .body("page.number", is(0))
        .body("page.totalElements", is(1))
        .body("page.totalPages", is(1));
  }

  @Test
  void getAll_GivenBranchIdAndDate_ShouldReturn200AndAJsonWithAPageOfSalesFilteredByBranchIdAndDate() {
    List<Sale> entities = this.insertSomeDefaultValues();
    int branchId = entities
        .getFirst()
        .getBranch()
        .getId()
        .intValue();
    Sale entity = entities.getLast();
    ReflectionTestUtils.setField(entity,
        "date",
        LocalDate.of(2020, 1, 1)
    );
    repo.save(entity);

    given()
        .port(port)
        .contentType(ContentType.JSON)
        .queryParam("branchId", branchId)
        .queryParam("date", LocalDate.now().toString())
    .when()
        .get()
    .then()
        .statusCode(200)
        .body("content", hasSize(1))
        .body("content[0].branchId", is(branchId))
        .body("content[0].date", is(LocalDate.now().toString()))
        .body("page.size", is(20))
        .body("page.number", is(0))
        .body("page.totalElements", is(1))
        .body("page.totalPages", is(1));
  }
  @Test
  void getById_GivenValidId_ShouldReturn200AndApiResponseOfSaleDto() {
    Sale entity = this.insertSomeDefaultValues().getFirst();
    int validId = entity.getId().intValue();
    int branchId = entity.getBranch().getId().intValue();

    given()
        .port(port)
        .contentType(ContentType.JSON)
    .when()
        .get("/{id}", validId)
     .then()
        .statusCode(200)
        .body("content", notNullValue())
        .body("content.id", is(validId))
        .body("content.date", is(LocalDate.now().toString()))
        .body("content.branchId", is(branchId))
        .body("content.detailSaleList", hasSize(1))
        .body("content.saleStatus", is("REGISTERED"))
        .body("content.total",
            comparesEqualTo(entity.getTotal()))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Sale retrieved successfully"))
        .body("status", is(200));
  }

  @Test
  void getById_GivenInvalidId_ShouldReturn404AndApiResponseError() {
    int invalidId = -1;
    given()
        .port(port)
        .contentType(ContentType.JSON)
    .when()
        .get("/{id}", invalidId)
    .then()
        .statusCode(404)
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Sale with id " + invalidId + " not found"))
        .body("status", is(404));
  }
  @Test
  void create_GivenValidSaleRequestDto_ShouldReturn201AndApiResponseOfSaleDto() {
    List<Sale> entities = this.insertSomeDefaultValues();
    Long validBranchId = entities
        .getLast()
        .getBranch()
        .getId();
    Long validProductId = entities
        .getLast()
        .getDetailSaleList()
        .getFirst()
        .getProduct()
        .getId();
    SaleRequestDto requestDto = new SaleRequestDto(
      validBranchId,
      List.of(
          new DetailSaleRequestDto(1, validProductId),
          new DetailSaleRequestDto(3, validProductId)
      )
    );

    given()
        .port(port)
        .contentType(ContentType.JSON)
        .body(requestDto)
    .when()
        .post()
    .then()
        .statusCode(201)
        .body("content", notNullValue())
        .body("content.id", notNullValue())
        .body("content.date", is(LocalDate.now().toString()))
        .body("content.branchId", is(validBranchId.intValue()))
        .body("content.detailSaleList", hasSize(2))
        .body("content.saleStatus", is("REGISTERED"))
        .body("content.total", notNullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Sale created successfully"))
        .body("status", is(201));
  }

  @Test
  void create_GivenInvalidSaleRequestDto_ShouldReturn500AndApiResponseError() {
    this.insertSomeDefaultValues();
    SaleRequestDto requestDto = new SaleRequestDto(
        null,
        null
    );

    given()
        .port(port)
        .contentType(ContentType.JSON)
        .body(requestDto)
    .when()
        .post()
        .then()
    .statusCode(500)
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", notNullValue())
        .body("status", is(500));
  }

  @Test
  void delete_GivenValidId_ShouldReturn204AndNoContent() {
    int validId = this.insertSomeDefaultValues().getFirst().getId().intValue();
    given()
        .port(port)
        .contentType(ContentType.JSON)
    .when()
        .delete("/{id}", validId)
    .then()
        .statusCode(204);
  }

  @Test
  void delete_GivenInvalidId_ShouldReturn404AndApiResponseError() {
    this.insertSomeDefaultValues();
    int invalidId = -1;
    given()
        .port(port)
        .contentType(ContentType.JSON)
    .when()
        .delete("/{id}", invalidId)
    .then()
        .statusCode(404)
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Sale with id " + invalidId + " not found"))
        .body("status", is(404));
  }

  private List<Sale> insertSomeDefaultValues() {
    Branch branch1 =  branchRepo.save(Branch.builder().name("B1").build());
    Branch branch2 =  branchRepo.save(Branch.builder().name("B2").build());
    Product product = productRepo.save(
            Product.builder()
                .name("P1")
                .actualPrice(BigDecimal.ONE)
                .build()
    );

    DetailSale ds1 = DetailSale.builder()
        .quantity(5)
        .product(product)
        .build();
    DetailSale ds2 = DetailSale.builder()
        .quantity(5)
        .product(product)
        .build();
    DetailSale ds3 = DetailSale.builder()
        .quantity(1)
        .product(product)
        .build();

    Sale entity1 = Sale.builder()
        .branch(branch1)
        .detailSaleList(List.of(ds1))
        .build();

    Sale entity2 = Sale.builder()
        .branch(branch2)
        .detailSaleList(List.of(ds2,ds3))
        .build();

    List<Sale> out = repo.saveAll(List.of(entity1, entity2));
    detailSaleRepo.saveAll(List.of(ds1, ds2, ds3));
    return out;
  }
}