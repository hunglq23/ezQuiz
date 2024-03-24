package com.group3.ezquiz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidAttemptException extends RuntimeException {
  public InvalidAttemptException(String message) {
    super(message);
  }
}
