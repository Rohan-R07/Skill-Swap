package com.example.backend.service;

import com.example.backend.dto.CreateOfferRequest;
import com.example.backend.dto.ExchangeOfferResponse;
import com.example.backend.model.ExchangeOffer;
import com.example.backend.model.OfferStatus;
import com.example.backend.model.Skill;
import com.example.backend.model.SkillType;
import com.example.backend.model.User;
import com.example.backend.model.UserSkill;
import com.example.backend.repository.ExchangeOfferRepository;
import com.example.backend.repository.SkillRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.UserSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeOfferServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserSkillRepository userSkillRepository;

    @Mock
    private ExchangeOfferRepository exchangeOfferRepository;

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ExchangeOfferService exchangeOfferService;

    private User sender;
    private User receiver;
    private Skill offeredSkill;
    private Skill requestedSkill;

    @BeforeEach
    void setUp() {
        sender = new User(1L, "Alice", "alice@example.com", "hash", "Bio");
        receiver = new User(2L, "Bob", "bob@example.com", "hash", "Bio");

        offeredSkill = new Skill(1L, "Java", "Programming");
        requestedSkill = new Skill(2L, "React", "Frontend");
    }

    @Test
    void createOffer_success() {
        CreateOfferRequest request = new CreateOfferRequest();
        request.setSenderId(1L);
        request.setReceiverId(2L);
        request.setOfferedSkillId(1L);
        request.setRequestedSkillId(2L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(true);
        when(userSkillRepository.existsByUser_IdAndSkill_IdAndType(1L, 1L, SkillType.TEACH)).thenReturn(true);
        when(userSkillRepository.existsByUser_IdAndSkill_IdAndType(1L, 2L, SkillType.LEARN)).thenReturn(true);
        when(userSkillRepository.existsByUser_IdAndSkill_IdAndType(2L, 2L, SkillType.TEACH)).thenReturn(true);
        when(userSkillRepository.existsByUser_IdAndSkill_IdAndType(2L, 1L, SkillType.LEARN)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(offeredSkill));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(requestedSkill));

        ExchangeOffer savedOffer = new ExchangeOffer();
        savedOffer.setId(10L);
        savedOffer.setSender(sender);
        savedOffer.setReceiver(receiver);
        savedOffer.setOfferedSkill(offeredSkill);
        savedOffer.setRequestedSkill(requestedSkill);
        savedOffer.setStatus(OfferStatus.PENDING);
        savedOffer.setCreatedAt(LocalDateTime.now());
        when(exchangeOfferRepository.save(any(ExchangeOffer.class))).thenReturn(savedOffer);

        ExchangeOfferResponse response = exchangeOfferService.createOffer(request);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(10L, response.getId());
        assertEquals("Alice", response.getSenderName());
        assertEquals("Bob", response.getReceiverName());
        assertEquals("Java", response.getOfferedSkillName());
        assertEquals("React", response.getRequestedSkillName());
        assertEquals("PENDING", response.getStatus());
        verify(exchangeOfferRepository, times(1)).save(any(ExchangeOffer.class));
    }

    @Test
    void createOffer_selfOffer_rejected() {
        CreateOfferRequest request = new CreateOfferRequest();
        request.setSenderId(1L);
        request.setReceiverId(1L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> exchangeOfferService.createOffer(request));
        assertEquals("Users cannot send offers to themselves", ex.getMessage());
    }

    @Test
    void createOffer_senderNotFound_rejected() {
        CreateOfferRequest request = new CreateOfferRequest();
        request.setSenderId(99L);
        request.setReceiverId(2L);
        request.setOfferedSkillId(1L);
        request.setRequestedSkillId(2L);

        when(userRepository.existsById(99L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> exchangeOfferService.createOffer(request));
        assertEquals("Sender user not found", ex.getMessage());
    }

    @Test
    void createOffer_receiverNotFound_rejected() {
        CreateOfferRequest request = new CreateOfferRequest();
        request.setSenderId(1L);
        request.setReceiverId(99L);
        request.setOfferedSkillId(1L);
        request.setRequestedSkillId(2L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(99L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> exchangeOfferService.createOffer(request));
        assertEquals("Receiver user not found", ex.getMessage());
    }

    @Test
    void createOffer_offeredSkillNotFound_rejected() {
        CreateOfferRequest request = new CreateOfferRequest();
        request.setSenderId(1L);
        request.setReceiverId(2L);
        request.setOfferedSkillId(99L);
        request.setRequestedSkillId(2L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(skillRepository.existsById(99L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> exchangeOfferService.createOffer(request));
        assertEquals("Offered skill not found", ex.getMessage());
    }

    @Test
    void createOffer_requestedSkillNotFound_rejected() {
        CreateOfferRequest request = new CreateOfferRequest();
        request.setSenderId(1L);
        request.setReceiverId(2L);
        request.setOfferedSkillId(1L);
        request.setRequestedSkillId(99L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(99L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> exchangeOfferService.createOffer(request));
        assertEquals("Requested skill not found", ex.getMessage());
    }

    @Test
    void createOffer_senderDoesNotTeachOfferedSkill_rejected() {
        CreateOfferRequest request = new CreateOfferRequest();
        request.setSenderId(1L);
        request.setReceiverId(2L);
        request.setOfferedSkillId(1L);
        request.setRequestedSkillId(2L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(true);
        when(userSkillRepository.existsByUser_IdAndSkill_IdAndType(1L, 1L, SkillType.TEACH)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> exchangeOfferService.createOffer(request));
        assertEquals("Sender must teach the offered skill", ex.getMessage());
    }

    @Test
    void createOffer_senderDoesNotLearnRequestedSkill_rejected() {
        CreateOfferRequest request = new CreateOfferRequest();
        request.setSenderId(1L);
        request.setReceiverId(2L);
        request.setOfferedSkillId(1L);
        request.setRequestedSkillId(2L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(true);
        when(userSkillRepository.existsByUser_IdAndSkill_IdAndType(1L, 1L, SkillType.TEACH)).thenReturn(true);
        when(userSkillRepository.existsByUser_IdAndSkill_IdAndType(1L, 2L, SkillType.LEARN)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> exchangeOfferService.createOffer(request));
        assertEquals("Sender must have the requested skill listed as LEARN", ex.getMessage());
    }

    @Test
    void createOffer_receiverDoesNotTeachRequestedSkill_rejected() {
        CreateOfferRequest request = new CreateOfferRequest();
        request.setSenderId(1L);
        request.setReceiverId(2L);
        request.setOfferedSkillId(1L);
        request.setRequestedSkillId(2L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(true);
        when(userSkillRepository.existsByUser_IdAndSkill_IdAndType(1L, 1L, SkillType.TEACH)).thenReturn(true);
        when(userSkillRepository.existsByUser_IdAndSkill_IdAndType(1L, 2L, SkillType.LEARN)).thenReturn(true);
        when(userSkillRepository.existsByUser_IdAndSkill_IdAndType(2L, 2L, SkillType.TEACH)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> exchangeOfferService.createOffer(request));
        assertEquals("Receiver must teach the requested skill", ex.getMessage());
    }

    @Test
    void createOffer_receiverDoesNotLearnOfferedSkill_rejected() {
        CreateOfferRequest request = new CreateOfferRequest();
        request.setSenderId(1L);
        request.setReceiverId(2L);
        request.setOfferedSkillId(1L);
        request.setRequestedSkillId(2L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(true);
        when(userSkillRepository.existsByUser_IdAndSkill_IdAndType(1L, 1L, SkillType.TEACH)).thenReturn(true);
        when(userSkillRepository.existsByUser_IdAndSkill_IdAndType(1L, 2L, SkillType.LEARN)).thenReturn(true);
        when(userSkillRepository.existsByUser_IdAndSkill_IdAndType(2L, 2L, SkillType.TEACH)).thenReturn(true);
        when(userSkillRepository.existsByUser_IdAndSkill_IdAndType(2L, 1L, SkillType.LEARN)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> exchangeOfferService.createOffer(request));
        assertEquals("Receiver must want to learn the offered skill", ex.getMessage());
    }

    @Test
    void getIncomingOffers_returnsList() {
        ExchangeOffer offer = new ExchangeOffer();
        offer.setId(1L);
        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setOfferedSkill(offeredSkill);
        offer.setRequestedSkill(requestedSkill);
        offer.setStatus(OfferStatus.PENDING);
        offer.setCreatedAt(LocalDateTime.now());

        when(exchangeOfferRepository.findByReceiverId(2L)).thenReturn(List.of(offer));

        List<ExchangeOfferResponse> responses = exchangeOfferService.getIncomingOffers(2L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Alice", responses.get(0).getSenderName());
        assertEquals("Bob", responses.get(0).getReceiverName());
        assertEquals("PENDING", responses.get(0).getStatus());
    }

    @Test
    void getIncomingOffers_emptyList() {
        when(exchangeOfferRepository.findByReceiverId(2L)).thenReturn(List.of());

        List<ExchangeOfferResponse> responses = exchangeOfferService.getIncomingOffers(2L);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void getOutgoingOffers_returnsList() {
        ExchangeOffer offer = new ExchangeOffer();
        offer.setId(1L);
        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setOfferedSkill(offeredSkill);
        offer.setRequestedSkill(requestedSkill);
        offer.setStatus(OfferStatus.PENDING);
        offer.setCreatedAt(LocalDateTime.now());

        when(exchangeOfferRepository.findBySenderId(1L)).thenReturn(List.of(offer));

        List<ExchangeOfferResponse> responses = exchangeOfferService.getOutgoingOffers(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Alice", responses.get(0).getSenderName());
        assertEquals("Bob", responses.get(0).getReceiverName());
    }

    @Test
    void getOutgoingOffers_emptyList() {
        when(exchangeOfferRepository.findBySenderId(1L)).thenReturn(List.of());

        List<ExchangeOfferResponse> responses = exchangeOfferService.getOutgoingOffers(1L);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    void acceptOffer_success() {
        ExchangeOffer offer = new ExchangeOffer();
        offer.setId(1L);
        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setOfferedSkill(offeredSkill);
        offer.setRequestedSkill(requestedSkill);
        offer.setStatus(OfferStatus.PENDING);
        offer.setCreatedAt(LocalDateTime.now());

        when(exchangeOfferRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(exchangeOfferRepository.save(any(ExchangeOffer.class))).thenReturn(offer);

        ExchangeOfferResponse response = exchangeOfferService.acceptOffer(1L, 2L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("ACCEPTED", response.getStatus());
        verify(exchangeOfferRepository, times(1)).save(offer);
        verify(chatService, times(1)).ensureConversation(1L);
    }

    @Test
    void acceptOffer_notReceiver_rejected() {
        ExchangeOffer offer = new ExchangeOffer();
        offer.setId(1L);
        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setStatus(OfferStatus.PENDING);

        when(exchangeOfferRepository.findById(1L)).thenReturn(Optional.of(offer));

        OfferAccessDeniedException ex = assertThrows(OfferAccessDeniedException.class,
                () -> exchangeOfferService.acceptOffer(1L, 99L));
        assertEquals("Only the receiver can accept this offer", ex.getMessage());
    }

    @Test
    void acceptOffer_alreadyAccepted_reusesConversation() {
        ExchangeOffer offer = new ExchangeOffer();
        offer.setId(1L);
        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setOfferedSkill(offeredSkill);
        offer.setRequestedSkill(requestedSkill);
        offer.setStatus(OfferStatus.ACCEPTED);

        when(exchangeOfferRepository.findById(1L)).thenReturn(Optional.of(offer));

        ExchangeOfferResponse response = exchangeOfferService.acceptOffer(1L, 2L);
        assertEquals("ACCEPTED", response.getStatus());
        verify(exchangeOfferRepository, never()).save(any(ExchangeOffer.class));
        verify(chatService).ensureConversation(1L);
    }

    @Test
    void acceptOffer_notFound_rejected() {
        when(exchangeOfferRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> exchangeOfferService.acceptOffer(99L, 2L));
        assertEquals("Offer not found", ex.getMessage());
    }

    @Test
    void rejectOffer_success() {
        ExchangeOffer offer = new ExchangeOffer();
        offer.setId(1L);
        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setOfferedSkill(offeredSkill);
        offer.setRequestedSkill(requestedSkill);
        offer.setStatus(OfferStatus.PENDING);
        offer.setCreatedAt(LocalDateTime.now());

        when(exchangeOfferRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(exchangeOfferRepository.save(any(ExchangeOffer.class))).thenReturn(offer);

        ExchangeOfferResponse response = exchangeOfferService.rejectOffer(1L, 2L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("REJECTED", response.getStatus());
        verify(exchangeOfferRepository, times(1)).save(offer);
    }

    @Test
    void rejectOffer_notReceiver_rejected() {
        ExchangeOffer offer = new ExchangeOffer();
        offer.setId(1L);
        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setStatus(OfferStatus.PENDING);

        when(exchangeOfferRepository.findById(1L)).thenReturn(Optional.of(offer));

        OfferAccessDeniedException ex = assertThrows(OfferAccessDeniedException.class,
                () -> exchangeOfferService.rejectOffer(1L, 99L));
        assertEquals("Only the receiver can reject this offer", ex.getMessage());
    }

    @Test
    void rejectOffer_alreadyProcessed_rejected() {
        ExchangeOffer offer = new ExchangeOffer();
        offer.setId(1L);
        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setStatus(OfferStatus.REJECTED);

        when(exchangeOfferRepository.findById(1L)).thenReturn(Optional.of(offer));

        OfferAlreadyProcessedException ex = assertThrows(OfferAlreadyProcessedException.class,
                () -> exchangeOfferService.rejectOffer(1L, 2L));
        assertTrue(ex.getMessage().contains("REJECTED"));
    }

    @Test
    void rejectOffer_notFound_rejected() {
        when(exchangeOfferRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> exchangeOfferService.rejectOffer(99L, 2L));
        assertEquals("Offer not found", ex.getMessage());
    }
}
