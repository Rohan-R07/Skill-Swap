package com.example.backend.controller;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.ExchangeOfferResponse;
import com.example.backend.service.EmailAlreadyExistsException;
import com.example.backend.service.InvalidCredentialsException;
import com.example.backend.service.OfferAccessDeniedException;
import com.example.backend.service.OfferAlreadyProcessedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<AuthResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        AuthResponse response = new AuthResponse(false, ex.getMessage(), null, null, null, null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<AuthResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        AuthResponse response = new AuthResponse(false, ex.getMessage(), null, null, null, null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(OfferAlreadyProcessedException.class)
    public ResponseEntity<ExchangeOfferResponse> handleOfferAlreadyProcessed(OfferAlreadyProcessedException ex) {
        ExchangeOfferResponse response = new ExchangeOfferResponse();
        response.setSuccess(false);
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(OfferAccessDeniedException.class)
    public ResponseEntity<ExchangeOfferResponse> handleOfferAccessDenied(OfferAccessDeniedException ex) {
        ExchangeOfferResponse response = new ExchangeOfferResponse();
        response.setSuccess(false);
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExchangeOfferResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ExchangeOfferResponse response = new ExchangeOfferResponse();
        response.setSuccess(false);
        response.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
