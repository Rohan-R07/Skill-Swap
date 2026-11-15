package com.example.backend.dto;

import java.time.LocalDateTime;

public class ExchangeOfferResponse {

    private boolean success;
    private String message;
    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private String offeredSkillName;
    private String requestedSkillName;
    private String status;
    private LocalDateTime createdAt;

    public ExchangeOfferResponse() {
    }

    public ExchangeOfferResponse(Long id, Long senderId, String senderName, Long receiverId,
                                 String receiverName, String offeredSkillName, String requestedSkillName,
                                 String status, LocalDateTime createdAt) {
        this.success = true;
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.offeredSkillName = offeredSkillName;
        this.requestedSkillName = requestedSkillName;
        this.status = status;
        this.createdAt = createdAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getOfferedSkillName() {
        return offeredSkillName;
    }

    public void setOfferedSkillName(String offeredSkillName) {
        this.offeredSkillName = offeredSkillName;
    }

    public String getRequestedSkillName() {
        return requestedSkillName;
    }

    public void setRequestedSkillName(String requestedSkillName) {
        this.requestedSkillName = requestedSkillName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
