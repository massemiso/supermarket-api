package com.massemiso.supermarket_api.service;

import com.massemiso.supermarket_api.dto.BranchRequestDto;
import com.massemiso.supermarket_api.dto.BranchResponseDto;
import com.massemiso.supermarket_api.dto.mapper.BranchMapper;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.exception.BranchNotFoundException;
import com.massemiso.supermarket_api.repository.BranchRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
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
    return branchRepository
        .findByDeletedAtIsNull(pageable)
        .map(branchMapper::toDto);
  }

  public BranchResponseDto getById(Long id) {
    return branchMapper.toDto(findById(id));
  }

  @Transactional
  public BranchResponseDto create(BranchRequestDto requestDto) {
    Branch entity = branchMapper.toEntity(requestDto);
    entity = branchRepository.save(entity);

    return branchMapper.toDto(entity);
  }

  @Transactional
  public BranchResponseDto update(Long id, BranchRequestDto requestDto) {
    Branch entity = findById(id);
    entity.update(requestDto.name(), requestDto.address(), requestDto.phoneNumber());
    entity = branchRepository.save(entity);

    return branchMapper.toDto(entity);
  }

  @Transactional
  public void delete(Long id) {
    Branch entity = findById(id);
    entity.delete();

    branchRepository.save(entity);
  }

  private Branch findById(Long id){
    return branchRepository
        .findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new BranchNotFoundException(id));
  }


}
