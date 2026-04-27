package com.massemiso.supermarket_api.util;

import com.massemiso.supermarket_api.dto.BranchRequestDto;
import com.massemiso.supermarket_api.dto.BranchResponseDto;
import com.massemiso.supermarket_api.dto.DetailSaleResponseDto;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.entity.DetailSale;
import com.massemiso.supermarket_api.entity.Product;
import java.math.BigDecimal;

public class TestDataFactory {
  private final static Long DEFAULT_BRANCH_ID = 1L;
  private final static String DEFAULT_BRANCH_NAME = "Some branch name";
  private final static String DEFAULT_BRANCH_ADDRESS  = "Some branch address";
  private final static String DEFAULT_BRANCH_PHONENUMBER  = "123456789" ;

  public static Branch createDefaultBranch() {
    return Branch.builder()
        .name(DEFAULT_BRANCH_NAME)
        .address(DEFAULT_BRANCH_ADDRESS)
        .phoneNumber(DEFAULT_BRANCH_PHONENUMBER)
        .build();
  }
  public static BranchRequestDto createDefaultBranchRequestDto() {
    return new BranchRequestDto(
        DEFAULT_BRANCH_NAME,
        DEFAULT_BRANCH_ADDRESS,
        DEFAULT_BRANCH_PHONENUMBER);
  }

  public static BranchResponseDto createDefaultBranchResponseDto() {
    return new BranchResponseDto(
        DEFAULT_BRANCH_ID,
        DEFAULT_BRANCH_NAME,
        DEFAULT_BRANCH_ADDRESS,
        DEFAULT_BRANCH_PHONENUMBER);
  }

  public static Product createDefaultProduct() {
    return Product.builder()
        .name("Some product name")
        .category("Some category")
        .actualPrice(BigDecimal.valueOf(2.50))
        .build();
  }

  public static DetailSale createDefaultDetailSale() {
    return DetailSale.builder()
        .quantity(5)
        .product(createDefaultProduct())
        .build();
  }

  public static Long getDefaultBranchId() {
    return DEFAULT_BRANCH_ID;
  }
}
