package com.massemiso.supermarket_api.repository;

import com.massemiso.supermarket_api.entity.BaseEntityWithSoftDelete;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for repositories that support soft deletion.
 * @param <T> Entity type that extends BaseEntityWithSoftDelete
 */
@Repository
public interface IJpaRepositoryWithDeletedAt<T extends BaseEntityWithSoftDelete> extends JpaRepository<T, Long> {
  Optional<T> findByIdAndDeletedAtIsNull(Long id);
  Page<T> findByDeletedAtIsNull(Pageable pageable);

}
