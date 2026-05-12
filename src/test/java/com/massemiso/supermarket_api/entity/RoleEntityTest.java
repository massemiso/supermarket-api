package com.massemiso.supermarket_api.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class RoleEntityTest {

  @Test
  void toAuthority_ShouldReturnSimpleGrantedAuthority_WithRoleEnum() {
    // given
    RoleEntity entity = RoleEntity.builder()
        .roleEnum(RoleEnum.ADMIN)
        .build();

    // when
    SimpleGrantedAuthority authority = entity.toAuthority();

    // then
    assertNotNull(authority);
    assertEquals("ROLE_ADMIN", authority.getAuthority());
  }
}