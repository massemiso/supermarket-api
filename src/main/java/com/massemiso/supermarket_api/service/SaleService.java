package com.massemiso.supermarket_api.service;

import com.massemiso.supermarket_api.dto.DetailSaleRequestDto;
import com.massemiso.supermarket_api.dto.SaleRequestDto;
import com.massemiso.supermarket_api.dto.SaleResponseDto;
import com.massemiso.supermarket_api.dto.mapper.DetailSaleMapper;
import com.massemiso.supermarket_api.dto.mapper.SaleMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
      page = saleRepository
          .findByDeletedAtIsNull(pageable)
          .map(sale -> saleMapper.toDto(
              sale,
              detailSaleMapper.getDetailSaleListDto(
                  sale.getDetailSaleList())));
    }
    else if(date == null){
      page = saleRepository
          .findByDeletedAtIsNullAndBranchId(branchId, pageable)
          .map(sale -> saleMapper.toDto(
              sale,
              detailSaleMapper.getDetailSaleListDto(
                  sale.getDetailSaleList())));
    }
    else if(branchId == null){
      page = saleRepository
          .findByDeletedAtIsNullAndDate(date, pageable)
          .map(sale -> saleMapper.toDto(
              sale,
              detailSaleMapper.getDetailSaleListDto(
                  sale.getDetailSaleList())));
    }
    else{
      page = saleRepository
          .findByDeletedAtIsNullAndBranchIdAndDate(branchId, date, pageable)
          .map(sale -> saleMapper.toDto(
              sale,
              detailSaleMapper.getDetailSaleListDto(
                  sale.getDetailSaleList())));
    }

    return page;
  }

  public SaleResponseDto getById(Long id) {
    Sale sale = findById(id);
    return saleMapper.toDto(
        sale,
        detailSaleMapper.getDetailSaleListDto(
            sale.getDetailSaleList()));
  }

  @Transactional
  public SaleResponseDto create(SaleRequestDto requestDto) {
    Branch branch = branchRepository
        .findByIdAndDeletedAtIsNull(requestDto.branchId())
        .orElseThrow(() -> new BranchNotFoundException(requestDto.branchId()));

    // sales in detailSaleList are empty => sale don't exist yet
    List<DetailSale> detailSaleList = this.getDetailSaleList(
        requestDto.detailSaleRequestDtoList());

    Sale entity = saleMapper.toEntity(branch, detailSaleList);
    entity = saleRepository.save(entity);

    return saleMapper.toDto(entity,
        detailSaleMapper.getDetailSaleListDto(detailSaleList));
  }

  @Transactional
  public void delete(Long id) {
    Sale entity = findById(id);
    entity.delete();
    List<DetailSale> detailSaleList = detailSaleRepository
        .findByDeletedAtIsNullAndSaleId(id);
    detailSaleList.forEach(ds -> {
      ds.delete();
      detailSaleRepository.save(ds);
    });

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
