package com.massemiso.supermarket_api.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import com.massemiso.supermarket_api.util.TestDataFactory;
import org.junit.jupiter.api.Test;

class BranchTest {

  @Test
  void update_GivenValidArgs_ShouldSetOwnOnesCorrectly() {
    // Arrange
    Branch branch = TestDataFactory.createDefaultBranch();

    // Act
    branch.update(
        "New Branch Name",
        "New Branch Address",
        "999999");

    // Assert
    assertNotNull(branch);
    assertEquals("New Branch Name", branch.getName());
    assertEquals("New Branch Address", branch.getAddress());
    assertEquals("999999", branch.getPhoneNumber());
    assertThat(branch.getSaleList())
        .isNullOrEmpty();
  }

  @Test
  void update_GivenNullArgs_ShouldKeepOldData() {
    // Arrange
    Branch branch = TestDataFactory.createDefaultBranch();
    String oldName = branch.getName();
    String oldAddress = branch.getAddress();
    String oldPhoneNumber = branch.getPhoneNumber();

    // Act
    branch.update(
        null,
        null,
        null);

    // Assert
    assertNotNull(branch);
    assertThat(branch.getName())
        .as("Name should remain unchanged when null is passed")
        .isNotNull()
        .isEqualTo(oldName);
    assertThat(branch.getAddress())
        .as("Address should remain unchanged when null is passed")
        .isNotNull()
        .isEqualTo(oldAddress);
    assertThat(branch.getPhoneNumber())
        .as("Phone number should remain unchanged when null is passed")
        .isNotNull()
        .isEqualTo(oldPhoneNumber);
    assertThat(branch.getSaleList())
        .isNullOrEmpty();

  }

  @Test
  void update_GivenNameNull_ShouldKeepOldDataAndUpdateWithValidArgs() {
    // Arrange
    Branch branch = TestDataFactory.createDefaultBranch();
    String oldName = branch.getName();
    String newAddress = "New Address";
    String newPhoneNumber = "123456789";

    // Act
    branch.update(
        null,
        newAddress,
        newPhoneNumber);

    // Assert
    assertNotNull(branch);
    assertThat(branch.getName())
        .as("Name should remain unchanged when null is passed")
        .isNotNull()
        .isEqualTo(oldName);
    assertThat(branch.getAddress())
        .as("Address SHOULD CHANGE when a valid arg is passed")
        .isNotNull()
        .isEqualTo(newAddress);
    assertThat(branch.getPhoneNumber())
        .as("Phone Number SHOULD CHANGE when a valid arg is passed")
        .isNotNull()
        .isEqualTo(newPhoneNumber);
    assertThat(branch.getSaleList())
        .isNullOrEmpty();
  }

  @Test
  void update_GivenNameBlank_ShouldKeepOldDataAndUpdateWithValidArgs() {
    // Arrange
    Branch branch = TestDataFactory.createDefaultBranch();
    String oldName = branch.getName();
    String newAddress = "New Address";
    String newPhoneNumber = "123456789";

    // Act
    branch.update(
        "",
        newAddress,
        newPhoneNumber);

    // Assert
    assertNotNull(branch);
    assertThat(branch.getName())
        .as("Name should remain unchanged when no characters are passed")
        .isNotNull()
        .isEqualTo(oldName);
    assertThat(branch.getAddress())
        .as("Address SHOULD CHANGE when a valid arg is passed")
        .isNotNull()
        .isEqualTo(newAddress);
    assertThat(branch.getPhoneNumber())
        .as("Phone Number SHOULD CHANGE when a valid arg is passed")
        .isNotNull()
        .isEqualTo(newPhoneNumber);
    assertThat(branch.getSaleList())
        .isNullOrEmpty();
  }

  @Test
  void update_GivenCategoryNull_ShouldKeepOldDataAndUpdateWithValidArgs() {
    // Arrange
    Branch branch = TestDataFactory.createDefaultBranch();
    String newName = "New name";
    String oldAddress = branch.getAddress();
    String newPhoneNumber = "123456789";

    // Act
    branch.update(
        newName,
        null,
        newPhoneNumber);

    // Assert
    assertNotNull(branch);
    assertThat(branch.getName())
        .as("Name SHOULD CHANGE when a valid arg is passed")
        .isNotNull()
        .isEqualTo(newName);
    assertThat(branch.getAddress())
        .as("Address should remain unchanged when a null is passed")
        .isNotNull()
        .isEqualTo(oldAddress);
    assertThat(branch.getPhoneNumber())
        .as("Phone Number SHOULD CHANGE when a valid arg is passed")
        .isNotNull()
        .isEqualTo(newPhoneNumber);
    assertThat(branch.getSaleList())
        .isNullOrEmpty();
  }

  @Test
  void update_GivenCategoryBlank_ShouldKeepOldDataAndUpdateWithValidArgs() {
    // Arrange
    Branch branch = TestDataFactory.createDefaultBranch();
    String newName = "New name";
    String oldAddress = branch.getAddress();
    String newPhoneNumber = "123456789";

    // Act
    branch.update(
        newName,
        "",
        newPhoneNumber);

    // Assert
    assertNotNull(branch);
    assertThat(branch.getName())
        .as("Name SHOULD CHANGE when a valid arg is passed")
        .isNotNull()
        .isEqualTo(newName);
    assertThat(branch.getAddress())
        .as("Address should remain unchanged when no characters are passed")
        .isNotNull()
        .isEqualTo(oldAddress);
    assertThat(branch.getPhoneNumber())
        .as("Phone Number SHOULD CHANGE when a valid arg is passed")
        .isNotNull()
        .isEqualTo(newPhoneNumber);
    assertThat(branch.getSaleList())
        .isNullOrEmpty();
  }
  @Test
  void update_GivenPhoneNumberNull_ShouldKeepOldDataAndUpdateWithValidArgs() {
    // Arrange
    Branch branch = TestDataFactory.createDefaultBranch();
    String newName = "New name";
    String newAddress = "New address";
    String oldPhoneNumber = branch.getPhoneNumber();

    // Act
    branch.update(
        newName,
        newAddress,
        null);

    // Assert
    assertNotNull(branch);
    assertThat(branch.getName())
        .as("Name SHOULD CHANGE when a valid arg is passed")
        .isNotNull()
        .isEqualTo(newName);
    assertThat(branch.getAddress())
        .as("Address SHOULD CHANGE when a valid arg is passed")
        .isNotNull()
        .isEqualTo(newAddress);
    assertThat(branch.getPhoneNumber())
        .as("Phone Number should remain unchanged when null is "
            + "passed")
        .isNotNull()
        .isEqualTo(oldPhoneNumber);
    assertThat(branch.getSaleList())
        .isNullOrEmpty();
  }

  @Test
  void update_GivenPhoneNumberBlank_ShouldKeepOldDataAndUpdateWithValidArgs() {
    // Arrange
    Branch branch = TestDataFactory.createDefaultBranch();
    String newName = "New name";
    String newAddress = "New address";
    String oldPhoneNumber = branch.getPhoneNumber();

    // Act
    branch.update(
        newName,
        newAddress,
        "");

    // Assert
    assertNotNull(branch);
    assertThat(branch.getName())
        .as("Name SHOULD CHANGE when a valid arg is passed")
        .isNotNull()
        .isEqualTo(newName);
    assertThat(branch.getAddress())
        .as("Address SHOULD CHANGE when a valid arg is passed")
        .isNotNull()
        .isEqualTo(newAddress);
    assertThat(branch.getPhoneNumber())
        .as("Phone Number should remain unchanged when no characters are "
            + "passed")
        .isNotNull()
        .isEqualTo(oldPhoneNumber);
    assertThat(branch.getSaleList())
        .isNullOrEmpty();
  }
}