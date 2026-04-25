package com.massemiso.supermarket_api.dto.mapper;

import com.massemiso.supermarket_api.dto.DetailSaleResponseDto;
import com.massemiso.supermarket_api.dto.SaleResponseDto;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.entity.DetailSale;
import com.massemiso.supermarket_api.entity.Sale;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SaleMapper {

  public SaleResponseDto toDto(
      Sale entity,
      List<DetailSaleResponseDto> detailSaleResponseDtoList) {
    return new SaleResponseDto(
        entity.getId(),
        entity.getDate(),
        entity.getBranch().getId(),
        detailSaleResponseDtoList,
        entity.getSaleStatus(),
        entity.getTotal()
    );
  }

  public Sale toEntity(
      Branch branch,
      List<DetailSale> detailSaleList) {
    // ASSUME: user can't update sales
    return Sale.builder()
        .branch(branch)
        .detailSaleList(detailSaleList)
        .build();
  }
}
