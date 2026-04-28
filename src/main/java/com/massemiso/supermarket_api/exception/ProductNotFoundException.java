package com.massemiso.supermarket_api.exception;

/**
 * Exception thrown when a product is not found.
 * This means the product with the given id does not exist in the database.
 * OR the product has been deleted.
 */
public class ProductNotFoundException extends RuntimeException {
  public ProductNotFoundException(Long id) {
    super("Product with id " + id + " not found");
  }

}
