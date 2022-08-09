package com.reactivespring.movieinfoservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {
  @ExceptionHandler(WebExchangeBindException.class)
  public ResponseEntity<String> handleRequestBodyError(WebExchangeBindException ex) {
    log.error("Exception Caught in handleRequestBodyError : {}", ex.getMessage(), ex);
    var errors =
        ex.getBindingResult().getAllErrors().stream()
            .filter(Objects::nonNull)
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .sorted()
            .collect(Collectors.joining(","));
    log.error("Error is : {}", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }
}
