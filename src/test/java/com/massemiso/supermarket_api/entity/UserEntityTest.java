package com.massemiso.supermarket_api.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.massemiso.supermarket_api.util.TestDataFactory;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class UserEntityTest {

  @Test
  void getAuthorities_ShouldReturnASetOfSimpleGrantedAuthority_WithRoles() {
    // given
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    String role = "ROLE_".concat(TestDataFactory.getDEFAULT_USER_ROLE().name());

    // when
    Set<SimpleGrantedAuthority> authorities = entity.getAuthorities();

    // then
    assertNotNull(authorities);
    assertThat(authorities)
        .hasSize(1)
        .contains(new SimpleGrantedAuthority(role));
  }


  @Test
  void update_WhenGivenValidArgs_ShouldSetArgsCorrectly() {
    // Arrange
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    String oldUsername = entity.getUsername(); // should not change
    String newPassword = "new_password";
    String newEmail = "new.email@example.com";
    RoleEntity newRole = RoleEntity.builder().roleEnum(RoleEnum.MANAGER).build();

    // Act
    entity.update(
        newPassword,
        newEmail,
        Set.of(newRole)
    );

    // Assert
    assertNotNull(entity);
    assertEquals(oldUsername, entity.getUsername());
    assertEquals(newPassword, entity.getPassword());
    assertEquals(newEmail, entity.getEmail());
    assertThat(entity.getRoles())
        .isNotNull()
        .hasSize(1)
        .contains(newRole);
  }

  @Test
  void update_WhenGivenNullArgs_ShouldKeepOldData() {
    // Arrange
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    String oldUsername = entity.getUsername(); // should not change
    String oldPassword = entity.getPassword();
    String oldEmail = entity.getEmail();
    RoleEntity oldRole = entity.getRoles().iterator().next();

    // Act
    entity.update(
        null,
        null,
        null
    );

    // Assert
    assertNotNull(entity);
    assertEquals(oldUsername, entity.getUsername());
    assertEquals(oldPassword, entity.getPassword());
    assertEquals(oldEmail, entity.getEmail());
    assertThat(entity.getRoles())
        .isNotNull()
        .hasSize(1)
        .contains(oldRole);
  }

  @Test
  void update_WhenGivenPasswordNull_ShouldKeepOldDataAndSetValidArgsCorrectly() {
    // Arrange
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    String oldUsername = entity.getUsername(); // should not change
    String oldPassword = entity.getPassword();
    String newEmail = "new.email@example.com";
    RoleEntity newRole = RoleEntity.builder().roleEnum(RoleEnum.MANAGER).build();

    // Act
    entity.update(
        null,
        newEmail,
        Set.of(newRole)
    );

    // Assert
    assertNotNull(entity);
    assertEquals(oldUsername, entity.getUsername());
    assertEquals(oldPassword, entity.getPassword());
    assertEquals(newEmail, entity.getEmail());
    assertThat(entity.getRoles())
        .isNotNull()
        .hasSize(1)
        .contains(newRole);
  }

  @Test
  void update_WhenGivenPasswordBlank_ShouldKeepOldDataAndSetValidArgsCorrectly() {
    // Arrange
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    String oldUsername = entity.getUsername(); // should not change
    String oldPassword = entity.getPassword();
    String newEmail = "new.email@example.com";
    RoleEntity newRole = RoleEntity.builder().roleEnum(RoleEnum.MANAGER).build();

    // Act
    entity.update(
        "",
        newEmail,
        Set.of(newRole)
    );

    // Assert
    assertNotNull(entity);
    assertEquals(oldUsername, entity.getUsername());
    assertEquals(oldPassword, entity.getPassword());
    assertEquals(newEmail, entity.getEmail());
    assertThat(entity.getRoles())
        .isNotNull()
        .hasSize(1)
        .contains(newRole);
  }

  @Test
  void update_WhenGivenEmailNull_ShouldKeepOldDataAndSetValidArgsCorrectly() {
    // Arrange
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    String oldUsername = entity.getUsername(); // should not change
    String newPassword = "new_password";
    String oldEmail = entity.getEmail();
    RoleEntity newRole = RoleEntity.builder().roleEnum(RoleEnum.MANAGER).build();

    // Act
    entity.update(
        newPassword,
        null,
        Set.of(newRole)
    );

    // Assert
    assertNotNull(entity);
    assertEquals(oldUsername, entity.getUsername());
    assertEquals(newPassword, entity.getPassword());
    assertEquals(oldEmail, entity.getEmail());
    assertThat(entity.getRoles())
        .isNotNull()
        .hasSize(1)
        .contains(newRole);
  }

  @Test
  void update_WhenGivenEmailBlank_ShouldKeepOldDataAndSetValidArgsCorrectly() {
    // Arrange
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    String oldUsername = entity.getUsername(); // should not change
    String newPassword = "new_password";
    String oldEmail = entity.getEmail();
    RoleEntity newRole = RoleEntity.builder().roleEnum(RoleEnum.MANAGER).build();

    // Act
    entity.update(
        newPassword,
        "",
        Set.of(newRole)
    );

    // Assert
    assertNotNull(entity);
    assertEquals(oldUsername, entity.getUsername());
    assertEquals(newPassword, entity.getPassword());
    assertEquals(oldEmail, entity.getEmail());
    assertThat(entity.getRoles())
        .isNotNull()
        .hasSize(1)
        .contains(newRole);
  }

  @Test
  void update_WhenGivenRolesNull_ShouldKeepOldDataAndSetValidArgsCorrectly() {
    // Arrange
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    String oldUsername = entity.getUsername(); // should not change
    String newPassword = "new_password";
    String newEmail = "new.email@example.com";
    Set<RoleEntity> oldRoles = entity.getRoles();

    // Act
    entity.update(
        newPassword,
        newEmail,
        null
    );

    // Assert
    assertNotNull(entity);
    assertEquals(oldUsername, entity.getUsername());
    assertEquals(newPassword, entity.getPassword());
    assertEquals(newEmail, entity.getEmail());
    assertThat(entity.getRoles())
        .isNotNull()
        .hasSize(oldRoles.size())
        .contains(oldRoles.stream().findFirst().orElseThrow());
  }

  @Test
  void update_WhenGivenRolesEmpty_ShouldKeepOldDataAndSetValidArgsCorrectly() {
    // Arrange
    UserEntity entity = TestDataFactory.createDefaultUserEntity();
    String oldUsername = entity.getUsername(); // should not change
    String newPassword = "new_password";
    String newEmail = "new.email@example.com";
    Set<RoleEntity> oldRoles = entity.getRoles();

    // Act
    entity.update(
        newPassword,
        newEmail,
        Set.of()
    );

    // Assert
    assertNotNull(entity);
    assertEquals(oldUsername, entity.getUsername());
    assertEquals(newPassword, entity.getPassword());
    assertEquals(newEmail, entity.getEmail());
    assertThat(entity.getRoles())
        .isNotNull()
        .hasSize(oldRoles.size())
        .contains(oldRoles.stream().findFirst().orElseThrow());
  }

}