package com.massemiso.supermarket_api.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.massemiso.supermarket_api.util.TestDataFactory;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DetailSaleTest {

  @Test
  void buildDetailSale_ShouldCreateDetailSale_WithActualPriceFromGivenProduct(){
    // Arrange
    Product product = TestDataFactory.createDefaultProduct();

    // Act
    DetailSale detailSale = DetailSale.builder()
        .quantity(2)
        .product(product)
        .build();

    // Assert
    assertNotNull(detailSale);
    assertEquals(2, detailSale.getQuantity());
    assertEquals(product.getActualPrice(), detailSale.getUnitPrice());

    /* Sale should be the one responsible
     for setting itself in its detail sales */
    assertNull(detailSale.getSale());

    assertEquals(product, detailSale.getProduct());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, -100})
  void buildDetailSale_WhenQuantityIsInvalid_ShouldThrowArithmeticException(int invalidQuantity) {
    Product product = TestDataFactory.createDefaultProduct();

    assertThrows(ArithmeticException.class, () ->
        DetailSale.builder()
            .quantity(invalidQuantity)
            .product(product)
            .build()
    );
  }

  @Test
  void calculateTotal_ShouldReturnCorrectProductOfQuantityAndPrice(){
    // Arrange
    Product product = TestDataFactory.createDefaultProduct();
    DetailSale detailSale = DetailSale.builder()
        .quantity(2)
        .product(product)
        .build();

    // Act
    BigDecimal actual = detailSale.calculateTotal();

    // Assert
    assertNotNull(actual);
    assertThat(actual)
        .isEqualByComparingTo("5.00");
  }

}