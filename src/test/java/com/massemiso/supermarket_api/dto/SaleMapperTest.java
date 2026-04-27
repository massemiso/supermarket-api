package com.massemiso.supermarket_api.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.massemiso.supermarket_api.dto.mapper.SaleMapper;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.entity.DetailSale;
import com.massemiso.supermarket_api.entity.Sale;
import com.massemiso.supermarket_api.entity.SaleStatus;
import com.massemiso.supermarket_api.util.TestDataFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class SaleMapperTest {

  @Test
  void toDto_GivenAValidSaleAndAListOfDetailSaleResponseDto_ShouldReturnCorrectSaleResponseDto() {
    // arrange
    Branch branch = TestDataFactory.createDefaultBranch();

    List<DetailSale> detailSaleList = new ArrayList<>();
    List<DetailSaleResponseDto> detailSaleResponseDtoList = new ArrayList<>();
    for (int i = 0;  i < 5;  i++) {
      DetailSale detailSale = TestDataFactory.createDefaultDetailSale();
      ReflectionTestUtils.setField(detailSale, "id", (long) (i + 1));
      detailSaleList.add(detailSale);

      // Instead of using the real DetailSaleMapper:
      DetailSaleResponseDto dummyDto = new DetailSaleResponseDto(
          detailSale.getId(),
          detailSale.getQuantity(),
          detailSale.getUnitPrice(),
          detailSale.getProduct().getId());
      detailSaleResponseDtoList.add(dummyDto);
    }

    Sale entity = Sale.builder()
        .branch(branch)
        .detailSaleList(detailSaleList)
        .build();
   SaleMapper mapper = new SaleMapper();

    // act
    SaleResponseDto dto = mapper.toDto(entity, detailSaleResponseDtoList);

    // assert
    assertNotNull(dto);
    assertEquals(entity.getId(), dto.id());
    assertEquals(entity.getDate(), dto.date());
    assertEquals(entity.getBranch().getId(), dto.branchId());
    assertThat(dto.detailSaleList())
        .isNotEmpty()
        .hasSize(detailSaleResponseDtoList.size())
        .containsExactlyInAnyOrderElementsOf(detailSaleResponseDtoList);
    assertEquals(entity.getSaleStatus(), dto.saleStatus());
    assertThat(entity.getTotal()).isEqualByComparingTo(dto.total());
  }

  @Test
  void toEntity_GivenAValidBranchAndAListOfDetailSale_ShouldReturnACorrectSale() {
    // arrange
    Branch branch = TestDataFactory.createDefaultBranch();
    List<DetailSale> detailSaleList = new ArrayList<>();

    for(int i = 0; i < 5; i++){
      DetailSale detailSale = TestDataFactory.createDefaultDetailSale();
      ReflectionTestUtils.setField(detailSale, "id", (long) (i + 1));
      detailSaleList.add(detailSale);
    }

    BigDecimal actualTotal = detailSaleList.stream()
        .map(DetailSale::calculateTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    SaleMapper mapper = new SaleMapper();

    // act
    Sale entity = mapper.toEntity(branch, detailSaleList);

    // assert
    assertNotNull(entity);
    assertNull(entity.getId()); // JPA handle id generation
    assertEquals(LocalDate.now(), entity.getDate());
    assertEquals(SaleStatus.REGISTERED, entity.getSaleStatus());
    assertThat(entity.getTotal())
        .isEqualByComparingTo(actualTotal);
    assertEquals(branch, entity.getBranch());
    assertThat(entity.getDetailSaleList())
        .isNotEmpty()
        .hasSize(detailSaleList.size())
        .containsExactlyInAnyOrderElementsOf(detailSaleList);
  }
}