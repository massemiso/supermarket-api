package com.massemiso.supermarket_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.massemiso.supermarket_api.dto.ProductRequestDto;
import com.massemiso.supermarket_api.dto.ProductResponseDto;
import com.massemiso.supermarket_api.dto.mapper.ProductMapper;
import com.massemiso.supermarket_api.entity.Product;
import com.massemiso.supermarket_api.exception.ProductNotFoundException;
import com.massemiso.supermarket_api.repository.ProductRepository;
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
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ProductMapper productMapper;

  @InjectMocks
  private ProductService productService;

  @Test
  void getAll_GivenPageable_ShouldReturnSomeProductResponseDtoInAPage() {
    // arrange
    Pageable pageable = PageRequest.of(0, 10);
    ProductResponseDto responseDto =
        TestDataFactory.createDefaultProductResponseDto();
    Product entity = TestDataFactory.createDefaultProduct();
    ReflectionTestUtils.setField(entity, "id", responseDto.id());

    List<Product> entities = List.of(entity);
    Page<Product> page = new PageImpl<>(entities, pageable, entities.size());

    // mock
    when(productRepository.findByDeletedAtIsNull(pageable))
        .thenReturn(page);
    when(productMapper.toDto(entity))
        .thenReturn(responseDto);

    // act
    Page<ProductResponseDto> actual = productService.getAll(pageable);

    // assert
    assertNotNull(actual);
    assertEquals(entities.size(), actual.getContent().size());
    assertEquals(responseDto.id(), actual.getContent().getFirst().id());
    assertEquals(responseDto.name(), actual.getContent().getFirst().name());
    assertEquals(responseDto.category(), actual.getContent().getFirst().category());
    assertThat(actual.getContent().getFirst().actualPrice())
        .isEqualByComparingTo(responseDto.actualPrice());

    verify(productRepository).findByDeletedAtIsNull(pageable);
    verify(productMapper, times(1)).toDto(entity);
  }

  @Test
  void getById_GivenValidId_ShouldReturnAProductResponseDto() {
    // arrange
    ProductResponseDto responseDto = TestDataFactory.createDefaultProductResponseDto();
    Product entity = TestDataFactory.createDefaultProduct();
    Long validId = responseDto.id();
    ReflectionTestUtils.setField(entity, "id", validId);

    // mock
    when(productRepository.findByIdAndDeletedAtIsNull(validId))
        .thenReturn(Optional.of(entity));
    when(productMapper.toDto(entity))
        .thenReturn(responseDto);

    // act
    ProductResponseDto actual = productService.getById(validId);

    // assert
    assertNotNull(actual);
    assertEquals(responseDto.id(), actual.id());
    assertEquals(responseDto.name(), actual.name());
    assertEquals(responseDto.category(), actual.category());
    assertThat(actual.actualPrice())
        .isEqualByComparingTo(responseDto.actualPrice());

    verify(productRepository).findByIdAndDeletedAtIsNull(validId);
    verify(productMapper).toDto(entity);
  }

  @Test
  void getById_GivenInvalidId_ShouldThrowProductNotFoundException() {
    // arrange
    Long invalidId = -1L;

    // mock
    when(productRepository.findByIdAndDeletedAtIsNull(invalidId))
        .thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class,
        () ->
            productService.getById(invalidId)
        );

    verify(productRepository).findByIdAndDeletedAtIsNull(invalidId);
    verify(productMapper, never()).toDto(any(Product.class));
  }

  @Test
  void create_GivenValidProductRequestDto_ShouldReturnAProductResponseDto() {
    // arrange
    ProductResponseDto responseDto =
        TestDataFactory.createDefaultProductResponseDto();
    ProductRequestDto requestDto =
        TestDataFactory.createDefaultProductRequestDto();
    Product entity = TestDataFactory.createDefaultProduct();
    Long validId = responseDto.id();
    ReflectionTestUtils.setField(entity, "id", validId);

    // mock
    when(productMapper.toEntity(requestDto))
        .thenReturn(entity);
    when(productRepository.save(entity))
        .thenReturn(entity);
    when(productMapper.toDto(entity))
        .thenReturn(responseDto);

    // act
    ProductResponseDto actual = productService.create(requestDto);

    // assert
    assertNotNull(actual);
    assertEquals(validId, actual.id());
    assertEquals(requestDto.name(), actual.name());
    assertEquals(requestDto.category(), actual.category());
    assertThat(actual.actualPrice())
        .isEqualByComparingTo(requestDto.actualPrice());

    verify(productMapper).toEntity(requestDto);
    verify(productRepository).save(entity);
    verify(productMapper).toDto(entity);
  }

  @Test
  void update_GivenValidIdAndProductRequestDto_ShouldReturnAProductResponseDto() {
    // arrange
    ProductResponseDto responseDto =
        TestDataFactory.createDefaultProductResponseDto();
    ProductRequestDto requestDto =
        TestDataFactory.createDefaultProductRequestDto();
    Long validId = responseDto.id();
    Product entity = TestDataFactory.createDefaultProduct();
    ReflectionTestUtils.setField(entity, "id", validId);

    // mock
    when(productRepository.findByIdAndDeletedAtIsNull(validId))
        .thenReturn(Optional.of(entity));
    when(productRepository.save(entity))
        .thenReturn(entity);
    when(productMapper.toDto(entity))
        .thenReturn(responseDto);

    // act
    ProductResponseDto actual = productService.update(validId, requestDto);

    // assert

    /* check if entity was actually updated */
    assertEquals(requestDto.name(), entity.getName());

    assertNotNull(actual);
    assertEquals(validId, actual.id());
    assertEquals(requestDto.name(), actual.name());
    assertEquals(requestDto.category(), actual.category());
    assertThat(actual.actualPrice())
        .isEqualByComparingTo(requestDto.actualPrice());

    verify(productRepository).findByIdAndDeletedAtIsNull(validId);
    verify(productRepository).save(entity);
    verify(productMapper).toDto(entity);
  }

  @Test
  void update_GivenInvalidId_ShouldThrowProductNotFoundException() {
    // arrange
    Long invalidId = -1L;

    // mock
    when(productRepository.findByIdAndDeletedAtIsNull(invalidId))
        .thenReturn(Optional.empty());

    // act & assert
    assertThrows(ProductNotFoundException.class,
        () ->
            productService.update(invalidId, any(ProductRequestDto.class))
        );

    verify(productRepository).findByIdAndDeletedAtIsNull(invalidId);
    verify(productMapper, never()).toDto(any(Product.class));
  }

  @Test
  void delete_GivenValidId_ShouldSoftDeleteProduct() {
    // arrange
    Long validId = TestDataFactory.getDefaultProductId();
    Product entity = TestDataFactory.createDefaultProduct();
    ReflectionTestUtils.setField(entity, "id", validId);

    // mock
    when(productRepository.findByIdAndDeletedAtIsNull(validId))
        .thenReturn(Optional.of(entity));

    // act
    productService.delete(validId);

    // assert
    assertNotNull(entity);
    assertTrue(entity.isDeleted());

    verify(productRepository).findByIdAndDeletedAtIsNull(validId);
    verify(productRepository).save(entity);
  }

  @Test
  void delete_GivenInvalidId_ShouldThrowProductNotFoundException() {
    // arrange
    Long invalidId = -1L;

    // mock
    when(productRepository.findByIdAndDeletedAtIsNull(invalidId))
        .thenReturn(Optional.empty());

    // act & assert
    assertThrows(ProductNotFoundException.class,
        () ->
            productService.delete(invalidId)
    );
    verify(productRepository).findByIdAndDeletedAtIsNull(invalidId);
    verify(productRepository, never()).save(any(Product.class));
  }
}