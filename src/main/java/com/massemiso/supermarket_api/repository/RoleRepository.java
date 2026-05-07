package com.massemiso.supermarket_api.repository;

import com.massemiso.supermarket_api.entity.RoleEntity;
import com.massemiso.supermarket_api.entity.RoleEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
  Optional<RoleEntity> findByRoleEnum(RoleEnum roleEnum);
}
