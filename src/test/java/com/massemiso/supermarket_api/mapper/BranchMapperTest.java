package com.massemiso.supermarket_api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.massemiso.supermarket_api.dto.BranchRequestDto;
import com.massemiso.supermarket_api.dto.BranchResponseDto;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

class BranchMapperTest {
  private final BranchMapper mapper = Mappers.getMapper(BranchMapper.class);

  @Test
  void toDto_GivenABranch_ShouldReturnABranchResponseDto() {
    // Arrange
    Branch entity = TestDataFactory.createDefaultBranch();

    // manually set the id field even if it's private and has no setter
    ReflectionTestUtils.setField(entity, "id", 1L);

    // Act
    BranchResponseDto actual = mapper.toDto(entity);

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

    // Act
    Branch actual = mapper.toEntity(dto);

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