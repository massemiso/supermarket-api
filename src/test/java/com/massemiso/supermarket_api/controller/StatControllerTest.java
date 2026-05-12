package com.massemiso.supermarket_api.controller;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.massemiso.supermarket_api.dto.BestSellerResponseDto;
import com.massemiso.supermarket_api.dto.DetailSaleRequestDto;
import com.massemiso.supermarket_api.dto.SaleRequestDto;
import com.massemiso.supermarket_api.dto.mapper.ProductMapper;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.entity.DetailSale;
import com.massemiso.supermarket_api.entity.Product;
import com.massemiso.supermarket_api.entity.Sale;
import com.massemiso.supermarket_api.repository.BranchRepository;
import com.massemiso.supermarket_api.repository.DetailSaleRepository;
import com.massemiso.supermarket_api.repository.ProductRepository;
import com.massemiso.supermarket_api.repository.SaleRepository;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

class StatControllerTest extends BaseIntegrationTest {

  @Autowired
  private SaleRepository repo;

  @Autowired
  private BranchRepository branchRepo;

  @Autowired
  private ProductRepository productRepo;

  @Autowired
  private DetailSaleRepository detailSaleRepo;

  @Autowired
  private ProductMapper productMapper;

  @BeforeEach
  void setup(){
    RestAssured.baseURI = "http://localhost:" + port + "/api/stats";
    detailSaleRepo.deleteAll();
    repo.deleteAll();
    productRepo.deleteAll();
    branchRepo.deleteAll();
  }

  @Test
  void getBestSellingProduct_ShouldReturn200AndApiResponseOfBestSellerDto(){
    BestSellerResponseDto bestSeller = insertSomeDefaultValues();
    given()
        .header(HttpHeaders.AUTHORIZATION, adminAuthHeader)
        .contentType(ContentType.JSON)
    .when()
        .get("/best-selling-product")
    .then()
        .statusCode(HttpStatus.OK.value())
        .body("content", notNullValue())
        .body("content.product.id", is(bestSeller.product().id().intValue()))
        .body("content.product.name", is(bestSeller.product().name()))
        .body("content.product.actualPrice",
            comparesEqualTo(bestSeller.product().actualPrice()))
        .body("content.totalRevenue",
            comparesEqualTo(bestSeller.totalRevenue()))
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Get best selling product successfully"))
        .body("status", is(200));
  }

  @Test
  void getBestSellingProduct_GivenUserNotAuthenticated_ShouldReturn401Unauthorized(){
    given()
        // given no authentication
        .contentType(ContentType.JSON)
    .when()
        .get("/best-selling-product")
    .then()
        .statusCode(HttpStatus.UNAUTHORIZED.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("Authentication is required to"
            + " perform a GET on /api/stats/best-selling-product"))
        .body("status", is(401));
  }

  @Test
  void getBestSellingProduct_GivenUserNotAuthorized_ShouldReturn403Forbidden(){
    given()
        // cashiers can't get best selling product
        .header(HttpHeaders.AUTHORIZATION, cashierAuthHeader)
        .contentType(ContentType.JSON)
    .when()
        .get("/best-selling-product")
    .then()
        .statusCode(HttpStatus.FORBIDDEN.value())
        .body("content", nullValue())
        .body("timestamp", notNullValue())
        .body("timestamp", containsString(LocalDate.now().toString()))
        .body("message", is("You do not have the required"
            + " permissions to perform a GET on /api/stats/best-selling-product"))
        .body("status", is(403));
  }

  private BestSellerResponseDto insertSomeDefaultValues() {
    Branch branch = branchRepo.save(Branch.builder().build());
    Product p1 = productRepo.save(
        Product.builder()
            .name("P1")
            .actualPrice(BigDecimal.ONE)
            .build()
    );
    Product p2 = productRepo.save(
        Product.builder()
            .name("P2")
            .actualPrice(BigDecimal.TWO)
            .build()
    );

    DetailSale ds1 = DetailSale.builder()
        .quantity(5)
        .product(p2)
        .build();

    DetailSale ds2 = DetailSale.builder()
        .quantity(3)
        .product(p1)
        .build();
    DetailSale ds3 = DetailSale.builder()
        .quantity(1)
        .product(p1)
        .build();

    // 5 * 2.0 = 10.0
    Sale entity1 = Sale.builder()
        .branch(branch)
        .detailSaleList(List.of(ds1))
        .build();

    // (3 * 1.0) + (1 * 1.0) = 4.0
    Sale entity2 = Sale.builder()
        .branch(branch)
        .detailSaleList(List.of(ds2,ds3))
        .build();

    repo.saveAll(List.of(entity1, entity2));
    detailSaleRepo.saveAll(List.of(ds1, ds2, ds3));
    BigDecimal bestSellingProductTotalRevenue = BigDecimal.TEN;

    return new BestSellerResponseDto(productMapper.toDto(p2),
        bestSellingProductTotalRevenue);
  }
}