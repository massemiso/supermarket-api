package com.massemiso.supermarket_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.massemiso.supermarket_api.dto.BestSellerResponseDto;
import com.massemiso.supermarket_api.dto.ProductResponseDto;
import com.massemiso.supermarket_api.dto.mapper.ProductMapper;
import com.massemiso.supermarket_api.entity.Product;
import com.massemiso.supermarket_api.exception.ProductNotFoundException;
import com.massemiso.supermarket_api.repository.BestSellerProjection;
import com.massemiso.supermarket_api.repository.DetailSaleRepository;
import com.massemiso.supermarket_api.repository.ProductRepository;
import com.massemiso.supermarket_api.util.TestDataFactory;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class StatServiceTest {

  @Mock
  private DetailSaleRepository detailSaleRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ProductMapper productMapper;

  @InjectMocks
  private StatService statService;

  @Test
  void getBestSellingProduct_ShouldReturnBestSellerResponseDto() {
    // arrange
    BestSellerProjection seller = mock(BestSellerProjection.class);
    when(seller.getProductId()).thenReturn(1L);
    when(seller.getTotalRevenue()).thenReturn(BigDecimal.TEN);

    Product product = TestDataFactory.createDefaultProduct();
    ReflectionTestUtils.setField(product, "id", seller.getProductId());

    ProductResponseDto productResponseDto = new ProductResponseDto(
        product.getId(),
        product.getName(),
        product.getCategory(),
        product.getActualPrice()
    );

    // mock
    when(detailSaleRepository.findBestSellingProductId())
        .thenReturn(seller);
    when(productRepository.findByIdAndDeletedAtIsNull(seller.getProductId()))
        .thenReturn(Optional.of(product));
    when(productMapper.toDto(product))
        .thenReturn(productResponseDto);

    // act
    BestSellerResponseDto responseDto = statService.getBestSellingProduct();

    // assert
    assertNotNull(responseDto);
    assertEquals(seller.getProductId(), responseDto.product().id());
    assertEquals(seller.getTotalRevenue(), responseDto.totalRevenue());

    verify(detailSaleRepository).findBestSellingProductId();
    verify(productRepository).findByIdAndDeletedAtIsNull(seller.getProductId());
    verify(productMapper).toDto(product);
  }

  @Test
  void getBestSellingProduct_GivenInvalidProductIdFromSeller_ShouldThrowProductNotFoundException() {
    BestSellerProjection seller = mock(BestSellerProjection.class);
    when(seller.getProductId()).thenReturn(-1L);

    // mock
    when(detailSaleRepository.findBestSellingProductId())
        .thenReturn(seller);
    when(productRepository.findByIdAndDeletedAtIsNull(seller.getProductId()))
        .thenReturn(Optional.empty());

    // act & assert
    assertThrows(ProductNotFoundException.class,
        () ->
            statService.getBestSellingProduct()
        );

    verify(detailSaleRepository).findBestSellingProductId();
    verify(productRepository).findByIdAndDeletedAtIsNull(seller.getProductId());
    verify(productMapper, never()).toDto(any(Product.class));

  }
}