package com.massemiso.supermarket_api.controller;

import com.massemiso.supermarket_api.dto.ApiResponse;
import com.massemiso.supermarket_api.dto.BranchRequestDto;
import com.massemiso.supermarket_api.dto.BranchResponseDto;
import com.massemiso.supermarket_api.service.BranchService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

  private final BranchService branchService;

  @Autowired
  public BranchController(BranchService branchService){
    this.branchService = branchService;
  }

  // GET ALL
  @GetMapping
  public ResponseEntity<Page<BranchResponseDto>> getAll(
      Pageable pageable){
    return ResponseEntity.ok(
        branchService.getAll(pageable));
  }
  
  // GET
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<BranchResponseDto>> getById(
      @PathVariable Long id){
    BranchResponseDto responseDto = branchService.getById(id);
    ApiResponse<BranchResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "Branch retrieved successfully",
        HttpStatus.OK.value()
    );
    return ResponseEntity
        .ok(apiResponse);
  }

  // POST
  @PostMapping
  public ResponseEntity<ApiResponse<BranchResponseDto>> create(
      @Valid @RequestBody BranchRequestDto requestDto){
    BranchResponseDto responseDto = branchService.create(requestDto);
    ApiResponse<BranchResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "Branch created successfully",
        HttpStatus.CREATED.value()
    );
    return ResponseEntity
        .created(URI.create("/api/branches/" + responseDto.id()))
        .body(apiResponse);
  }

  // UPDATE
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<BranchResponseDto>> update(
      @PathVariable Long id,
      @Valid @RequestBody BranchRequestDto requestDto
  ){
    BranchResponseDto responseDto = branchService.update(id, requestDto);
    ApiResponse<BranchResponseDto> apiResponse = ApiResponse.success(
        responseDto,
        "Branch updated successfully",
        HttpStatus.OK.value()
    );
    return ResponseEntity
        .ok(apiResponse);
  }

  // DELETE
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(
      @PathVariable Long id
  ){
    branchService.delete(id);
    return ResponseEntity
        .noContent()
        .build();
  }

}
