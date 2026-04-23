package com.massemiso.supermarket_api.dto;

import com.massemiso.supermarket_api.entity.Branch;
import org.springframework.stereotype.Component;

@Component
public class BranchMapper {
  public BranchResponseDto toDto(Branch entity){
    return new BranchResponseDto(
        entity.getId(),
        entity.getName(),
        entity.getAddress(),
        entity.getPhoneNumber()
    );
  }

  public Branch toEntity(BranchRequestDto requestDto) {
    return Branch.builder()
        .name(requestDto.name())
        .address(requestDto.address())
        .phoneNumber(requestDto.phoneNumber())
        .build();
  }
}
