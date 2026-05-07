package com.massemiso.supermarket_api.repository;

import com.massemiso.supermarket_api.entity.RoleEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends IJpaRepositoryWithDeletedAt<RoleEntity> {

}
