package com.reactivespring.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalErrorHandlerController {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<String> handleWebExchangeBindException(WebExchangeBindException ex) {
        log.error("Exception Caught in handleWebExchangeBindException is: {}", ex.getMessage(), ex);

        var errorMessages = ex.getBindingResult().getAllErrors().stream()
                // Versión simplficada de la expresión "objectError -> objectError.getDefaultMessage()"
                .map(ObjectError::getDefaultMessage)
                .sorted()
                .collect(Collectors.joining(","));

        log.error("Error messages: {}", errorMessages);

        return ResponseEntity.badRequest().body(errorMessages);
    }
}
