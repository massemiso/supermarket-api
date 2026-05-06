package com.massemiso.supermarket_api.repository;

import com.massemiso.supermarket_api.entity.UserEntity;
import java.util.Optional;

public interface UserRepository extends IJpaRepositoryWithDeletedAt<UserEntity> {
  Optional<UserEntity> findByUsername(String username);
}
