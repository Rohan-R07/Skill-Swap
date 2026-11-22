package com.example.backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversations", uniqueConstraints = {
        @UniqueConstraint(name = "uk_conversation_exchange_offer", columnNames = "exchange_offer_id")
}, indexes = {
        @Index(name = "idx_conversation_participant_one", columnList = "participant_one_id"),
        @Index(name = "idx_conversation_participant_two", columnList = "participant_two_id")
})
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exchange_offer_id", nullable = false, unique = true)
    private ExchangeOffer exchangeOffer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_one_id", nullable = false)
    private User participantOne;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_two_id", nullable = false)
    private User participantTwo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Conversation() {}

    public Conversation(ExchangeOffer exchangeOffer, User participantOne, User participantTwo) {
        this.exchangeOffer = exchangeOffer;
        this.participantOne = participantOne;
        this.participantTwo = participantTwo;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ExchangeOffer getExchangeOffer() { return exchangeOffer; }
    public User getParticipantOne() { return participantOne; }
    public User getParticipantTwo() { return participantTwo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
