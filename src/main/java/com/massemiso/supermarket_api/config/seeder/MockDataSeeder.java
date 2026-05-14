package com.massemiso.supermarket_api.config.seeder;

import com.massemiso.supermarket_api.dto.BranchRequestDto;
import com.massemiso.supermarket_api.dto.BranchResponseDto;
import com.massemiso.supermarket_api.dto.DetailSaleRequestDto;
import com.massemiso.supermarket_api.dto.ProductRequestDto;
import com.massemiso.supermarket_api.dto.ProductResponseDto;
import com.massemiso.supermarket_api.dto.SaleRequestDto;
import com.massemiso.supermarket_api.repository.BranchRepository;
import com.massemiso.supermarket_api.service.BranchService;
import com.massemiso.supermarket_api.service.ProductService;
import com.massemiso.supermarket_api.service.SaleService;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
@PropertySource("classpath:mock-users.properties")
@Slf4j
@Order(2)
public class MockDataSeeder implements CommandLineRunner {
  private final BranchService branchService;
  private final BranchRepository branchRepository;
  private final ProductService productService;
  private final SaleService saleService;

  @Autowired
  public MockDataSeeder (
      BranchService branchService,
      BranchRepository branchRepository,
      ProductService productService,
      SaleService saleService
  ) {
    this.branchService = branchService;
    this.branchRepository = branchRepository;
    this.productService = productService;
    this.saleService = saleService;
  }

  @Override
  public void run(String... args) throws Exception {
    if (branchRepository.count() > 0) {
      log.info("DATA_SEEDER: Business data already seeded. Skipping...");
      return;
    }

    log.info("DATA_SEEDER: Seeding mock data (Branches, Products, Sales)...");

    // 1. Seed Branches
    BranchResponseDto branch1 =
        branchService.create(new BranchRequestDto(
            "Downtown Central",
            "123 Main St",
            "443-3211"));
    BranchResponseDto branch2 =
        branchService.create(new BranchRequestDto(
            "North Side Mall",
            "456 Avenue North",
            "121-3333"));

    // 2. Seed Products
    ProductResponseDto milk =
        productService.create(new ProductRequestDto(
            "Whole Milk 1L",
            "Dairy",
            BigDecimal.valueOf(1.50))
        );
    ProductResponseDto bread =
        productService.create(new ProductRequestDto(
            "Artisan Bread",
            "Grain",
            BigDecimal.valueOf(2.20))
        );

    // 3. Seed Sales with Relationships
    saleService.create(new SaleRequestDto(
                branch1.id(),
                List.of(
                    new DetailSaleRequestDto(100, milk.id()),
                    new DetailSaleRequestDto(5, bread.id()))
            )
        );
    saleService.create(new SaleRequestDto(
                branch1.id(),
                List.of(
                    new DetailSaleRequestDto(7, milk.id()),
                    new DetailSaleRequestDto(2, bread.id()))
            )
        );
    saleService.create(new SaleRequestDto(
                branch2.id(),
                List.of(
                    new DetailSaleRequestDto(7, milk.id()),
                    new DetailSaleRequestDto(200, bread.id()))
            )
        );

    log.info("DATA_SEEDER: Seeding complete. Created 2 Branches, 2 Products, and 3 Sales.");
  }
}
