package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;

public class CreateOfferRequest {

    @NotNull(message = "Sender ID is required")
    private Long senderId;

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    @NotNull(message = "Offered skill ID is required")
    private Long offeredSkillId;

    @NotNull(message = "Requested skill ID is required")
    private Long requestedSkillId;

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getOfferedSkillId() {
        return offeredSkillId;
    }

    public void setOfferedSkillId(Long offeredSkillId) {
        this.offeredSkillId = offeredSkillId;
    }

    public Long getRequestedSkillId() {
        return requestedSkillId;
    }

    public void setRequestedSkillId(Long requestedSkillId) {
        this.requestedSkillId = requestedSkillId;
    }
}
