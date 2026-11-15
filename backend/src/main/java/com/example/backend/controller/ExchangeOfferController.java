package com.example.backend.controller;

import com.example.backend.dto.CreateOfferRequest;
import com.example.backend.dto.ExchangeOfferResponse;
import com.example.backend.service.ExchangeOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class ExchangeOfferController {

    private final ExchangeOfferService exchangeOfferService;

    public ExchangeOfferController(ExchangeOfferService exchangeOfferService) {
        this.exchangeOfferService = exchangeOfferService;
    }

    @Operation(summary = "Create a new exchange offer",
            description = "Creates a PENDING exchange offer from sender to receiver. Validates that both users exist, " +
                    "skills exist, and skill ownership/type rules are satisfied.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Offer created successfully",
                    content = @Content(schema = @Schema(implementation = ExchangeOfferResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error or business rule violation"),
            @ApiResponse(responseCode = "404", description = "User or skill not found")
    })
    @PostMapping
    public ResponseEntity<ExchangeOfferResponse> createOffer(
            @Parameter(description = "Offer creation details", required = true)
            @Valid @RequestBody CreateOfferRequest request) {
        ExchangeOfferResponse response = exchangeOfferService.createOffer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get incoming offers",
            description = "Retrieves all exchange offers received by the specified user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of incoming offers",
                    content = @Content(schema = @Schema(implementation = ExchangeOfferResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/incoming/{userId}")
    public ResponseEntity<List<ExchangeOfferResponse>> getIncomingOffers(
            @Parameter(description = "ID of the user receiving offers", required = true)
            @PathVariable Long userId) {
        List<ExchangeOfferResponse> offers = exchangeOfferService.getIncomingOffers(userId);
        return ResponseEntity.ok(offers);
    }

    @Operation(summary = "Get outgoing offers",
            description = "Retrieves all exchange offers sent by the specified user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of outgoing offers",
                    content = @Content(schema = @Schema(implementation = ExchangeOfferResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/outgoing/{userId}")
    public ResponseEntity<List<ExchangeOfferResponse>> getOutgoingOffers(
            @Parameter(description = "ID of the user sending offers", required = true)
            @PathVariable Long userId) {
        List<ExchangeOfferResponse> offers = exchangeOfferService.getOutgoingOffers(userId);
        return ResponseEntity.ok(offers);
    }

    @Operation(summary = "Accept an exchange offer",
            description = "Accepts a PENDING exchange offer. Only the receiver can accept. Changes status to ACCEPTED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Offer accepted successfully",
                    content = @Content(schema = @Schema(implementation = ExchangeOfferResponse.class))),
            @ApiResponse(responseCode = "403", description = "Only the receiver can accept this offer"),
            @ApiResponse(responseCode = "404", description = "Offer not found"),
            @ApiResponse(responseCode = "409", description = "Offer is already processed")
    })
    @PutMapping("/{offerId}/accept/{userId}")
    public ResponseEntity<ExchangeOfferResponse> acceptOffer(
            @Parameter(description = "ID of the offer to accept", required = true)
            @PathVariable Long offerId,
            @Parameter(description = "ID of the user accepting the offer (must be receiver)", required = true)
            @PathVariable Long userId) {
        ExchangeOfferResponse response = exchangeOfferService.acceptOffer(offerId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reject an exchange offer",
            description = "Rejects a PENDING exchange offer. Only the receiver can reject. Changes status to REJECTED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Offer rejected successfully",
                    content = @Content(schema = @Schema(implementation = ExchangeOfferResponse.class))),
            @ApiResponse(responseCode = "403", description = "Only the receiver can reject this offer"),
            @ApiResponse(responseCode = "404", description = "Offer not found"),
            @ApiResponse(responseCode = "409", description = "Offer is already processed")
    })
    @PutMapping("/{offerId}/reject/{userId}")
    public ResponseEntity<ExchangeOfferResponse> rejectOffer(
            @Parameter(description = "ID of the offer to reject", required = true)
            @PathVariable Long offerId,
            @Parameter(description = "ID of the user rejecting the offer (must be receiver)", required = true)
            @PathVariable Long userId) {
        ExchangeOfferResponse response = exchangeOfferService.rejectOffer(offerId, userId);
        return ResponseEntity.ok(response);
    }
}
