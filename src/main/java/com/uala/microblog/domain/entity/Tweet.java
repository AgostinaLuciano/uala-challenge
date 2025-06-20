package com.uala.microblog.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Tweet {
    private String id; 
    private String content;
    private String userId; 
    private LocalDateTime createdAt;

    public static final int MAX_CONTENT_LENGTH = 280;

    public Tweet() {
    }

        public Tweet(String content, String userId) {
        this.content = content;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }
    
    public Tweet(String id, String content, String userId, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    
    public void validateContent() {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Tweet content cannot be null or empty");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("Tweet content cannot exceed " + MAX_CONTENT_LENGTH + " characters");
        }
    }

    
    public void validateUserId() {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
    }

    public void validate() {
        validateContent();
        validateUserId();
    }

    
    public long getAgeInMinutes() {
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toMinutes();
    }

    
    public boolean isRecent() {
        return getAgeInMinutes() < 60;
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tweet tweet = (Tweet) o;
        return Objects.equals(id, tweet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                '}';
    }
} 