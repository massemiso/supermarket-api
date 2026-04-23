package com.massemiso.supermarket_api.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "sale")
@NoArgsConstructor
@Getter
public class Sale extends BaseEntityWithSoftDelete {
  @PastOrPresent
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // Expects YYYY-MM-DD
  private LocalDate date = getCreatedAt().toLocalDate();

  @Enumerated(EnumType.STRING)
  private SaleStatus saleStatus;
  private BigDecimal total;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branch_id")
  private Branch branch;

  @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DetailSale> detailSaleList;

  /***
   * Constructs a new Sale and performs initial business validations.
   * If deletedAt is null, saleStatus is set to REGISTERED, otherwise ANNULLED.
   * Calculates the total price of the sale by calculating the sum of the
   * total prices of the detail sales.
   * Also syncs each detail sale with this sale.
   *
   * @param branch on which this sale occurs.
   * @param detailSaleList list of detail sales associated with this sale.
   */
  @Builder
  public Sale(Branch branch, List<DetailSale> detailSaleList) {
    this.branch = branch;
    this.detailSaleList = detailSaleList;
    this.saleStatus = !isDeleted() ? SaleStatus.REGISTERED : SaleStatus.ANNULLED;
    this.total = detailSaleList.stream()
        .map(DetailSale::calculateTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    detailSaleList.forEach(item -> item.setSale(this));
  }

  @Override
  public void delete(){
    super.delete();
    this.saleStatus = SaleStatus.ANNULLED;
  }
}
