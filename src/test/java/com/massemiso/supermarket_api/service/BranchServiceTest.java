package com.massemiso.supermarket_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.massemiso.supermarket_api.dto.BranchRequestDto;
import com.massemiso.supermarket_api.dto.BranchResponseDto;
import com.massemiso.supermarket_api.dto.mapper.BranchMapper;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.exception.BranchNotFoundException;
import com.massemiso.supermarket_api.repository.BranchRepository;
import com.massemiso.supermarket_api.util.TestDataFactory;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {

  @Mock
  private BranchRepository branchRepository;

  @Mock
  private BranchMapper branchMapper;

  @InjectMocks
  private BranchService branchService;

  @Test
  void getAll_GivenPageable_ShouldReturnSomeBranchesInAPage() {
    // arrange
    Pageable pageable = PageRequest.of(0, 10);
    BranchResponseDto responseDto = TestDataFactory.createDefaultBranchResponseDto();
    Branch entity = TestDataFactory.createDefaultBranch();
    ReflectionTestUtils.setField(entity, "id", responseDto.id());

    // page contains 1 branch
    List<Branch> branches = List.of(entity);
    Page<Branch> pageBranch = new PageImpl<>(branches, pageable, branches.size());

    // stub repository
    when(branchRepository.findByDeletedAtIsNull(pageable))
        .thenReturn(pageBranch);

    // stub mapper -- this will happen to every branch in Page<Branch> -> List<Branch>
    when(branchMapper.toDto(entity))
        .thenReturn(responseDto);

    // act
    Page<BranchResponseDto> page = branchService.getAll(pageable);

    // assert
    assertNotNull(page);
    assertEquals(branches.size(), page.getContent().size());
    assertEquals(responseDto.id(), page.getContent().getFirst().id());
    assertEquals(responseDto.name(), page.getContent().getFirst().name());
    assertEquals(responseDto.address(), page.getContent().getFirst().address());
    assertEquals(responseDto.phoneNumber(), page.getContent().getFirst().phoneNumber());

    // verify that repository and mapper were called
    verify(branchRepository).findByDeletedAtIsNull(pageable);
    verify(branchMapper).toDto(entity);
  }

  @Test
  void getById_GivenValidId_ShouldReturnCorrectBranch() {
    // arrange
    BranchResponseDto responseDto = TestDataFactory.createDefaultBranchResponseDto();
    Branch entity = TestDataFactory.createDefaultBranch();
    Long validId = responseDto.id();
    ReflectionTestUtils.setField(entity, "id", validId);

    // mocks
    when(branchRepository.findByIdAndDeletedAtIsNull(validId))
        .thenReturn(Optional.of(entity));

    when(branchMapper.toDto(entity))
        .thenReturn(responseDto);

    // act
    BranchResponseDto actual = branchService.getById(validId);

    // assert
    assertNotNull(actual);
    assertEquals(validId, actual.id());
    assertEquals(entity.getName(), actual.name());
    assertEquals(entity.getAddress(), actual.address());
    assertEquals(entity.getPhoneNumber(), actual.phoneNumber());

    verify(branchRepository).findByIdAndDeletedAtIsNull(validId);
    verify(branchMapper).toDto(entity);
  }

  @Test
  void getById_GivenInvalidId_ShouldThrowBranchNotFoundException() {
    // arrange
    Long invalidId = -1L;

    // mocks
    when(branchRepository.findByIdAndDeletedAtIsNull(invalidId))
        .thenReturn(Optional.empty());

    // asserts
    assertThrows(BranchNotFoundException.class,
        () ->
            branchService.getById(invalidId)
    );

    verify(branchRepository).findByIdAndDeletedAtIsNull(invalidId);
    verify(branchMapper, never()).toDto(any(Branch.class));
  }

  @Test
  void create_GivenValidBranchRequestDto_ShouldReturnBranchResponseDto() {
    // arrange
    Branch branch = TestDataFactory.createDefaultBranch();
    BranchRequestDto requestDto = TestDataFactory.createDefaultBranchRequestDto();
    BranchResponseDto responseDto = TestDataFactory.createDefaultBranchResponseDto();
    ReflectionTestUtils.setField(branch, "id", responseDto.id());

    // mocks
    when(branchMapper.toEntity(requestDto)).thenReturn(branch);
    when(branchRepository.save(branch)).thenReturn(branch);
    when(branchMapper.toDto(branch)).thenReturn(responseDto);

    // act
    BranchResponseDto actual = branchService.create(requestDto);

    // assert
    assertNotNull(actual);
    assertEquals(branch.getId(), actual.id());
    assertEquals(requestDto.name(), actual.name());
    assertEquals(requestDto.address(), actual.address());
    assertEquals(requestDto.phoneNumber(), actual.phoneNumber());

    verify(branchMapper).toEntity(requestDto);
    verify(branchRepository).save(branch);
    verify(branchMapper).toDto(branch);
  }

  @Test
  void update_GivenValidIdAndBranchRequestDto_ShouldReturnBranchResponseDto() {
    // arrange
    BranchRequestDto requestDto = TestDataFactory.createDefaultBranchRequestDto(); // arg
    BranchResponseDto responseDto = TestDataFactory.createDefaultBranchResponseDto(); // expected
    Branch entity = TestDataFactory.createDefaultBranch();
    Long validId = responseDto.id();
    ReflectionTestUtils.setField(entity, "id", validId);

    when(branchRepository.findByIdAndDeletedAtIsNull(validId))
        .thenReturn(Optional.of(entity));
    when(branchRepository.save(entity)).thenReturn(entity);
    when(branchMapper.toDto(entity)).thenReturn(responseDto);

    // acts
    BranchResponseDto actual = branchService.update(validId, requestDto);

    // asserts

    /* check if entity was actually updated */
    assertEquals(requestDto.name(), entity.getName());

    assertNotNull(actual);
    assertEquals(validId, actual.id());
    verify(branchRepository).findByIdAndDeletedAtIsNull(validId);
    verify(branchRepository).save(entity);
    verify(branchMapper).toDto(entity);
  }

  @Test
  void update_GivenInvalidId_ShouldThrowBranchNotFoundException() {
    // arrange
    BranchRequestDto requestDto = TestDataFactory.createDefaultBranchRequestDto();
    Long invalidId = -1L;

    // mocks
    when(branchRepository.findByIdAndDeletedAtIsNull(invalidId))
        .thenReturn(Optional.empty());

    // asserts
    assertThrows(BranchNotFoundException.class,
        () ->
            branchService.update(invalidId, requestDto)
    );

    verify(branchRepository).findByIdAndDeletedAtIsNull(invalidId);
    verify(branchMapper, never()).toDto(any());
  }

  @Test
  void delete_GivenValidId_ShouldSoftDeleteBranch() {
    // arrange
    Branch entity = TestDataFactory.createDefaultBranch();
    Long validId = TestDataFactory.getDefaultBranchId();
    ReflectionTestUtils.setField(entity, "id", validId);

    // mock
    when(branchRepository.findByIdAndDeletedAtIsNull(validId))
        .thenReturn(Optional.of(entity));

    // act
    branchService.delete(validId);

    // assert
    assertNotNull(entity);
    assertTrue(entity.isDeleted());
    verify(branchRepository).findByIdAndDeletedAtIsNull(validId);
    verify(branchRepository).save(entity);
  }

  @Test
  void delete_GivenInvalidId_ShouldThrowBranchNotFoundException() {
    // arrange
    Long invalidId = -1L;

    // mocks
    when(branchRepository.findByIdAndDeletedAtIsNull(invalidId))
        .thenReturn(Optional.empty());

    // asserts
    assertThrows(BranchNotFoundException.class,
        () ->
            branchService.delete(invalidId)
    );

    verify(branchRepository).findByIdAndDeletedAtIsNull(invalidId);
    verify(branchRepository, never()).save(any(Branch.class));
  }
}