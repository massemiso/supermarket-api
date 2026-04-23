package com.massemiso.supermarket_api.exception;

/**
 * Exception thrown when a branch is not found.
 * This means the branch with the given id does not exist in the database.
 * OR the branch has been deleted.
 */
public class BranchNotFoundException extends RuntimeException {

  public BranchNotFoundException(Long id) {
    super("Branch with id " + id + " not found");
  }
}
