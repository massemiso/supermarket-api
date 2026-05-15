package com.massemiso.supermarket_api.mapper;

import com.massemiso.supermarket_api.dto.BranchRequestDto;
import com.massemiso.supermarket_api.dto.BranchResponseDto;
import com.massemiso.supermarket_api.entity.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BranchMapper {
  BranchMapper INSTANCE = Mappers.getMapper(BranchMapper.class);
  public BranchResponseDto toDto(Branch entity);
  public Branch toEntity(BranchRequestDto requestDto);
}
