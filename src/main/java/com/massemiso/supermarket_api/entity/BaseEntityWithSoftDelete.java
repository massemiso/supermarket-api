package com.massemiso.supermarket_api.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Base entity with soft delete functionality.
 * Soft delete is used instead of hard delete to maintain data integrity.
 * Has an:
 *  id field to track the entity's unique identifier.
 *  createdAt field to track when the entity was created.
 *  deletedAt field to track when the entity was deleted.
 */
@MappedSuperclass
@Getter
@NoArgsConstructor
public abstract class BaseEntityWithSoftDelete {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private final LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime deletedAt;

  public void delete(){
    this.deletedAt = LocalDateTime.now();
  }

  public boolean isDeleted() {
    return deletedAt != null;
  }

}
