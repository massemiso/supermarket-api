package com.massemiso.supermarket_api.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.massemiso.supermarket_api.util.TestDataFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class SaleTest {

  @Test
  void deleteSale_ShouldSetDeletedAtAndSaleStatusToAnnulled() {
    // Arrange
    Branch branch = TestDataFactory.createDefaultBranch();
    Product product = TestDataFactory.createDefaultProduct();
    DetailSale item1 = DetailSale.builder()
        .quantity(2)
        .product(product)
        .build();
    DetailSale item2 = DetailSale.builder()
        .quantity(1)
        .product(product)
        .build();
    List<DetailSale> detailSaleList = new ArrayList<>();
    detailSaleList.add(item1);
    detailSaleList.add(item2);
    Sale sale = Sale.builder()
        .branch(branch)
        .detailSaleList(detailSaleList)
        .build();

    // Act
    sale.delete();

    // Assert
    assertNotNull(sale.getDeletedAt());
    assertThat(sale.getDeletedAt())
        .isBeforeOrEqualTo(LocalDateTime.now());
    assertEquals(SaleStatus.ANNULLED, sale.getSaleStatus());
  }

  @Test
  void saleBuilder_ShouldCalculateCorrectTotal() {
    // Arrange
    Branch branch = TestDataFactory.createDefaultBranch();
    Product product = TestDataFactory.createDefaultProduct();

    // total item 1: 2 * 2.50 = 5.0
    DetailSale item1 = DetailSale.builder()
        .quantity(2)
        .product(product)
        .build();
    // total item 2: 1 * 2.50 = 2.50
    DetailSale item2 = DetailSale.builder()
        .quantity(1)
        .product(product)
        .build();
    List<DetailSale> detailSaleList = new ArrayList<>();
    detailSaleList.add(item1);
    detailSaleList.add(item2);
    // total detailSaleList: 5.0 + 2.50 = 7.50

    // Act
    Sale actual = Sale.builder()
        .branch(branch)
        .detailSaleList(detailSaleList)
        .build();

    // Assert
    assertNotNull(actual);
    assertEquals(LocalDate.now(), actual.getDate());
    assertEquals(branch, actual.getBranch());

    assertThat(actual.getDetailSaleList())
        .hasSize(2)
        .containsExactlyInAnyOrder(item1, item2)
        .isNotEmpty()
        .allSatisfy(detail ->
            assertThat(detail.getSale())
                .isEqualTo(actual));

    assertEquals(SaleStatus.REGISTERED, actual.getSaleStatus());
    assertThat(actual.getTotal())
        .isEqualByComparingTo(BigDecimal.valueOf(7.50));
  }

  @Test
  void saleBuilder_WithNullOrEmptyList_ShouldThrowIllegalArgumentException() {
    Branch branch = TestDataFactory.createDefaultBranch();

    assertThrows(IllegalArgumentException.class, () ->
        Sale.builder()
            .branch(branch)
            .detailSaleList(null)
            .build());
    assertThrows(IllegalArgumentException.class, () ->
        Sale.builder()
            .branch(branch)
            .detailSaleList(List.of())
            .build());
  }
}