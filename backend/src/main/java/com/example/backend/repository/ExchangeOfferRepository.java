package com.example.backend.repository;

import com.example.backend.model.ExchangeOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeOfferRepository extends JpaRepository<ExchangeOffer, Long> {

    List<ExchangeOffer> findByReceiverId(Long receiverId);

    List<ExchangeOffer> findBySenderId(Long senderId);
}
