package com.massemiso.supermarket_api.dto;

import java.math.BigDecimal;

public record BestSellerResponseDto(
    ProductResponseDto productResponseDto,
    BigDecimal totalRevenue
){
}
