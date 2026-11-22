package com.example.backend.service;

import com.example.backend.dto.ConversationResponse;
import com.example.backend.dto.MessagePageResponse;
import com.example.backend.dto.MessageResponse;
import com.example.backend.dto.SendMessageRequest;
import com.example.backend.model.Conversation;
import com.example.backend.model.ExchangeOffer;
import com.example.backend.model.Message;
import com.example.backend.model.OfferStatus;
import com.example.backend.model.User;
import com.example.backend.repository.ConversationRepository;
import com.example.backend.repository.ExchangeOfferRepository;
import com.example.backend.repository.MessageRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ExchangeOfferRepository exchangeOfferRepository;
    private final UserRepository userRepository;

    public ChatService(ConversationRepository conversationRepository, MessageRepository messageRepository,
                       ExchangeOfferRepository exchangeOfferRepository, UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.exchangeOfferRepository = exchangeOfferRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Conversation ensureConversation(Long offerId) {
        ExchangeOffer offer = exchangeOfferRepository.findByIdForUpdate(offerId)
                .orElseThrow(() -> new ChatNotFoundException("Exchange offer not found"));
        if (offer.getStatus() != OfferStatus.ACCEPTED) {
            throw new ChatAccessDeniedException("Chat is available only for accepted exchange offers");
        }
        return conversationRepository.findByExchangeOfferId(offerId).orElseGet(() ->
                conversationRepository.save(new Conversation(offer, offer.getSender(), offer.getReceiver())));
    }

    @Transactional
    public List<ConversationResponse> getConversations(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ChatNotFoundException("User not found"));

        // Backfill conversations for accepted offers created before chat was added.
        List<ExchangeOffer> acceptedOffers = exchangeOfferRepository.findAcceptedByParticipant(userId);
        if (!acceptedOffers.isEmpty()) {
            List<Long> offerIds = acceptedOffers.stream().map(ExchangeOffer::getId).toList();
            var existingOfferIds = new HashSet<>(conversationRepository.findExistingOfferIds(offerIds));
            acceptedOffers.stream().map(ExchangeOffer::getId).filter(id -> !existingOfferIds.contains(id))
                    .forEach(this::ensureConversation);
        }

        List<Conversation> conversations = conversationRepository.findForParticipant(userId);
        List<Long> conversationIds = conversations.stream().map(Conversation::getId).toList();
        Map<Long, Message> latestMessages = new HashMap<>();
        if (!conversationIds.isEmpty()) {
            messageRepository.findLatestForConversations(conversationIds)
                    .forEach(message -> latestMessages.put(message.getConversation().getId(), message));
        }

        return conversations.stream().map(conversation -> {
            User other = conversation.getParticipantOne().getId().equals(user.getId())
                    ? conversation.getParticipantTwo() : conversation.getParticipantOne();
            Message latest = latestMessages.get(conversation.getId());
            return new ConversationResponse(conversation.getId(), other.getId(), other.getName(), other.getBio(),
                    conversation.getExchangeOffer().getId(),
                    conversation.getExchangeOffer().getOfferedSkill().getName(),
                    conversation.getExchangeOffer().getRequestedSkill().getName(),
                    latest == null ? null : latest.getContent(),
                    latest == null ? null : latest.getCreatedAt(), conversation.getUpdatedAt());
        }).toList();
    }

    @Transactional(readOnly = true)
    public MessagePageResponse getMessages(Long conversationId, Long userId, int page, int size) {
        Conversation conversation = requireParticipant(conversationId, userId);
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        Page<Message> result = messageRepository.findByConversationId(conversation.getId(),
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id"))));
        List<MessageResponse> messages = new ArrayList<>(result.getContent().stream().map(this::toResponse).toList());
        Collections.reverse(messages);
        return new MessagePageResponse(messages, result.getNumber(), result.getSize(), result.getTotalElements(),
                result.getTotalPages(), result.hasNext());
    }

    @Transactional
    public MessageResponse sendMessage(Long conversationId, SendMessageRequest request) {
        Conversation conversation = requireParticipant(conversationId, request.senderId());
        String content = request.content().trim();
        if (content.isEmpty()) throw new IllegalArgumentException("Message content cannot be blank");
        if (content.length() > 2000) throw new IllegalArgumentException("Message cannot exceed 2000 characters");
        User sender = userRepository.findById(request.senderId())
                .orElseThrow(() -> new ChatNotFoundException("Sender not found"));

        Message saved = messageRepository.save(new Message(conversation, sender, content));
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        return toResponse(saved);
    }

    private Conversation requireParticipant(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ChatNotFoundException("Conversation not found"));
        if (!conversation.getParticipantOne().getId().equals(userId)
                && !conversation.getParticipantTwo().getId().equals(userId)) {
            throw new ChatAccessDeniedException("You are not a participant in this conversation");
        }
        return conversation;
    }

    private MessageResponse toResponse(Message message) {
        return new MessageResponse(message.getId(), message.getConversation().getId(), message.getSender().getId(),
                message.getSender().getName(), message.getContent(), message.getCreatedAt());
    }
}
