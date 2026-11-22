package com.example.backend.service;

public class ChatAccessDeniedException extends RuntimeException {
    public ChatAccessDeniedException(String message) { super(message); }
}
