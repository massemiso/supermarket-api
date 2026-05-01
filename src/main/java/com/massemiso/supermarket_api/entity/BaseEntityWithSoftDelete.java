package com.massemiso.supermarket_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntityWithSoftDelete {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  private LocalDateTime deletedAt;

  @Version
  private Integer version;

  public void delete(){
    this.deletedAt = LocalDateTime.now();
  }

  public boolean isDeleted() {
    return deletedAt != null;
  }

}
