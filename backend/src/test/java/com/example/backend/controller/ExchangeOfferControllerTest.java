package com.example.backend.controller;

import com.example.backend.dto.CreateOfferRequest;
import com.example.backend.dto.ExchangeOfferResponse;
import com.example.backend.service.ExchangeOfferService;
import com.example.backend.service.OfferAccessDeniedException;
import com.example.backend.service.OfferAlreadyProcessedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeOfferControllerTest {

    @Mock
    private ExchangeOfferService exchangeOfferService;

    @InjectMocks
    private ExchangeOfferController exchangeOfferController;

    @BeforeEach
    void setUp() {
        exchangeOfferController = new ExchangeOfferController(exchangeOfferService);
    }

    @Test
    void createOffer_success() {
        CreateOfferRequest request = new CreateOfferRequest();
        request.setSenderId(1L);
        request.setReceiverId(2L);
        request.setOfferedSkillId(1L);
        request.setRequestedSkillId(2L);

        ExchangeOfferResponse response = new ExchangeOfferResponse(
                1L, 1L, "Alice", 2L, "Bob", "Java", "React", "PENDING", LocalDateTime.now());
        when(exchangeOfferService.createOffer(any(CreateOfferRequest.class))).thenReturn(response);

        ResponseEntity<ExchangeOfferResponse> result = exchangeOfferController.createOffer(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());
        assertEquals("Alice", result.getBody().getSenderName());
        assertEquals("Bob", result.getBody().getReceiverName());
        assertEquals("Java", result.getBody().getOfferedSkillName());
        assertEquals("React", result.getBody().getRequestedSkillName());
        assertEquals("PENDING", result.getBody().getStatus());
        verify(exchangeOfferService, times(1)).createOffer(any(CreateOfferRequest.class));
    }

    @Test
    void getIncomingOffers_success() {
        ExchangeOfferResponse response = new ExchangeOfferResponse(
                1L, 1L, "Alice", 2L, "Bob", "Java", "React", "PENDING", LocalDateTime.now());
        when(exchangeOfferService.getIncomingOffers(2L)).thenReturn(List.of(response));

        ResponseEntity<List<ExchangeOfferResponse>> result = exchangeOfferController.getIncomingOffers(2L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("Alice", result.getBody().get(0).getSenderName());
        assertEquals("PENDING", result.getBody().get(0).getStatus());
        verify(exchangeOfferService, times(1)).getIncomingOffers(2L);
    }

    @Test
    void getIncomingOffers_empty() {
        when(exchangeOfferService.getIncomingOffers(2L)).thenReturn(List.of());

        ResponseEntity<List<ExchangeOfferResponse>> result = exchangeOfferController.getIncomingOffers(2L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());
    }

    @Test
    void getOutgoingOffers_success() {
        ExchangeOfferResponse response = new ExchangeOfferResponse(
                1L, 1L, "Alice", 2L, "Bob", "Java", "React", "PENDING", LocalDateTime.now());
        when(exchangeOfferService.getOutgoingOffers(1L)).thenReturn(List.of(response));

        ResponseEntity<List<ExchangeOfferResponse>> result = exchangeOfferController.getOutgoingOffers(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals("Alice", result.getBody().get(0).getSenderName());
        verify(exchangeOfferService, times(1)).getOutgoingOffers(1L);
    }

    @Test
    void acceptOffer_success() {
        ExchangeOfferResponse response = new ExchangeOfferResponse(
                1L, 1L, "Alice", 2L, "Bob", "Java", "React", "ACCEPTED", LocalDateTime.now());
        when(exchangeOfferService.acceptOffer(1L, 2L)).thenReturn(response);

        ResponseEntity<ExchangeOfferResponse> result = exchangeOfferController.acceptOffer(1L, 2L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("ACCEPTED", result.getBody().getStatus());
        verify(exchangeOfferService, times(1)).acceptOffer(1L, 2L);
    }

    @Test
    void acceptOffer_accessDenied() {
        when(exchangeOfferService.acceptOffer(1L, 2L))
                .thenThrow(new OfferAccessDeniedException("Only the receiver can accept this offer"));

        assertThrows(OfferAccessDeniedException.class,
                () -> exchangeOfferController.acceptOffer(1L, 2L));
    }

    @Test
    void acceptOffer_alreadyProcessed() {
        when(exchangeOfferService.acceptOffer(1L, 2L))
                .thenThrow(new OfferAlreadyProcessedException("Offer is already ACCEPTED"));

        assertThrows(OfferAlreadyProcessedException.class,
                () -> exchangeOfferController.acceptOffer(1L, 2L));
    }

    @Test
    void rejectOffer_success() {
        ExchangeOfferResponse response = new ExchangeOfferResponse(
                1L, 1L, "Alice", 2L, "Bob", "Java", "React", "REJECTED", LocalDateTime.now());
        when(exchangeOfferService.rejectOffer(1L, 2L)).thenReturn(response);

        ResponseEntity<ExchangeOfferResponse> result = exchangeOfferController.rejectOffer(1L, 2L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("REJECTED", result.getBody().getStatus());
        verify(exchangeOfferService, times(1)).rejectOffer(1L, 2L);
    }

    @Test
    void rejectOffer_accessDenied() {
        when(exchangeOfferService.rejectOffer(1L, 2L))
                .thenThrow(new OfferAccessDeniedException("Only the receiver can reject this offer"));

        assertThrows(OfferAccessDeniedException.class,
                () -> exchangeOfferController.rejectOffer(1L, 2L));
    }

    @Test
    void rejectOffer_alreadyProcessed() {
        when(exchangeOfferService.rejectOffer(1L, 2L))
                .thenThrow(new OfferAlreadyProcessedException("Offer is already REJECTED"));

        assertThrows(OfferAlreadyProcessedException.class,
                () -> exchangeOfferController.rejectOffer(1L, 2L));
    }
}
