package com.massemiso.supermarket_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.massemiso.supermarket_api.dto.DetailSaleRequestDto;
import com.massemiso.supermarket_api.dto.DetailSaleResponseDto;
import com.massemiso.supermarket_api.dto.SaleRequestDto;
import com.massemiso.supermarket_api.dto.SaleResponseDto;
import com.massemiso.supermarket_api.dto.mapper.DetailSaleMapper;
import com.massemiso.supermarket_api.dto.mapper.SaleMapper;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.entity.DetailSale;
import com.massemiso.supermarket_api.entity.Product;
import com.massemiso.supermarket_api.entity.Sale;
import com.massemiso.supermarket_api.exception.BranchNotFoundException;
import com.massemiso.supermarket_api.exception.ProductNotFoundException;
import com.massemiso.supermarket_api.exception.SaleNotFoundException;
import com.massemiso.supermarket_api.repository.BranchRepository;
import com.massemiso.supermarket_api.repository.DetailSaleRepository;
import com.massemiso.supermarket_api.repository.ProductRepository;
import com.massemiso.supermarket_api.repository.SaleRepository;
import com.massemiso.supermarket_api.util.TestDataFactory;
import java.time.LocalDate;
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
class SaleServiceTest {

  @Mock
  private SaleRepository saleRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private BranchRepository branchRepository;

  @Mock
  private DetailSaleRepository detailSaleRepository;

  @Mock
  private SaleMapper saleMapper;

  @Mock
  private DetailSaleMapper detailSaleMapper;

  @InjectMocks
  private SaleService saleService;

  @Test
  void getAll_GivenOnlyPageable_ShouldReturnAPageOfSaleResponseDto() {
    // arrange
    Pageable pageable =  PageRequest.of(0, 10);

    Sale entity = TestDataFactory.createDefaultSale();
    List<Sale> saleList  = List.of(entity);
    Page<Sale> pageSales = new PageImpl<>(saleList, pageable,
        saleList.size());

    SaleResponseDto responseDto =
        TestDataFactory.createDefaultSaleResponseDto();
    List<SaleResponseDto> content = List.of(responseDto);

    List<DetailSaleResponseDto> detailSaleResponseDtoList =
        responseDto.detailSaleList();

    // mock
    when(saleRepository.findByDeletedAtIsNull(pageable))
        .thenReturn(pageSales);
    when(detailSaleMapper.getDetailSaleListDto( anyList() ))
        .thenReturn(detailSaleResponseDtoList);
    when(saleMapper.toDto( any(Sale.class) , anyList() ))
        .thenReturn(responseDto);

    // Act
    Page<SaleResponseDto> actual =  saleService.getAll(pageable, null, null);

    // Assert
    assertFalse(actual.isEmpty());
    assertEquals(content.size(), actual.getContent().size());
    assertEquals(responseDto.id(), actual.getContent().getFirst().id());
    assertThat(actual.getContent().getFirst().total())
        .isEqualByComparingTo(responseDto.total());

    verify(saleRepository).findByDeletedAtIsNull(pageable);
    verify(detailSaleMapper).getDetailSaleListDto( anyList() );
    verify(saleMapper).toDto( any(Sale.class) , anyList() );
  }

  @Test
  void getAll_GivenPageableAndBranchId_ShouldReturnAPageOfSaleResponseDtoFilterByBranchId() {
    // arrange
    Pageable pageable =  PageRequest.of(0, 10);
    Long branchId = TestDataFactory.getDefaultBranchId();

    Sale entity = TestDataFactory.createDefaultSale();
    List<Sale> saleList = List.of(entity);
    Page<Sale> pageSales = new PageImpl<>(saleList, pageable,
        saleList.size());

    SaleResponseDto responseDto =
        TestDataFactory.createDefaultSaleResponseDto();
    List<SaleResponseDto> content = List.of(responseDto);

    List<DetailSaleResponseDto> detailSaleResponseDtoList =
        responseDto.detailSaleList();

    // mock
    when(saleRepository.findByDeletedAtIsNullAndBranchId(branchId, pageable))
        .thenReturn(pageSales);
    when(detailSaleMapper.getDetailSaleListDto( anyList() ))
        .thenReturn(detailSaleResponseDtoList);
    when(saleMapper.toDto( any(Sale.class) , anyList() ))
        .thenReturn(responseDto);

    // Act
    Page<SaleResponseDto> actual = saleService.getAll(pageable, branchId,
        null);

    // Assert
    assertFalse(actual.isEmpty());
    assertEquals(branchId, actual.getContent().getFirst().branchId());
    assertEquals(content.size(), actual.getContent().size());
    assertEquals(responseDto.id(), actual.getContent().getFirst().id());
    assertThat(actual.getContent().getFirst().total())
        .isEqualByComparingTo(responseDto.total());

    verify(saleRepository).findByDeletedAtIsNullAndBranchId(branchId, pageable);
    verify(detailSaleMapper).getDetailSaleListDto( anyList() );
    verify(saleMapper).toDto( any(Sale.class) , anyList() );
  }

  @Test
  void getAll_GivenPageableAndDate_ShouldReturnAPageOfSaleResponseDtoFilterByDate() {
    // arrange
    Pageable pageable =  PageRequest.of(0, 10);
    LocalDate date = TestDataFactory.getDefaultSaleDate();

    Sale entity = TestDataFactory.createDefaultSale();
    List<Sale> saleList = List.of(entity);
    Page<Sale> pageSales = new PageImpl<>(saleList, pageable,
        saleList.size());

    SaleResponseDto responseDto =
        TestDataFactory.createDefaultSaleResponseDto();
    List<SaleResponseDto> content = List.of(responseDto);

    List<DetailSaleResponseDto> detailSaleResponseDtoList =
        responseDto.detailSaleList();

    // mock
    when(saleRepository.findByDeletedAtIsNullAndDate(date, pageable))
        .thenReturn(pageSales);
    when(detailSaleMapper.getDetailSaleListDto( anyList() ))
        .thenReturn(detailSaleResponseDtoList);
    when(saleMapper.toDto( any(Sale.class) , anyList() ))
        .thenReturn(responseDto);

    // Act
    Page<SaleResponseDto> actual = saleService.getAll(pageable, null, date);

    // Assert
    assertFalse(actual.isEmpty());
    assertEquals(date, actual.getContent().getFirst().date());
    assertEquals(content.size(), actual.getContent().size());
    assertEquals(responseDto.id(), actual.getContent().getFirst().id());
    assertThat(actual.getContent().getFirst().total())
        .isEqualByComparingTo(responseDto.total());

    verify(saleRepository).findByDeletedAtIsNullAndDate(date, pageable);
    verify(detailSaleMapper).getDetailSaleListDto( anyList() );
    verify(saleMapper).toDto( any(Sale.class) , anyList() );
  }

  @Test
  void getAll_GivenAllArgs_ShouldReturnAPageOfSaleResponseDtoFilterByBranchIdAndDate() {
    // arrange
    Pageable pageable =  PageRequest.of(0, 10);
    Long branchId =  TestDataFactory.getDefaultBranchId();
    LocalDate date = TestDataFactory.getDefaultSaleDate();

    Sale entity = TestDataFactory.createDefaultSale();
    List<Sale> saleList = List.of(entity);
    Page<Sale> pageSales = new PageImpl<>(saleList, pageable,
        saleList.size());

    SaleResponseDto responseDto =
        TestDataFactory.createDefaultSaleResponseDto();
    List<SaleResponseDto> content = List.of(responseDto);

    List<DetailSaleResponseDto> detailSaleResponseDtoList =
        responseDto.detailSaleList();

    // mock
    when(saleRepository
        .findByDeletedAtIsNullAndBranchIdAndDate(branchId, date, pageable))
        .thenReturn(pageSales);
    when(detailSaleMapper.getDetailSaleListDto( anyList() ))
        .thenReturn(detailSaleResponseDtoList);
    when(saleMapper.toDto( any(Sale.class) , anyList() ))
        .thenReturn(responseDto);

    // Act
    Page<SaleResponseDto> actual = saleService.getAll(pageable, branchId,
        date);

    // Assert
    assertFalse(actual.isEmpty());
    assertEquals(branchId, actual.getContent().getFirst().branchId());
    assertEquals(date, actual.getContent().getFirst().date());
    assertEquals(content.size(), actual.getContent().size());
    assertEquals(responseDto.id(), actual.getContent().getFirst().id());
    assertThat(actual.getContent().getFirst().total())
        .isEqualByComparingTo(responseDto.total());

    verify(saleRepository)
        .findByDeletedAtIsNullAndBranchIdAndDate(branchId, date, pageable);
    verify(detailSaleMapper).getDetailSaleListDto( anyList() );
    verify(saleMapper).toDto( any(Sale.class) , anyList() );
  }

  @Test
  void getById_GivenValidId_ShouldReturnASaleResponseDto() {
    // arrange
    Long validId = TestDataFactory.getDefaultSaleId();
    Sale entity = TestDataFactory.createDefaultSale();
    ReflectionTestUtils.setField(entity, "id", validId);
    ReflectionTestUtils.setField(entity.getBranch(), "id", TestDataFactory.getDefaultBranchId());

    SaleResponseDto responseDto = TestDataFactory.createDefaultSaleResponseDto();
    List<DetailSaleResponseDto> detailSaleResponseDtoList = responseDto.detailSaleList();

    // mock
    when(saleRepository.findByIdAndDeletedAtIsNull(validId))
        .thenReturn(Optional.of(entity));
    when(detailSaleMapper.getDetailSaleListDto( entity.getDetailSaleList() ))
        .thenReturn( detailSaleResponseDtoList );
    when(saleMapper.toDto( any(Sale.class), anyList() ))
        .thenReturn(responseDto);

    // act
    SaleResponseDto actual = saleService.getById(validId);

    // assert
    assertNotNull(actual);
    assertEquals(validId, actual.id());
    assertEquals(entity.getTotal(), actual.total());
    assertEquals(entity.getBranch().getId(), actual.branchId());
    assertEquals(entity.getDate(), actual.date());
    assertEquals(entity.getSaleStatus(), actual.saleStatus());
    assertThat(actual.detailSaleList())
        .hasSize(detailSaleResponseDtoList.size())
        .containsExactlyInAnyOrderElementsOf(detailSaleResponseDtoList);

    verify(saleRepository).findByIdAndDeletedAtIsNull(validId);
    verify(detailSaleMapper).getDetailSaleListDto( entity.getDetailSaleList() );
    verify(saleMapper).toDto( any(Sale.class), anyList() );
  }

  @Test
  void getById_GivenInvalidId_ShouldThrowSaleNotFoundException() {
    // Arrange
    Long invalidId = -1L;

    // mock
    when(saleRepository.findByIdAndDeletedAtIsNull(invalidId))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(SaleNotFoundException.class,
        () ->
            saleService.getById(invalidId)
    );

    verify(saleRepository).findByIdAndDeletedAtIsNull(invalidId);
    verify(saleRepository, never()).save(any(Sale.class));
  }

  @Test
  void create_GivenValidSaleRequestDto_ShouldReturnSaleResponseDto() {
    // 1. ARRANGE
    SaleRequestDto requestDto = TestDataFactory.createDefaultSaleRequestDto();
    Sale entity = TestDataFactory.createDefaultSale();
    Branch branch = TestDataFactory.createDefaultBranch();
    Product product = TestDataFactory.createDefaultProduct();
    SaleResponseDto responseDto = TestDataFactory.createDefaultSaleResponseDto();

    // 2. MOCK (The refined version)
    when(branchRepository.findByIdAndDeletedAtIsNull(requestDto.branchId()))
        .thenReturn(Optional.of(branch));

    // Handle the loop for product lookups
    when(productRepository.findByIdAndDeletedAtIsNull(anyLong()))
        .thenReturn(Optional.of(product));

    // Fix the DetailSale mapping loop
    List<DetailSale> detailEntities = entity.getDetailSaleList();
    for (int i = 0; i < requestDto.detailSaleRequestDtoList().size(); i++) {
      when(detailSaleMapper.toEntity(requestDto.detailSaleRequestDtoList().get(i), product))
          .thenReturn(detailEntities.get(i));
    }

    when(saleMapper.toEntity(eq(branch), anyList())).thenReturn(entity);
    when(saleRepository.save(entity)).thenReturn(entity);
    when(saleMapper.toDto(eq(entity), anyList())).thenReturn(responseDto);

    // 3. ACT
    SaleResponseDto actual = saleService.create(requestDto);

    // 4. ASSERT (Deep Verification)
    assertNotNull(actual);
    assertEquals(responseDto.total(), actual.total());
    assertEquals(responseDto.detailSaleList().size(), actual.detailSaleList().size());

    verify(saleRepository).save(entity);
    verify(productRepository, times(requestDto.detailSaleRequestDtoList().size()))
        .findByIdAndDeletedAtIsNull(anyLong());
  }

  @Test
  void create_GivenSaleRequestDtoWithInvalidBranchId_ShouldThrowBranchNotFoundException() {
    // arrange
    SaleRequestDto requestDto = new SaleRequestDto(
        999L,
        List.of(
            TestDataFactory.createDefaultDetailSaleRequestDto(),
            TestDataFactory.createDefaultDetailSaleRequestDto())
    );
    Long invalidBranchId = requestDto.branchId();

    // mock
    when(branchRepository.findByIdAndDeletedAtIsNull(requestDto.branchId()))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(BranchNotFoundException.class,
        () ->
            saleService.create(requestDto)
    );
    verify(branchRepository).findByIdAndDeletedAtIsNull(invalidBranchId);

    verify(productRepository, never()).findByIdAndDeletedAtIsNull( anyLong() );
    verify(detailSaleMapper, never()).toEntity( any(DetailSaleRequestDto.class), any(Product.class));
    verify(saleMapper, never()).toEntity(any(Branch.class), anyList());
    verify(saleRepository, never()).save(any(Sale.class));
    verify(detailSaleMapper, never()).getDetailSaleListDto( anyList() );
    verify(saleMapper, never()).toDto(any(Sale.class), anyList());

  }

  @Test
  void create_GivenSaleRequestDtoWithInvalidProductId_ShouldThrowProductNotFoundException() {
    // arrange
    SaleRequestDto requestDto = new SaleRequestDto(
        TestDataFactory.getDefaultBranchId(),
        List.of(
            new DetailSaleRequestDto(
                5,
                999L
            ),
            TestDataFactory.createDefaultDetailSaleRequestDto()
        )
    );
    Long invalidProductId = requestDto.detailSaleRequestDtoList()
        .getFirst()
        .productId();

    Branch branch = TestDataFactory.createDefaultBranch();

    // mock
    when(branchRepository.findByIdAndDeletedAtIsNull(requestDto.branchId()))
        .thenReturn(Optional.of(branch));
    when(productRepository.findByIdAndDeletedAtIsNull(invalidProductId))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(ProductNotFoundException.class,
        () ->
            saleService.create(requestDto)
    );

    verify(branchRepository).findByIdAndDeletedAtIsNull(requestDto.branchId());
    verify(productRepository).findByIdAndDeletedAtIsNull( invalidProductId );

    verify(detailSaleMapper, never()).toEntity( any(DetailSaleRequestDto.class), any(Product.class));
    verify(saleMapper, never()).toEntity(any(Branch.class), anyList());
    verify(saleRepository, never()).save(any(Sale.class));
    verify(detailSaleMapper, never()).getDetailSaleListDto( anyList() );
    verify(saleMapper, never()).toDto(any(Sale.class), anyList());

  }

  @Test
  void delete_GivenValidId_ShouldSoftDeleteSale() {
    // arrange
    Long validId = TestDataFactory.getDefaultSaleId();
    Sale entity = TestDataFactory.createDefaultSale();
    ReflectionTestUtils.setField(entity, "id", validId);

    List<DetailSale> detailSaleList = entity.getDetailSaleList();

    // mock
    when(saleRepository.findByIdAndDeletedAtIsNull(validId))
        .thenReturn(Optional.of(entity));
    when(detailSaleRepository.findByDeletedAtIsNullAndSaleId(validId))
        .thenReturn(entity.getDetailSaleList());
    for(DetailSale ds :  detailSaleList) {
      when(detailSaleRepository.save(ds))
          .thenReturn(ds);
    }

    // act
    saleService.delete(validId);

    // assert
    assertNotNull(entity);
    assertTrue(entity.isDeleted());
    for (DetailSale ds :  detailSaleList) {
      assertTrue(ds.isDeleted());
    }
    assertEquals(validId, entity.getId());

    verify(saleRepository).findByIdAndDeletedAtIsNull(validId);
    verify(detailSaleRepository).findByDeletedAtIsNullAndSaleId(validId);
    for (DetailSale ds :  detailSaleList) {
      verify(detailSaleRepository).save(ds);
    }
    verify(saleRepository).save(entity);

  }

  @Test
  void delete_GivenInvalidId_ShouldThrowSaleNotFoundException() {
    // Arrange
    Long invalidId = -1L;

    // mock
    when(saleRepository.findByIdAndDeletedAtIsNull(invalidId))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(SaleNotFoundException.class,
        () ->
            saleService.delete(invalidId)
    );

    verify(saleRepository).findByIdAndDeletedAtIsNull(invalidId);
    verify(saleRepository, never()).save(any(Sale.class));
  }
}