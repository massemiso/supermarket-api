package com.massemiso.supermarket_api.service;

import com.massemiso.supermarket_api.dto.ProductRequestDto;
import com.massemiso.supermarket_api.dto.ProductResponseDto;
import com.massemiso.supermarket_api.dto.mapper.ProductMapper;
import com.massemiso.supermarket_api.entity.Product;
import com.massemiso.supermarket_api.exception.ProductNotFoundException;
import com.massemiso.supermarket_api.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  @Autowired
  public ProductService(
      ProductRepository productRepository,
      ProductMapper productMapper) {
    this.productRepository = productRepository;
    this.productMapper = productMapper;
  }

  public Page<ProductResponseDto> getAll(Pageable pageable) {
    log.info("Attempting to retrieve all products");
    Page<ProductResponseDto> page = productRepository
        .findByDeletedAtIsNull(pageable)
        .map(productMapper::toDto);

    log.info("Returning page {} of products with total {} elements",
            page.getNumber(), page.getTotalElements());
    return page;
  }

  public ProductResponseDto getById(Long id) {
    log.info("Attempting to retrieve product by ID: {}", id);
    ProductResponseDto dto = productMapper.toDto(findById(id));

    log.info("Returning product: {}", dto);
    return dto;
  }

  @Transactional
  public ProductResponseDto create(ProductRequestDto requestDto) {
    log.info("Attempting to create product: {}", requestDto);
    Product entity = productMapper.toEntity(requestDto);
    entity = productRepository.save(entity);

    log.info("Successfully created product with id: {}", entity.getId());
    return productMapper.toDto(entity);
  }

  @Transactional
  public ProductResponseDto update(Long id, ProductRequestDto requestDto) {
    log.info("Attempting to update product with id: {}", id);
    Product entity = findById(id);
    entity.update(requestDto.name(), requestDto.category(), requestDto.actualPrice());
    entity = productRepository.save(entity);

    ProductResponseDto dto = productMapper.toDto(entity);
    log.info("Successfully updated product: {}", dto);
    return dto;
  }

  @Transactional
  public void delete(Long id) {
    log.info("Attempting to soft delete product with id: {}", id);
    Product entity = findById(id);
    entity.delete();

    productRepository.save(entity);
    log.info("Successfully soft deleted product with id: {}", id);
  }

  private Product findById(Long id){
    return productRepository
        .findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }

}
