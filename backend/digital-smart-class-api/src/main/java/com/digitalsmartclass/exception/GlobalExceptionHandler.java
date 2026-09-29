package com.digitalsmartclass.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> invalid(MethodArgumentNotValidException e) {
    Map<String, String> fields = e.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(f -> f.getField(), f -> String.valueOf(f.getDefaultMessage()), (a, b) -> a));
    return ResponseEntity.badRequest().body(Map.of("error", "Validation failed", "fields", fields));
  }
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, String>> conflict(DataIntegrityViolationException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "This record already exists or violates a rule."));
  }
}
