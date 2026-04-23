package com.massemiso.supermarket_api.repository;

import java.math.BigDecimal;

public interface BestSellerProjection {
  Long getProductId();
  BigDecimal getTotalRevenue();
}
