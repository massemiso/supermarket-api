package com.massemiso.supermarket_api.repository;

import com.massemiso.supermarket_api.entity.UserEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends IJpaRepositoryWithDeletedAt<UserEntity> {
  Optional<UserEntity> findByUsername(String username);
}
