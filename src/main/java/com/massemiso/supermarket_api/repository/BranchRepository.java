package com.massemiso.supermarket_api.repository;

import com.massemiso.supermarket_api.entity.Branch;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends IJpaRepositoryWithDeletedAt<Branch> {
}
