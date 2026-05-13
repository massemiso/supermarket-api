package com.massemiso.supermarket_api.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(example = "ADMIN")
public enum RoleEnum {
  ADMIN,
  MANAGER,
  CASHIER,
  GUEST
}
