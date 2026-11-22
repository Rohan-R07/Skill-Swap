package com.example.backend.service;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(String message) { super(message); }
}
