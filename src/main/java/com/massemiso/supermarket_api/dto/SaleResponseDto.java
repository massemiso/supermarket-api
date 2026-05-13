package com.massemiso.supermarket_api.dto;

import com.massemiso.supermarket_api.entity.SaleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SaleResponseDto (
    @Schema(example = "1")
    Long id,

    @Schema(example = "2026-04-22")
    LocalDate date,

    @Schema(example = "1")
    Long branchId,

    List<DetailSaleResponseDto> detailSaleList,

    @Schema(example = "REGISTERED")
    SaleStatus saleStatus,

   @Schema(example = "22.5")
    BigDecimal total
) {

}
