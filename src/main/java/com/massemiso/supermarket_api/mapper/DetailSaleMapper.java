package com.massemiso.supermarket_api.mapper;

import com.massemiso.supermarket_api.dto.DetailSaleRequestDto;
import com.massemiso.supermarket_api.dto.DetailSaleResponseDto;
import com.massemiso.supermarket_api.entity.DetailSale;
import com.massemiso.supermarket_api.entity.Product;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DetailSaleMapper {
  DetailSaleMapper INSTANCE = Mappers.getMapper(DetailSaleMapper.class);

  @Mapping(target = "productId", source = "product.id")
  public DetailSaleResponseDto toDto(DetailSale entity);

  @Mapping(target = "product", source = "product")
  public DetailSale toEntity(DetailSaleRequestDto requestDto, Product product);

  public List<DetailSaleResponseDto> toDtoList(List<DetailSale> detailSaleList);
}
