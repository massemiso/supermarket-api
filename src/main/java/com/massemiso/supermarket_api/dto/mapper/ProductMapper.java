package com.massemiso.supermarket_api.dto.mapper;

import com.massemiso.supermarket_api.dto.ProductRequestDto;
import com.massemiso.supermarket_api.dto.ProductResponseDto;
import com.massemiso.supermarket_api.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
  public ProductResponseDto toDto(Product product){
    return new ProductResponseDto(
        product.getId(),
        product.getName(),
        product.getCategory(),
        product.getActualPrice()
    );
  }

  public Product toEntity(ProductRequestDto requestDto) {
    return Product.builder()
        .name(requestDto.name())
        .category(requestDto.category())
        .actualPrice(requestDto.actualPrice())
        .build();
  }
}
