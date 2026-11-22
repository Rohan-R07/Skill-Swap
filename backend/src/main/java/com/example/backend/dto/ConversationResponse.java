package com.example.backend.dto;

import java.time.LocalDateTime;

public record ConversationResponse(Long id, Long otherParticipantId, String otherParticipantName,
                                   String otherParticipantBio, Long exchangeOfferId,
                                   String offeredSkillName, String requestedSkillName,
                                   String latestMessage, LocalDateTime latestMessageAt,
                                   LocalDateTime updatedAt) {}
