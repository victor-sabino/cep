package com.desafio.cep.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCepException.class)
    public ResponseEntity<String> handleInvalidCep(InvalidCepException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<String> handleExternalApiError(ExternalApiException ex) {
        return ResponseEntity.status(502).body("Erro na API externa: " + ex.getMessage());
    }
}
