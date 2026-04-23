package com.massemiso.supermarket_api.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@NoArgsConstructor
@Getter
public class Product extends BaseEntityWithSoftDelete {
  private String name;
  private String category;
  private BigDecimal actualPrice;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DetailSale> detailSaleList;

  @Builder
  public Product(String name, String category, BigDecimal actualPrice) {
    this.name = name;
    this.category = category;
    this.actualPrice = actualPrice;
  }

  /**
   * Updates the product with the given parameters.
   * The detailSaleList is excluded from manual updates to maintain data integrity; detailSales must be managed through the SaleService.
   * @param name The new name of the product.
   * @param category The new category of the product.
   * @param actualPrice The new price of the product.
   */
  public void update(String name, String category, BigDecimal actualPrice) {
    this.name = name;
    this.category = category;
    this.actualPrice = actualPrice;
  }
}
