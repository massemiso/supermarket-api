package com.massemiso.supermarket_api.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "branch")
@NoArgsConstructor
@Getter
public class Branch extends BaseEntityWithSoftDelete{
  private String name;
  private String address;
  private String phoneNumber;

  @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Sale> saleList;

  @Builder
  public Branch(String name, String address, String phoneNumber) {
    this.name = name;
    this.address = address;
    this.phoneNumber = phoneNumber;
  }

  /**
   * Updates the branch with the given parameters.
   * The saleList is excluded from manual updates to maintain data integrity; sales must be managed through the SaleService.
   * @param name The new name of the branch.
   * @param address The new physical address of the branch.
   * @param phoneNumber The new phone number of the branch.
   */
  public void update(String name, String address, String phoneNumber) {
    if(name != null && !name.isBlank())
      this.name = name;
    if(address != null && !address.isBlank())
      this.address = address;
    if(phoneNumber != null && !phoneNumber.isBlank())
      this.phoneNumber = phoneNumber;
  }
}
