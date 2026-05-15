package com.massemiso.supermarket_api.dto.mapper;

import com.massemiso.supermarket_api.dto.DetailSaleResponseDto;
import com.massemiso.supermarket_api.dto.SaleResponseDto;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.entity.DetailSale;
import com.massemiso.supermarket_api.entity.Sale;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {

  @Mapping(target = "detailSaleList", source = "detailSaleResponseDtoList")
  @Mapping(target = "branchId", source = "entity.branch.id")
  public SaleResponseDto toDto(
      Sale entity, List<DetailSaleResponseDto> detailSaleResponseDtoList);

  public Sale toEntity(Branch branch, List<DetailSale> detailSaleList);
}
