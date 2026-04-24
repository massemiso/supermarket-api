package com.massemiso.supermarket_api.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.massemiso.supermarket_api.util.TestDataFactory;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProductTest {

  @Test
  void buildProduct_WhenGivenValidArgs_ShouldCreateProduct() {
    // Arrange

    // Act
    Product actual = Product.builder()
        .name("New Product")
        .category("New Product Category")
        .actualPrice(BigDecimal.valueOf(2))
        .build();

    // Assert
    assertNotNull(actual);
    assertEquals("New Product", actual.getName());
    assertEquals("New Product Category", actual.getCategory());
    assertThat(actual.getActualPrice())
        .isEqualByComparingTo(BigDecimal.valueOf(2));
    assertNull(actual.getDetailSaleList());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, -100})
  void buildProduct_WhenGivenInvalidActualPrice_ShouldThrowArithmeticException(int invalidActualPrice) {
    assertThrows(ArithmeticException.class, () ->
      Product.builder()
          .name("New Product")
          .category("New Product Category")
          .actualPrice(BigDecimal.valueOf(invalidActualPrice))
          .build()
    );
  }

  @Test
  void update_WhenGivenValidArgs_ShouldSetArgsCorrectly() {
    // Arrange
    Product product = TestDataFactory.createDefaultProduct();

    // Act
    product.update(
        "New name",
        "New category",
        BigDecimal.valueOf(1)
    );

    // Assert
    assertNotNull(product);
    assertEquals("New name", product.getName());
    assertEquals("New category", product.getCategory());
    assertThat(product.getActualPrice())
        .isEqualByComparingTo(BigDecimal.valueOf(1));
  }

  @Test
  void update_WhenGivenNullArgs_ShouldKeepOldData() {
    // Arrange
    Product product = TestDataFactory.createDefaultProduct();
    String oldName = product.getName();
    String oldCategory = product.getCategory();
    BigDecimal oldActualPrice = product.getActualPrice();

    // Act
    product.update(
        null,
        null,
        null
    );

    // Assert
    assertNotNull(product);
    assertEquals(oldName, product.getName());
    assertEquals(oldCategory, product.getCategory());
    assertThat(product.getActualPrice())
        .isEqualByComparingTo(oldActualPrice);
  }

  @Test
  void update_WhenGivenSomeNullArgs_ShouldKeepOldDataAndSetValidArgsCorrectly() {
    // Arrange
    Product product = TestDataFactory.createDefaultProduct();
    String oldCategory = product.getCategory();
    BigDecimal oldActualPrice = product.getActualPrice();

    // Act
    product.update(
        "New name",
        null,
        null
    );

    // Assert
    assertNotNull(product);
    assertEquals("New name", product.getName());
    assertEquals(oldCategory, product.getCategory());
    assertThat(product.getActualPrice())
        .isEqualByComparingTo(oldActualPrice);
  }

}