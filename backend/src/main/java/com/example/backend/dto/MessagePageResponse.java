package com.example.backend.dto;

import java.util.List;

public record MessagePageResponse(List<MessageResponse> messages, int page, int size,
                                  long totalElements, int totalPages, boolean hasNext) {}
