package com.massemiso.supermarket_api.service;

import com.massemiso.supermarket_api.dto.ProductRequestDto;
import com.massemiso.supermarket_api.dto.ProductResponseDto;
import com.massemiso.supermarket_api.dto.mapper.ProductMapper;
import com.massemiso.supermarket_api.entity.Product;
import com.massemiso.supermarket_api.exception.ProductNotFoundException;
import com.massemiso.supermarket_api.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    return productRepository
        .findByDeletedAtIsNull(pageable)
        .map(productMapper::toDto);
  }

  public ProductResponseDto getById(Long id) {
    return productMapper.toDto(findById(id));
  }

  @Transactional
  public ProductResponseDto create(ProductRequestDto requestDto) {
    Product entity = productMapper.toEntity(requestDto);
    entity = productRepository.save(entity);

    return productMapper.toDto(entity);
  }

  @Transactional
  public ProductResponseDto update(Long id, ProductRequestDto requestDto) {
    Product entity = findById(id);
    entity.update(requestDto.name(), requestDto.category(), requestDto.actualPrice());
    entity = productRepository.save(entity);

    return productMapper.toDto(entity);
  }

  @Transactional
  public void delete(Long id) {
    Product entity = findById(id);
    entity.delete();

    productRepository.save(entity);
  }

  private Product findById(Long id){
    return productRepository
        .findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }

}
