package com.group3.ezquiz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidUserException extends UsernameNotFoundException {
  public InvalidUserException(String message) {
    super(message);
  }
}