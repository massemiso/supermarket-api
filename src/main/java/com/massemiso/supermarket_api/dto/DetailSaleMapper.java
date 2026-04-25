package com.massemiso.supermarket_api.dto;

import com.massemiso.supermarket_api.entity.DetailSale;
import com.massemiso.supermarket_api.entity.Product;
import com.massemiso.supermarket_api.entity.Sale;
import com.massemiso.supermarket_api.exception.ProductNotFoundException;
import com.massemiso.supermarket_api.repository.ProductRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DetailSaleMapper {

  public DetailSaleResponseDto toDto(DetailSale entity) {
    return new DetailSaleResponseDto(
        entity.getId(),
        entity.getQuantity(),
        entity.getUnitPrice(),
        entity.getProduct().getId()
    );
  }

  public DetailSale toEntity(DetailSaleRequestDto requestDto, Product product) {
    return DetailSale.builder()
        .quantity(requestDto.quantity())
        .product(product)
        .build();
  }

  public List<DetailSaleResponseDto> getDetailSaleListDto(List<DetailSale> detailSaleList){
    if (detailSaleList == null)
      return List.of();
    return detailSaleList.stream()
        .map(this::toDto)
        .toList();
  }
}
