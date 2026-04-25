package com.massemiso.supermarket_api.dto;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import com.massemiso.supermarket_api.entity.DetailSale;
import com.massemiso.supermarket_api.entity.Product;
import com.massemiso.supermarket_api.util.TestDataFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DetailSaleMapperTest {

  @Test
  void toDto_GivenValidDetailSale_ShouldReturnCorrectDetailSaleResponseDto() {
    // arrange
    DetailSale entity = TestDataFactory.createDefaultDetailSale();
    ReflectionTestUtils.setField(entity, "id", 1L);
    DetailSaleMapper mapper = new DetailSaleMapper();

    // act
    DetailSaleResponseDto dto = mapper.toDto(entity);

    // assert
    assertNotNull(dto);
    assertEquals(entity.getId(), dto.id());
    assertEquals(entity.getQuantity(), dto.quantity());
    assertEquals(entity.getUnitPrice(), dto.unitPrice());
    assertEquals(entity.getProduct().getId(), dto.productId());
  }

  @Test
  void toEntity_GivenValidDetailSaleRequestDtoAndProduct_ShouldReturnCorrectDetailSale() {
    // arrange
    Product product = TestDataFactory.createDefaultProduct();
    DetailSaleRequestDto dto = new DetailSaleRequestDto(
        5,
        product.getId()
    );
    DetailSaleMapper mapper = new DetailSaleMapper();

    // act
    DetailSale entity = mapper.toEntity(dto, product);

    // assert
    assertNotNull(entity);
    assertNull(entity.getId());
    assertEquals(dto.productId(), entity.getProduct().getId());
    assertEquals(dto.quantity(), entity.getQuantity());
    assertNull(entity.getSale()); // Sale is responsible for setting itself.
    assertEquals(product, entity.getProduct());
  }

  @Test
  void getDetailSaleListDto_GivenAValidListOfDetailSale_ShouldReturnACorrectListOfDetailSaleResponseDto() {
    // arrange
    List<DetailSale> detailSaleList = new ArrayList<>();
    List<DetailSaleResponseDto> expectedDtoList = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      DetailSale detailSale = TestDataFactory.createDefaultDetailSale();
      ReflectionTestUtils.setField(detailSale, "id", (long) (i + 1));
      detailSaleList.add(detailSale);

      expectedDtoList.add(
          new DetailSaleResponseDto(
              detailSale.getId(),
              detailSale.getQuantity(),
              detailSale.getUnitPrice(),
              detailSale.getProduct().getId()
          )
      );
    }
    DetailSaleMapper mapper = new DetailSaleMapper();

    // act
    List<DetailSaleResponseDto> dtoList = mapper.getDetailSaleListDto(detailSaleList);

    // assert
    assertNotNull(dtoList);
    assertThat(dtoList)
        .hasSize(detailSaleList.size())
        .containsExactlyInAnyOrderElementsOf(expectedDtoList);
  }

  @Test
  void getDetailSaleListDto_GivenNullList_ShouldReturnAnEmptyListOfDetailSaleResponseDto() {
    // arrange
    DetailSaleMapper mapper = new DetailSaleMapper();

    // act
    List<DetailSaleResponseDto> dtoList = mapper.getDetailSaleListDto(null);

    // assert
    assertNotNull(dtoList);
    assertThat(dtoList)
        .isEmpty();
  }

}