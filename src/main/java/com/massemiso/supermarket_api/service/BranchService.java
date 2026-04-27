package com.massemiso.supermarket_api.service;

import com.massemiso.supermarket_api.dto.BranchRequestDto;
import com.massemiso.supermarket_api.dto.BranchResponseDto;
import com.massemiso.supermarket_api.dto.mapper.BranchMapper;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.exception.BranchNotFoundException;
import com.massemiso.supermarket_api.repository.BranchRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BranchService {

  private final BranchRepository branchRepository;
  private final BranchMapper branchMapper;

  @Autowired
  public BranchService(
      BranchRepository branchRepository,
      BranchMapper branchMapper) {
    this.branchRepository = branchRepository;
    this.branchMapper = branchMapper;
  }

  public Page<BranchResponseDto> getAll(Pageable pageable) {
    log.info("Attempting to retrieve all branches");
    Page<BranchResponseDto> page = branchRepository
        .findByDeletedAtIsNull(pageable)
        .map(branchMapper::toDto);

    log.info("Returning page {} of branches with total {} elements",
        page.getNumber(), page.getTotalElements());
    return page;
  }

  public BranchResponseDto getById(Long id) {
    log.info("Attempting to retrieve branch by ID: {}", id);
    BranchResponseDto branchResponseDto = branchMapper.toDto(findById(id));

    log.info("Returning branch: {}", branchResponseDto);
    return branchResponseDto;
  }

  @Transactional
  public BranchResponseDto create(BranchRequestDto requestDto) {
    log.info("Attempting to create branch: {}", requestDto);
    Branch entity = branchMapper.toEntity(requestDto);
    entity = branchRepository.save(entity);

    log.info("Successfully created branch with ID: {}",
        entity.getId());
    return branchMapper.toDto(entity);
  }

  @Transactional
  public BranchResponseDto update(Long id, BranchRequestDto requestDto) {
    log.info("Attempting to update branch with ID: {}", id);
    Branch entity = findById(id);
    entity.update(requestDto.name(), requestDto.address(), requestDto.phoneNumber());
    entity = branchRepository.save(entity);

    BranchResponseDto dto = branchMapper.toDto(entity);
    log.info("Successfully updated branch: {}", dto);
    return dto;
  }

  @Transactional
  public void delete(Long id) {
    log.info("Attempting to soft delete branch with ID: {}", id);
    Branch entity = findById(id);
    entity.delete();

    branchRepository.save(entity);
    log.info("Successfully soft deleted branch with ID: {}", id);
  }

  private Branch findById(Long id){
    return branchRepository
        .findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new BranchNotFoundException(id));
  }


}
