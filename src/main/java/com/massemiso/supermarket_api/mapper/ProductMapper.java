package com.massemiso.supermarket_api.mapper;

import com.massemiso.supermarket_api.dto.ProductRequestDto;
import com.massemiso.supermarket_api.dto.ProductResponseDto;
import com.massemiso.supermarket_api.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  public ProductResponseDto toDto(Product product);
  public Product toEntity(ProductRequestDto requestDto);
}
