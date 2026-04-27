package com.massemiso.supermarket_api.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

// dummy class just for test base entity
class TestEntity extends BaseEntityWithSoftDelete{}

class BaseEntityWithSoftDeleteTest {

  @Test
  void delete_ShouldSetDeletedAtTimestamp() {
    // Arrange
    TestEntity testEntity = new TestEntity();

    // Act
    testEntity.delete();

    // Assertions
    assertThat(testEntity.getDeletedAt())
        .isNotNull()
        .isBeforeOrEqualTo(LocalDateTime.now());
  }

  @Test
  void isDeleted_WhenDeletedAtIsNull_ShouldReturnFalse() {
    // Arrange
    TestEntity testEntity = new TestEntity();

    // Act
    boolean actual = testEntity.isDeleted();

    // Assertions
    assertNull(testEntity.getDeletedAt());
    assertFalse(actual);
  }

  @Test
  void isDeleted_WhenDeletedAtIsNotNull_ShouldReturnTrue() {
    // Arrange
    TestEntity testEntity = new TestEntity();

    // Act
    testEntity.delete();
    boolean actual = testEntity.isDeleted();

    // Assertions
    assertNotNull(testEntity.getDeletedAt());
    assertTrue(actual);
  }
}