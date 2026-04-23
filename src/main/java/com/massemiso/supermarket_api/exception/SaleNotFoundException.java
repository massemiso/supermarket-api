package com.massemiso.supermarket_api.exception;

/**
 * Exception thrown when a sale is not found.
 * This means the sale with the given id does not exist in the database.
 * OR the sale has been deleted.
 */
public class SaleNotFoundException extends RuntimeException {

  public SaleNotFoundException(Long id) {
    super("Sale with id " + id + " not found");
  }
}
