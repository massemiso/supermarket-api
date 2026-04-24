package com.massemiso.supermarket_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "detail_sale")
@NoArgsConstructor
@Getter
public class DetailSale extends BaseEntityWithSoftDelete {
  private Integer quantity;
  private BigDecimal unitPrice;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "sale_id")
  @Setter
  private Sale sale;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id")
  private Product product;

  @Builder
  public DetailSale(Integer quantity, Product product) {
    if (quantity < 1)
      throw new ArithmeticException("Quantity cannot be negative");
    this.quantity = quantity;
    this.unitPrice = product.getActualPrice();
    this.product = product;
  }

  public BigDecimal calculateTotal() {
    return BigDecimal.valueOf(this.quantity).multiply(this.unitPrice);
  }
}
