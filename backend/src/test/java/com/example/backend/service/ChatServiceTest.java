package com.example.backend.service;

import com.example.backend.dto.MessagePageResponse;
import com.example.backend.dto.MessageResponse;
import com.example.backend.dto.SendMessageRequest;
import com.example.backend.model.*;
import com.example.backend.repository.ConversationRepository;
import com.example.backend.repository.ExchangeOfferRepository;
import com.example.backend.repository.MessageRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock private ConversationRepository conversationRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private ExchangeOfferRepository exchangeOfferRepository;
    @Mock private UserRepository userRepository;

    private ChatService chatService;
    private User alice;
    private User bob;
    private ExchangeOffer offer;
    private Conversation conversation;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(conversationRepository, messageRepository, exchangeOfferRepository, userRepository);
        alice = new User(1L, "Alice", "alice@example.com", "hash", "Alice bio");
        bob = new User(2L, "Bob", "bob@example.com", "hash", "Bob bio");
        offer = new ExchangeOffer();
        offer.setId(9L);
        offer.setSender(alice);
        offer.setReceiver(bob);
        offer.setOfferedSkill(new Skill(1L, "Java", "Code"));
        offer.setRequestedSkill(new Skill(2L, "Design", "Art"));
        offer.setStatus(OfferStatus.ACCEPTED);
        conversation = new Conversation(offer, alice, bob);
        conversation.setId(20L);
    }

    @Test
    void acceptedOfferCreatesOneConversationAndRepeatedAccessReusesIt() {
        when(exchangeOfferRepository.findByIdForUpdate(9L)).thenReturn(Optional.of(offer));
        when(conversationRepository.findByExchangeOfferId(9L)).thenReturn(Optional.empty(), Optional.of(conversation));
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);

        Conversation created = chatService.ensureConversation(9L);
        Conversation repeated = chatService.ensureConversation(9L);

        assertEquals(20L, created.getId());
        assertSame(conversation, repeated);
        verify(conversationRepository, times(1)).save(any(Conversation.class));
    }

    @Test
    void pendingOrRejectedOfferCannotCreateConversation() {
        when(exchangeOfferRepository.findByIdForUpdate(9L)).thenReturn(Optional.of(offer));
        offer.setStatus(OfferStatus.REJECTED);
        assertThrows(ChatAccessDeniedException.class, () -> chatService.ensureConversation(9L));
        verify(conversationRepository, never()).save(any());
    }

    @Test
    void eitherParticipantCanListConversationButOtherUserCannot() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));
        when(exchangeOfferRepository.findAcceptedByParticipant(anyLong())).thenReturn(List.of());
        when(conversationRepository.findForParticipant(anyLong())).thenReturn(List.of(conversation));
        when(messageRepository.findLatestForConversations(List.of(20L))).thenReturn(List.of());

        assertEquals("Bob", chatService.getConversations(1L).get(0).otherParticipantName());
        assertEquals("Alice", chatService.getConversations(2L).get(0).otherParticipantName());
        when(userRepository.findById(99L)).thenReturn(Optional.of(new User(99L, "Mallory", "m@example.com", "hash", null)));
        when(exchangeOfferRepository.findAcceptedByParticipant(99L)).thenReturn(List.of());
        when(conversationRepository.findForParticipant(99L)).thenReturn(List.of());
        assertTrue(chatService.getConversations(99L).isEmpty());
    }

    @Test
    void validMessageIsSavedAndActivityUpdated() {
        when(conversationRepository.findById(20L)).thenReturn(Optional.of(conversation));
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            message.setId(31L);
            message.setCreatedAt(LocalDateTime.now());
            return message;
        });

        MessageResponse response = chatService.sendMessage(20L, new SendMessageRequest(1L, "  Hello Bob  "));

        assertEquals("Hello Bob", response.content());
        assertEquals("Alice", response.senderName());
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(captor.capture());
        assertEquals("Hello Bob", captor.getValue().getContent());
        verify(conversationRepository).save(conversation);
        assertNotNull(conversation.getUpdatedAt());
    }

    @Test
    void messageHistoryUsesChronologicalPagination() {
        when(conversationRepository.findById(20L)).thenReturn(Optional.of(conversation));
        Message first = new Message(conversation, alice, "first");
        first.setId(1L); first.setCreatedAt(LocalDateTime.parse("2025-01-01T10:00:00"));
        Message second = new Message(conversation, bob, "second");
        second.setId(2L); second.setCreatedAt(LocalDateTime.parse("2025-01-01T10:01:00"));
        when(messageRepository.findByConversationId(eq(20L), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(second, first), PageRequest.of(0, 2), 2));

        MessagePageResponse page = chatService.getMessages(20L, 2L, 0, 2);
        assertEquals(List.of("first", "second"), page.messages().stream().map(MessageResponse::content).toList());
        assertFalse(page.hasNext());
    }

    @Test
    void blankLongAndNonParticipantMessagesAreRejected() {
        when(conversationRepository.findById(20L)).thenReturn(Optional.of(conversation));
        assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(20L, new SendMessageRequest(1L, "  ")));
        assertThrows(IllegalArgumentException.class, () -> chatService.sendMessage(20L, new SendMessageRequest(1L, "x".repeat(2001))));
        assertThrows(ChatAccessDeniedException.class, () -> chatService.sendMessage(20L, new SendMessageRequest(99L, "hello")));
        assertThrows(ChatAccessDeniedException.class, () -> chatService.getMessages(20L, 99L, 0, 50));
        verify(messageRepository, never()).save(any());
    }

    @Test
    void missingConversationIsNotFound() {
        when(conversationRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(ChatNotFoundException.class, () -> chatService.getMessages(404L, 1L, 0, 50));
    }
}
