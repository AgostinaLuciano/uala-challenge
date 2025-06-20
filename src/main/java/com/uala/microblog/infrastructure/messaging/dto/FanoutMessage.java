package com.uala.microblog.infrastructure.messaging.dto;

import java.time.LocalDateTime;

public class FanoutMessage {
    
    private String tweetId;
    private String userId; 
    private String content;
    private LocalDateTime createdAt;
    
    public FanoutMessage() {
    }
    
    public FanoutMessage(String tweetId, String userId, String content, LocalDateTime createdAt) {
        this.tweetId = tweetId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }
    
    
        public String getTweetId() {
        return tweetId;
    }

    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "FanoutMessage{" +
                "tweetId=" + tweetId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
} 