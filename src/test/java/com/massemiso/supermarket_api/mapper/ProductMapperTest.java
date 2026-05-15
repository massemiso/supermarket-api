package com.massemiso.supermarket_api.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.massemiso.supermarket_api.dto.ProductRequestDto;
import com.massemiso.supermarket_api.dto.ProductResponseDto;
import com.massemiso.supermarket_api.entity.Product;
import com.massemiso.supermarket_api.util.TestDataFactory;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

class ProductMapperTest {
  private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

  @Test
  void toDto_GivenValidProduct_ShouldReturnCorrectProductResponseDto() {
    // Arrange
    Product entity = TestDataFactory.createDefaultProduct();

    // manually set the id field even if it's private and has no setter
    ReflectionTestUtils.setField(entity, "id", 1L);

    // Act
    ProductResponseDto dto = mapper.toDto(entity);

    // Assert
    assertNotNull(dto);
    assertEquals(entity.getId(), dto.id());
    assertEquals(entity.getName(), dto.name());
    assertEquals(entity.getCategory(), dto.category());
    assertThat(entity.getActualPrice()).isEqualByComparingTo(dto.actualPrice());
  }

  @Test
  void toEntity_GivenValidProductRequestDto_ShouldReturnCorrectProduct() {
    // Arrange
    ProductRequestDto dto = new ProductRequestDto(
      "Some product",
      "Some category",
      BigDecimal.ZERO
    );

    // Act
    Product entity = mapper.toEntity(dto);

    // Assert
    assertNotNull(entity);
    assertNull(entity.getId());
    assertEquals(dto.name(), entity.getName());
    assertEquals(dto.category(), entity.getCategory());
    assertThat(dto.actualPrice()).isEqualByComparingTo(entity.getActualPrice());
    assertNull(entity.getDetailSaleList());
  }
}