package com.massemiso.supermarket_api.service;

import com.massemiso.supermarket_api.dto.DetailSaleMapper;
import com.massemiso.supermarket_api.dto.DetailSaleRequestDto;
import com.massemiso.supermarket_api.dto.SaleMapper;
import com.massemiso.supermarket_api.dto.SaleRequestDto;
import com.massemiso.supermarket_api.dto.SaleResponseDto;
import com.massemiso.supermarket_api.entity.Branch;
import com.massemiso.supermarket_api.entity.DetailSale;
import com.massemiso.supermarket_api.entity.Sale;
import com.massemiso.supermarket_api.exception.BranchNotFoundException;
import com.massemiso.supermarket_api.exception.ProductNotFoundException;
import com.massemiso.supermarket_api.exception.SaleNotFoundException;
import com.massemiso.supermarket_api.repository.BranchRepository;
import com.massemiso.supermarket_api.repository.DetailSaleRepository;
import com.massemiso.supermarket_api.repository.ProductRepository;
import com.massemiso.supermarket_api.repository.SaleRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SaleService {

  private final SaleRepository saleRepository;
  private final ProductRepository productRepository;
  private final BranchRepository branchRepository;
  private final DetailSaleRepository detailSaleRepository;
  private final SaleMapper saleMapper;
  private final DetailSaleMapper detailSaleMapper;


  @Autowired
  public SaleService(
      SaleRepository saleRepository,
      ProductRepository productRepository,
      BranchRepository branchRepository,
      DetailSaleRepository detailSaleRepository,
      SaleMapper saleMapper,
      DetailSaleMapper detailSaleMapper){
    this.saleRepository = saleRepository;
    this.productRepository = productRepository;
    this.detailSaleRepository = detailSaleRepository;
    this.branchRepository = branchRepository;
    this.saleMapper = saleMapper;
    this.detailSaleMapper = detailSaleMapper;
  }

  public Page<SaleResponseDto> getAll(Pageable pageable, Long branchId, LocalDate date) {
    Page<SaleResponseDto> page;
    if (branchId == null && date == null) {
      log.info("Attempting to return all sales");
      page = saleRepository
          .findByDeletedAtIsNull(pageable)
          .map(sale -> saleMapper.toDto(
              sale,
              detailSaleMapper.getDetailSaleListDto(
                  sale.getDetailSaleList())));
    }
    else if(date == null){
      log.info("Attempting to return all sales by branchId: {}", branchId);
      page = saleRepository
          .findByDeletedAtIsNullAndBranchId(branchId, pageable)
          .map(sale -> saleMapper.toDto(
              sale,
              detailSaleMapper.getDetailSaleListDto(
                  sale.getDetailSaleList())));
    }
    else if(branchId == null){
      log.info("Attempting to return all sales by date: {}", date);
      page = saleRepository
          .findByDeletedAtIsNullAndDate(date, pageable)
          .map(sale -> saleMapper.toDto(
              sale,
              detailSaleMapper.getDetailSaleListDto(
                  sale.getDetailSaleList())));
    }
    else{
      log.info("Attempting to return all sales by branchId and date: {}, {}",
          branchId, date);
      page = saleRepository
          .findByDeletedAtIsNullAndBranchIdAndDate(branchId, date, pageable)
          .map(sale -> saleMapper.toDto(
              sale,
              detailSaleMapper.getDetailSaleListDto(
                  sale.getDetailSaleList())));
    }

    log.info("Returning page {} of sales with total {} elements",
        page.getNumber(), page.getTotalElements());
    return page;
  }

  public SaleResponseDto getById(Long id) {
    log.info("Attempting to get sale by id: {}", id);
    Sale sale = findById(id);

    SaleResponseDto dto = saleMapper.toDto(
        sale,
        detailSaleMapper.getDetailSaleListDto(
            sale.getDetailSaleList()));
    log.info("Returning sale: {}", dto);
    return dto;
  }

  @Transactional
  public SaleResponseDto create(SaleRequestDto requestDto) {
    log.info("Attempting to create sale: {}", requestDto);
    Branch branch = branchRepository
        .findByIdAndDeletedAtIsNull(requestDto.branchId())
        .orElseThrow(() -> new BranchNotFoundException(requestDto.branchId()));

    // sales in detailSaleList are empty => sale don't exist yet
    List<DetailSale> detailSaleList = this.getDetailSaleList(
        requestDto.detailSaleRequestDtoList());

    Sale entity = saleMapper.toEntity(branch, detailSaleList);
    entity = saleRepository.save(entity);

    SaleResponseDto dto = saleMapper.toDto(entity,
        detailSaleMapper.getDetailSaleListDto(detailSaleList));
    log.info("Successfully created sale: {}", dto);
    return dto;
  }

  @Transactional
  public void delete(Long id) {
    log.info("Attempting to soft delete sale by id: {}", id);
    Sale entity = findById(id);
    entity.delete();
    List<DetailSale> detailSaleList = detailSaleRepository
        .findByDeletedAtIsNullAndSaleId(id);
    detailSaleList.forEach(ds -> {
      ds.delete();
      detailSaleRepository.save(ds);
    });

    log.info("Successfully soft deleted sale: {}", entity);
    saleRepository.save(entity);
  }

  private Sale findById(Long id){
    return saleRepository
        .findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new SaleNotFoundException(id));
  }

  private List<DetailSale> getDetailSaleList(List<DetailSaleRequestDto> detailSaleRequestDtoList){
    return detailSaleRequestDtoList.stream()
        .map(ds -> detailSaleMapper.toEntity(
            ds,
            productRepository
                .findByIdAndDeletedAtIsNull(ds.productId())
                .orElseThrow(() -> new ProductNotFoundException(ds.productId()))))
        .toList();
  }

}
