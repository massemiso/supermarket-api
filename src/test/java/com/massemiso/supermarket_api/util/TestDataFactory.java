package com.massemiso.supermarket_api.util;

import com.massemiso.supermarket_api.dto.BranchRequestDto;
import com.massemiso.supermarket_api.dto.BranchResponseDto;
import com.massemiso.supermarket_api.dto.DetailSaleRequestDto;
import com.massemiso.supermarket_api.dto.DetailSaleResponseDto;
import com.massemiso.supermarket_api.dto.ProductRequestDto;
import com.massemiso.supermarket_api.dto.ProductResponseDto;
import com.massemiso.supermarket_api.dto.SaleRequestDto;
import com.massemiso.supermarket_api.dto.SaleResponseDto;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.entity.DetailSale;
import com.massemiso.supermarket_api.entity.Product;
import com.massemiso.supermarket_api.entity.Sale;
import com.massemiso.supermarket_api.entity.SaleStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TestDataFactory {
  private final static Long DEFAULT_BRANCH_ID = 1L;
  private final static String DEFAULT_BRANCH_NAME = "Some branch name";
  private final static String DEFAULT_BRANCH_ADDRESS  = "Some branch address";
  private final static String DEFAULT_BRANCH_PHONENUMBER  = "123456789" ;

  private final static Long DEFAULT_PRODUCT_ID = 1L;
  private final static String DEFAULT_PRODUCT_NAME = "Some product name";
  private final static String DEFAULT_PRODUCT_CATEGORY  = "Some product category";
  private final static BigDecimal DEFAULT_PRODUCT_ACTUALPRICE = BigDecimal.valueOf(2.50);

  private final static Long DEFAULT_SALE_ID = 1L;
  private final static BigDecimal DEFAULT_SALE_TOTAL =
      createDefaultSale().getTotal();
  private final static LocalDate DEFAULT_SALE_DATE = LocalDate.now();


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
        .name(DEFAULT_PRODUCT_NAME)
        .category(DEFAULT_PRODUCT_CATEGORY)
        .actualPrice(DEFAULT_PRODUCT_ACTUALPRICE)
        .build();
  }

  public static ProductRequestDto createDefaultProductRequestDto() {
    return new ProductRequestDto(
        DEFAULT_PRODUCT_NAME,
        DEFAULT_PRODUCT_CATEGORY,
        DEFAULT_PRODUCT_ACTUALPRICE
    );
  }

  public static ProductResponseDto createDefaultProductResponseDto() {
    return new ProductResponseDto(
        DEFAULT_PRODUCT_ID,
        DEFAULT_PRODUCT_NAME,
        DEFAULT_PRODUCT_CATEGORY,
        DEFAULT_PRODUCT_ACTUALPRICE
    );
  }

  public static Sale createDefaultSale() {
    return Sale.builder()
        .branch(createDefaultBranch())
        .detailSaleList(List.of(createDefaultDetailSale(), createDefaultDetailSale()))
        .build();
  }

  public static SaleRequestDto createDefaultSaleRequestDto(){
    return new SaleRequestDto(
        DEFAULT_BRANCH_ID,
        List.of(createDefaultDetailSaleRequestDto(),
            createDefaultDetailSaleRequestDto())
    );
  }

  public static SaleRequestDto createDefaultSaleRequestDtoWithInvalidBranchId(){
    return new SaleRequestDto(
        999L,
        List.of(createDefaultDetailSaleRequestDto(),
            createDefaultDetailSaleRequestDto())
    );
  }

  public static SaleResponseDto createDefaultSaleResponseDto() {
    return new SaleResponseDto(
        DEFAULT_SALE_ID,
        DEFAULT_SALE_DATE,
        DEFAULT_BRANCH_ID,
        List.of(createDefaultDetailSaleResponseDto(1L),
            createDefaultDetailSaleResponseDto(2L)),
        SaleStatus.REGISTERED,
        DEFAULT_SALE_TOTAL
    );
  }

  public static DetailSale createDefaultDetailSale() {
    return DetailSale.builder()
        .quantity(5)
        .product(createDefaultProduct())
        .build();
  }

  public static DetailSaleRequestDto createDefaultDetailSaleRequestDto() {
    return new DetailSaleRequestDto(
        5,
        DEFAULT_PRODUCT_ID
    );
  }

  public static DetailSaleResponseDto createDefaultDetailSaleResponseDto(Long id) {
    return new DetailSaleResponseDto(
       id,
       5,
       DEFAULT_PRODUCT_ACTUALPRICE,
       DEFAULT_PRODUCT_ID
    );
  }

  public static Long getDefaultBranchId() {
    return DEFAULT_BRANCH_ID;
  }

  public static Long getDefaultProductId() { return DEFAULT_PRODUCT_ID; }

  public static Long getDefaultSaleId() { return DEFAULT_SALE_ID; }

  public static LocalDate getDefaultSaleDate(){  return DEFAULT_SALE_DATE; }

}
