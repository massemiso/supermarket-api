package com.massemiso.supermarket_api.service;

import com.massemiso.supermarket_api.dto.ProductMapper;
import com.massemiso.supermarket_api.dto.ProductResponseDto;
import com.massemiso.supermarket_api.exception.ProductNotFoundException;
import com.massemiso.supermarket_api.repository.BestSellerProjection;
import com.massemiso.supermarket_api.repository.DetailSaleRepository;
import com.massemiso.supermarket_api.repository.ProductRepository;
import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StatService {

  private final DetailSaleRepository detailSaleRepository;
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  @Autowired
  public StatService(
      DetailSaleRepository detailSaleRepository,
      ProductRepository productRepository,
      ProductMapper productMapper) {
    this.detailSaleRepository = detailSaleRepository;
    this.productRepository = productRepository;
    this.productMapper = productMapper;
  }

  /**
   * Identifies the top-performing product in terms of revenue by using a
   * native SQL query (to avoid performance issues with JPA)
   * Note: Aggregation excludes annulled sales to ensure financial reporting accuracy.
   * @return Pair of ProductResponseDto with the best selling product and
   * a BigDecimal with the total revenue.
   */
  public Pair<ProductResponseDto, BigDecimal> getBestSellingProduct() {
    log.info("Attempting to get best selling product");
    BestSellerProjection seller = detailSaleRepository.findBestSellingProductId();
    Pair<ProductResponseDto, BigDecimal> out = Pair.of(productMapper.toDto(productRepository
            .findByIdAndDeletedAtIsNull(seller.getProductId())
            .orElseThrow(() -> new ProductNotFoundException(seller.getProductId()))),
        seller.getTotalRevenue());

    log.info("Successfully get best selling product: {} with a total of {}",
        out.getFirst(),
        out.getSecond());
    return out;
  }
}
