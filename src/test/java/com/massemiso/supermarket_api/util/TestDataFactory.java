package com.massemiso.supermarket_api.util;

import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.entity.Product;
import java.math.BigDecimal;

public class TestDataFactory {
  public static Branch createDefaultBranch() {
    return Branch.builder()
        .name("Some branch name")
        .address("Some address")
        .phoneNumber("123456")
        .build();
  }

  public static Product createDefaultProduct() {
    return Product.builder()
        .name("Some product name")
        .category("Some category")
        .actualPrice(BigDecimal.valueOf(2.50))
        .build();
  }
}
