package com.massemiso.supermarket_api.repository;

import com.massemiso.supermarket_api.entity.DetailSale;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailSaleRepository extends IJpaRepositoryWithDeletedAt<DetailSale> {
  @Query(value = """
        SELECT ds.product_id as productId, SUM(quantity * unit_price) as totalRevenue FROM detail_sale ds
        JOIN sale s ON ds.sale_id = s.id
        WHERE s.deleted_at IS NULL
        GROUP BY ds.product_id
        ORDER BY totalRevenue DESC
        LIMIT 1
        """, nativeQuery = true)
  BestSellerProjection findBestSellingProductId();

  List<DetailSale> findByDeletedAtIsNullAndSaleId(Long id);
}
