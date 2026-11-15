package com.example.backend.service;

public class OfferAccessDeniedException extends RuntimeException {

    public OfferAccessDeniedException(String message) {
        super(message);
    }
}
