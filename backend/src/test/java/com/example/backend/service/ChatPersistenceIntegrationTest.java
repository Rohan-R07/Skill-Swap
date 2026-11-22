package com.example.backend.service;

import com.example.backend.dto.MessagePageResponse;
import com.example.backend.dto.SendMessageRequest;
import com.example.backend.model.*;
import com.example.backend.repository.ConversationRepository;
import com.example.backend.repository.ExchangeOfferRepository;
import com.example.backend.repository.SkillRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ChatPersistenceIntegrationTest {
    @Autowired private UserRepository userRepository;
    @Autowired private SkillRepository skillRepository;
    @Autowired private ExchangeOfferRepository exchangeOfferRepository;
    @Autowired private ConversationRepository conversationRepository;
    @Autowired private ChatService chatService;
    @Autowired private ExchangeOfferService exchangeOfferService;

    @Test
    void acceptedOfferPersistsOneConversationAndMessagesForBothParticipants() {
        String suffix = UUID.randomUUID().toString();
        User alice = userRepository.save(new User(null, "Chat Alice", "chat-alice-" + suffix + "@example.test", "test-hash", null));
        User bob = userRepository.save(new User(null, "Chat Bob", "chat-bob-" + suffix + "@example.test", "test-hash", null));
        User unrelated = userRepository.save(new User(null, "Chat Other", "chat-other-" + suffix + "@example.test", "test-hash", null));
        Skill offered = skillRepository.save(new Skill(null, "Offered " + suffix, "Test"));
        Skill requested = skillRepository.save(new Skill(null, "Requested " + suffix, "Test"));

        ExchangeOffer offer = new ExchangeOffer();
        offer.setSender(alice);
        offer.setReceiver(bob);
        offer.setOfferedSkill(offered);
        offer.setRequestedSkill(requested);
        offer.setStatus(OfferStatus.PENDING);
        offer = exchangeOfferRepository.save(offer);

        exchangeOfferService.acceptOffer(offer.getId(), bob.getId());
        Long conversationId = conversationRepository.findByExchangeOfferId(offer.getId()).orElseThrow().getId();
        exchangeOfferService.acceptOffer(offer.getId(), bob.getId());
        assertEquals(conversationId, conversationRepository.findByExchangeOfferId(offer.getId()).orElseThrow().getId());

        var aliceConversations = chatService.getConversations(alice.getId());
        var bobConversations = chatService.getConversations(bob.getId());
        assertEquals("Chat Bob", aliceConversations.get(0).otherParticipantName());
        assertEquals("Chat Alice", bobConversations.get(0).otherParticipantName());
        assertEquals(conversationId, aliceConversations.get(0).id());

        chatService.sendMessage(conversationId, new SendMessageRequest(alice.getId(), " first "));
        chatService.sendMessage(conversationId, new SendMessageRequest(bob.getId(), "second"));
        MessagePageResponse firstPage = chatService.getMessages(conversationId, alice.getId(), 0, 1);
        MessagePageResponse secondPage = chatService.getMessages(conversationId, bob.getId(), 1, 1);
        assertEquals("second", firstPage.messages().get(0).content());
        assertTrue(firstPage.hasNext());
        assertEquals("first", secondPage.messages().get(0).content());
        assertFalse(secondPage.hasNext());
        assertThrows(ChatAccessDeniedException.class,
                () -> chatService.getMessages(conversationId, unrelated.getId(), 0, 50));
        assertThrows(ChatAccessDeniedException.class,
                () -> chatService.sendMessage(conversationId, new SendMessageRequest(unrelated.getId(), "intrusion")));
    }

    @Test
    void rejectedOfferCannotHaveAConversation() {
        String suffix = UUID.randomUUID().toString();
        User alice = userRepository.save(new User(null, "Reject Alice", "reject-alice-" + suffix + "@example.test", "test-hash", null));
        User bob = userRepository.save(new User(null, "Reject Bob", "reject-bob-" + suffix + "@example.test", "test-hash", null));
        ExchangeOffer offer = new ExchangeOffer();
        offer.setSender(alice);
        offer.setReceiver(bob);
        offer.setOfferedSkill(skillRepository.save(new Skill(null, "No Chat " + suffix, "Test")));
        offer.setRequestedSkill(skillRepository.save(new Skill(null, "Rejected " + suffix, "Test")));
        offer.setStatus(OfferStatus.REJECTED);
        offer = exchangeOfferRepository.save(offer);

        Long offerId = offer.getId();
        assertThrows(ChatAccessDeniedException.class, () -> chatService.ensureConversation(offerId));
        assertTrue(conversationRepository.findByExchangeOfferId(offerId).isEmpty());
    }
}
