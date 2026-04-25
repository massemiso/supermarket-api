package com.massemiso.supermarket_api.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.massemiso.supermarket_api.dto.mapper.BranchMapper;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class BranchMapperTest {

  @Test
  void toDto_GivenABranch_ShouldReturnABranchResponseDto() {
    // Arrange
    Branch entity = TestDataFactory.createDefaultBranch();
    BranchMapper branchMapper = new BranchMapper();

    // manually set the id field even if it's private and has no setter
    ReflectionTestUtils.setField(entity, "id", 1L);

    // Act
    BranchResponseDto actual = branchMapper.toDto(entity);

    // Assert
    assertNotNull(actual);
    assertEquals(entity.getId(), actual.id());
    assertEquals(entity.getName(), actual.name());
    assertEquals(entity.getAddress(), actual.address());
    assertEquals(entity.getPhoneNumber(),  actual.phoneNumber());
  }

  @Test
  void toEntity_GivenBranchRequestDto_ShouldReturnABranch() {
    // Arrange
    BranchRequestDto dto = new BranchRequestDto(
        "Some branch",
        "Some address",
        "Some phoneNumber"
    );
    BranchMapper branchMapper = new BranchMapper();

    // Act
    Branch actual = branchMapper.toEntity(dto);

    // Assert
    assertNotNull(actual);
    assertNull(actual.getId()); // id should be null because branch doesn't
    // exist in db yet
    assertEquals(dto.name(), actual.getName());
    assertEquals(dto.address(), actual.getAddress());
    assertEquals(dto.phoneNumber(),  actual.getPhoneNumber());
    assertNull(actual.getSaleList());
  }
}