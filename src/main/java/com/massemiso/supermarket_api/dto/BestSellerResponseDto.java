package com.massemiso.supermarket_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record BestSellerResponseDto(
    ProductResponseDto product,

    @Schema(example = "100.0")
    BigDecimal totalRevenue
){
}
