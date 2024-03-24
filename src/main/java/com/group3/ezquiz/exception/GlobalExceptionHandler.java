package com.group3.ezquiz.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  // @ExceptionHandler(RuntimeException.class)
  // public ResponseEntity<?> handleRuntimeException(
  // RuntimeException exception) {
  // return new ResponseEntity<>(
  // "An error occured! (Runtime Error Handled.)",
  // HttpStatus.INTERNAL_SERVER_ERROR);
  // }

  @ExceptionHandler(ResourceNotFoundException.class)
  public String handleResourceNotFoundException(
      ResourceNotFoundException exception, Model model) {
    model.addAttribute("message", exception.getMessage());
    return "error/404";
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public String handleMissingServletRequestParameterException(
      MissingServletRequestParameterException exception, Model model) {
    model.addAttribute("message", exception.getMessage());
    return "error/404";
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorDetails> handleBindException(
      BindException exception,
      WebRequest webRequest) {
    Map<String, String> errors = new HashMap<>();
    exception.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField() + "Error";
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    return ResponseEntity.badRequest().body(
        ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .message("An error occured!")
            .details(webRequest.getDescription(false))
            .errors(errors)
            .build());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorDetails> handleDataIntegrityViolation(
      DataIntegrityViolationException ex,
      WebRequest webRequest) {

    return new ResponseEntity<>(
        ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .message("Data Integrity Violation: " + ex.getMessage())
            .details(webRequest.getDescription(false))
            .build(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidEmailException.class)
  public ResponseEntity<ErrorDetails> handleInvalidEmailException(
      InvalidEmailException ex,
      WebRequest webRequest) {
    return new ResponseEntity<>(
        ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .message("Invalid Email Exception.")
            .details(webRequest.getDescription(false))
            .errors(Map.of("emailError", ex.getMessage()))
            .build(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidQuestionException.class)
  public ResponseEntity<ErrorDetails> handleInvalidQuestionException(
      InvalidQuestionException ex,
      WebRequest webRequest) {
    return new ResponseEntity<>(
        ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .message("Invalid Question Exception.")
            .details(webRequest.getDescription(false))
            .errors(Map.of("questionError", ex.getMessage()))
            .build(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidClassroomException.class)
  public ResponseEntity<ErrorDetails> handleInvalidClassroomException(
      InvalidClassroomException ex,
      WebRequest webRequest) {
    return new ResponseEntity<>(
        ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .message("Invalid Classroom Exception.")
            .details(webRequest.getDescription(false))
            .errors(Map.of("nameError", ex.getMessage()))
            .build(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidAttemptException.class)
  public ResponseEntity<?> handleInvalidAttemptException(
      InvalidAttemptException ex) {
    return new ResponseEntity<>(
        ErrorDetails.builder()
            .timestamp(LocalDateTime.now())
            .message(ex.getMessage())
            .build(),
        HttpStatus.CONFLICT);
  }

}
