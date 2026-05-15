package com.massemiso.supermarket_api.mapper;

import com.massemiso.supermarket_api.dto.BranchRequestDto;
import com.massemiso.supermarket_api.dto.BranchResponseDto;
import com.massemiso.supermarket_api.entity.Branch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchMapper {
  public BranchResponseDto toDto(Branch entity);
  public Branch toEntity(BranchRequestDto requestDto);
}
