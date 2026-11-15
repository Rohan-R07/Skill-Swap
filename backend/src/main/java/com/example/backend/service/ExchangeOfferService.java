package com.example.backend.service;

import com.example.backend.dto.CreateOfferRequest;
import com.example.backend.dto.ExchangeOfferResponse;
import com.example.backend.model.ExchangeOffer;
import com.example.backend.model.OfferStatus;
import com.example.backend.model.SkillType;
import com.example.backend.model.User;
import com.example.backend.model.UserSkill;
import com.example.backend.repository.ExchangeOfferRepository;
import com.example.backend.repository.SkillRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.UserSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangeOfferService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final ExchangeOfferRepository exchangeOfferRepository;

    public ExchangeOfferService(UserRepository userRepository,
                                SkillRepository skillRepository,
                                UserSkillRepository userSkillRepository,
                                ExchangeOfferRepository exchangeOfferRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
        this.exchangeOfferRepository = exchangeOfferRepository;
    }

    @Transactional
    public ExchangeOfferResponse createOffer(CreateOfferRequest request) {
        validateSenderAndReceiver(request.getSenderId(), request.getReceiverId());
        validateSkills(request.getOfferedSkillId(), request.getRequestedSkillId());
        validateSkillOwnership(request.getSenderId(), request.getReceiverId(),
                request.getOfferedSkillId(), request.getRequestedSkillId());

        User sender = userRepository.findById(request.getSenderId()).orElseThrow();
        User receiver = userRepository.findById(request.getReceiverId()).orElseThrow();

        ExchangeOffer offer = new ExchangeOffer();
        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setOfferedSkill(skillRepository.findById(request.getOfferedSkillId()).orElseThrow());
        offer.setRequestedSkill(skillRepository.findById(request.getRequestedSkillId()).orElseThrow());
        offer.setStatus(OfferStatus.PENDING);

        ExchangeOffer saved = exchangeOfferRepository.save(offer);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ExchangeOfferResponse> getIncomingOffers(Long userId) {
        List<ExchangeOffer> offers = exchangeOfferRepository.findByReceiverId(userId);
        return offers.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExchangeOfferResponse> getOutgoingOffers(Long userId) {
        List<ExchangeOffer> offers = exchangeOfferRepository.findBySenderId(userId);
        return offers.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ExchangeOfferResponse acceptOffer(Long offerId, Long userId) {
        ExchangeOffer offer = exchangeOfferRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        if (!offer.getReceiver().getId().equals(userId)) {
            throw new OfferAccessDeniedException("Only the receiver can accept this offer");
        }

        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new OfferAlreadyProcessedException("Offer is already " + offer.getStatus() + " and cannot be accepted");
        }

        offer.setStatus(OfferStatus.ACCEPTED);
        ExchangeOffer saved = exchangeOfferRepository.save(offer);
        return toResponse(saved);
    }

    @Transactional
    public ExchangeOfferResponse rejectOffer(Long offerId, Long userId) {
        ExchangeOffer offer = exchangeOfferRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        if (!offer.getReceiver().getId().equals(userId)) {
            throw new OfferAccessDeniedException("Only the receiver can reject this offer");
        }

        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new OfferAlreadyProcessedException("Offer is already " + offer.getStatus() + " and cannot be rejected");
        }

        offer.setStatus(OfferStatus.REJECTED);
        ExchangeOffer saved = exchangeOfferRepository.save(offer);
        return toResponse(saved);
    }

    private void validateSenderAndReceiver(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Users cannot send offers to themselves");
        }

        if (!userRepository.existsById(senderId)) {
            throw new IllegalArgumentException("Sender user not found");
        }

        if (!userRepository.existsById(receiverId)) {
            throw new IllegalArgumentException("Receiver user not found");
        }
    }

    private void validateSkills(Long offeredSkillId, Long requestedSkillId) {
        if (!skillRepository.existsById(offeredSkillId)) {
            throw new IllegalArgumentException("Offered skill not found");
        }

        if (!skillRepository.existsById(requestedSkillId)) {
            throw new IllegalArgumentException("Requested skill not found");
        }
    }

    private void validateSkillOwnership(Long senderId, Long receiverId,
                                        Long offeredSkillId, Long requestedSkillId) {
        if (!userSkillRepository.existsByUser_IdAndSkill_IdAndType(senderId, offeredSkillId, SkillType.TEACH)) {
            throw new IllegalArgumentException("Sender must teach the offered skill");
        }

        if (!userSkillRepository.existsByUser_IdAndSkill_IdAndType(senderId, requestedSkillId, SkillType.LEARN)) {
            throw new IllegalArgumentException("Sender must have the requested skill listed as LEARN");
        }

        if (!userSkillRepository.existsByUser_IdAndSkill_IdAndType(receiverId, requestedSkillId, SkillType.TEACH)) {
            throw new IllegalArgumentException("Receiver must teach the requested skill");
        }

        if (!userSkillRepository.existsByUser_IdAndSkill_IdAndType(receiverId, offeredSkillId, SkillType.LEARN)) {
            throw new IllegalArgumentException("Receiver must want to learn the offered skill");
        }
    }

    private ExchangeOfferResponse toResponse(ExchangeOffer offer) {
        return new ExchangeOfferResponse(
                offer.getId(),
                offer.getSender().getId(),
                offer.getSender().getName(),
                offer.getReceiver().getId(),
                offer.getReceiver().getName(),
                offer.getOfferedSkill().getName(),
                offer.getRequestedSkill().getName(),
                offer.getStatus().name(),
                offer.getCreatedAt()
        );
    }
}
