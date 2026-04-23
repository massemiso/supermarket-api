package com.massemiso.supermarket_api.repository;

import com.massemiso.supermarket_api.entity.Sale;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends IJpaRepositoryWithDeletedAt<Sale> {
  Page<Sale> findByDeletedAtIsNullAndBranchId(Long branchId, Pageable pageable);
  Page<Sale> findByDeletedAtIsNullAndDate(LocalDate date, Pageable pageable);
  Page<Sale> findByDeletedAtIsNullAndBranchIdAndDate(Long branchId, LocalDate date, Pageable pageable);
}
