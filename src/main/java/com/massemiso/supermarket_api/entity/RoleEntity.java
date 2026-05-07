package com.massemiso.supermarket_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sec_role")
@NoArgsConstructor
@Getter
public class RoleEntity extends BaseEntityWithSoftDelete {

  @Column(name = "name", unique = true, nullable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  private RoleEnum roleEnum;

  @Builder
  public RoleEntity(RoleEnum roleEnum) {
    this.roleEnum = roleEnum;
  }

}
