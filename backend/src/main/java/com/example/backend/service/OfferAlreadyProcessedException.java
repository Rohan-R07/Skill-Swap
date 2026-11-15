package com.example.backend.service;

public class OfferAlreadyProcessedException extends RuntimeException {

    public OfferAlreadyProcessedException(String message) {
        super(message);
    }
}
