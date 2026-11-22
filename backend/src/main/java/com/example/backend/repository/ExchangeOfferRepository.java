package com.example.backend.repository;

import com.example.backend.model.ExchangeOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.util.List;

@Repository
public interface ExchangeOfferRepository extends JpaRepository<ExchangeOffer, Long> {

    List<ExchangeOffer> findByReceiverId(Long receiverId);

    List<ExchangeOffer> findBySenderId(Long senderId);

    @EntityGraph(attributePaths = {"sender", "receiver"})
    @Query("select o from ExchangeOffer o where o.status = com.example.backend.model.OfferStatus.ACCEPTED and (o.sender.id = :userId or o.receiver.id = :userId)")
    List<ExchangeOffer> findAcceptedByParticipant(@Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from ExchangeOffer o where o.id = :offerId")
    java.util.Optional<ExchangeOffer> findByIdForUpdate(@Param("offerId") Long offerId);
}
