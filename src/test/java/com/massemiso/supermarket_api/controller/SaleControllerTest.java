package com.massemiso.supermarket_api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.massemiso.supermarket_api.BaseIntegrationTest;
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
import com.massemiso.supermarket_api.util.TestDataFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

class SaleControllerTest extends BaseIntegrationTest {

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
        .header(HttpHeaders.AUTHORIZATION, cashierAuthHeader)
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
        .header(HttpHeaders.AUTHORIZATION, cashierAuthHeader)
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
        .header(HttpHeaders.AUTHORIZATION, cashierAuthHeader)
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
        .header(HttpHeaders.AUTHORIZATION, cashierAuthHeader)
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
  void getAll_GivenUserNotAuthenticated_ShouldReturn401Unauthorized() {
    given()
        // no given user authentication
        .contentType(ContentType.JSON)
    .when()
        .get()
    .then()
        .statusCode(HttpStatus.UNAUTHORIZED.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Authentication is required to"
            + " perform a GET on /api/sales"))
        .body("status", is(401));
  }

  @Test
  void getAll_GivenUserNotAuthorized_ShouldReturn403Forbidden() {
    given()
        // guest users aren't supposed to fetch sales
        .header(HttpHeaders.AUTHORIZATION, guestAuthHeader)
        .contentType(ContentType.JSON)
    .when()
        .get()
    .then()
        .statusCode(HttpStatus.FORBIDDEN.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("You do not have the required"
            + " permissions to perform a GET on /api/sales"))
        .body("status", is(403));
  }

  @Test
  void getById_GivenValidId_ShouldReturn200AndApiResponseOfSaleDto() {
    Sale entity = this.insertSomeDefaultValues().getFirst();
    int validId = entity.getId().intValue();
    int branchId = entity.getBranch().getId().intValue();

    given()
        .header(HttpHeaders.AUTHORIZATION, cashierAuthHeader)
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
        .header(HttpHeaders.AUTHORIZATION, cashierAuthHeader)
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
  void getById_GivenUserNotAuthenticated_ShouldReturn401Unauthorized() {
    Sale entity = this.insertSomeDefaultValues().getFirst();
    int validId = entity.getId().intValue();
    given()
        // no given user authentication
        .contentType(ContentType.JSON)
    .when()
        .get("/{id}", validId)
    .then()
        .statusCode(HttpStatus.UNAUTHORIZED.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Authentication is required to"
            + " perform a GET on /api/sales/" + validId))
        .body("status", is(401));
  }

  @Test
  void getById_GivenUserNotAuthorized_ShouldReturn403Forbidden() {
    Sale entity = this.insertSomeDefaultValues().getFirst();
    int validId = entity.getId().intValue();
    given()
        // guest users aren't supposed to fetch sales
        .header(HttpHeaders.AUTHORIZATION, guestAuthHeader)
        .contentType(ContentType.JSON)
    .when()
        .get("/{id}", validId)
    .then()
        .statusCode(HttpStatus.FORBIDDEN.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("You do not have the required"
            + " permissions to perform a GET on /api/sales/" + validId))
        .body("status", is(403));
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
        .header(HttpHeaders.AUTHORIZATION, cashierAuthHeader)
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
  void create_GivenInvalidSaleRequestDto_ShouldReturn400AndApiResponseError() {
    this.insertSomeDefaultValues();
    SaleRequestDto requestDto = new SaleRequestDto(
        null,
        null
    );

    given()
        .header(HttpHeaders.AUTHORIZATION, cashierAuthHeader)
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
        .body("message", containsString("[branchId] must not be null"))
        .body("message",
            containsString("[detailSaleRequestDtoList] must not be empty"))
        .body("status", is(400));
  }

  @Test
  void create_GivenUserNotAuthenticated_ShouldReturn401Unauthorized(){
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
            + " perform a POST on /api/sales"))
        .body("status", is(401));
  }

  @Test
  void create_GivenUserNotAuthorized_ShouldReturn403Forbidden(){
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
        // Guest does not have permission to create products
        .header(HttpHeaders.AUTHORIZATION, guestAuthHeader)
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
            + " permissions to perform a POST on /api/sales"))
        .body("status", is(403));
  }

  @Test
  void delete_GivenValidId_ShouldReturn204AndNoContent() {
    int validId = this.insertSomeDefaultValues().getFirst().getId().intValue();
    given()
        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader)
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
        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader)
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

  @Test
  void delete_GivenUserNotAuthenticated_ShouldReturn401Unauthorized(){
    int validId = TestDataFactory.getDefaultProductId().intValue();

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
            + " perform a DELETE on /api/sales/" + validId))
        .body("status", is(401));
  }

  @Test
  void delete_GivenUserNotAuthorized_ShouldReturn403Forbidden(){
    int validId = TestDataFactory.getDefaultProductId().intValue();

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
            + " permissions to perform a DELETE on /api/sales/" + validId))
        .body("status", is(403));
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

    return repo.saveAll(List.of(entity1, entity2));
  }
}