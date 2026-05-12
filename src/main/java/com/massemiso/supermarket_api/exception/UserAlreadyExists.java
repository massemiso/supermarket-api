package com.massemiso.supermarket_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExists extends RuntimeException{
  public UserAlreadyExists(String username){
    super("User with username '" + username + "' already exists.");
  }
}
