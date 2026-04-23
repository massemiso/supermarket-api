package com.massemiso.supermarket_api.dto;

import com.massemiso.supermarket_api.entity.SaleStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SaleResponseDto (
    Long id,
    LocalDate date,
    Long branchId,
    List<DetailSaleResponseDto> detailSaleResponseDtoList,
    SaleStatus saleStatus,
    BigDecimal total
) {

}
