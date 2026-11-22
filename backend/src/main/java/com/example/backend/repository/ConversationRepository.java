package com.example.backend.repository;

import com.example.backend.model.Conversation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Collection;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByExchangeOfferId(Long offerId);

    @Query("select c.exchangeOffer.id from Conversation c where c.exchangeOffer.id in :offerIds")
    List<Long> findExistingOfferIds(@Param("offerIds") Collection<Long> offerIds);

    @EntityGraph(attributePaths = {"participantOne", "participantTwo", "exchangeOffer"})
    @Query("select c from Conversation c where c.participantOne.id = :userId or c.participantTwo.id = :userId order by c.updatedAt desc")
    List<Conversation> findForParticipant(@Param("userId") Long userId);

}
