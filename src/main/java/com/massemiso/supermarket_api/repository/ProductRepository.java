package com.massemiso.supermarket_api.repository;

import com.massemiso.supermarket_api.entity.Product;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends IJpaRepositoryWithDeletedAt<Product> {
}
