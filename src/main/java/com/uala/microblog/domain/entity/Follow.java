package com.uala.microblog.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Follow {
    private String id; 
    private String followerId; 
    private String followedId; 
    private LocalDateTime createdAt;

    public Follow() {
    }

    public Follow(String followerId, String followedId) {
        this.followerId = followerId;
        this.followedId = followedId;
        this.createdAt = LocalDateTime.now();
    }

    public Follow(String id, String followerId, String followedId, LocalDateTime createdAt) {
        this.id = id;
        this.followerId = followerId;
        this.followedId = followedId;
        this.createdAt = createdAt;
    }

    
    public void validateFollowerId() {
        if (followerId == null || followerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Follower ID is required");
        }
    }

    
    public void validateFollowedId() {
        if (followedId == null || followedId.trim().isEmpty()) {
            throw new IllegalArgumentException("Followed ID is required");
        }
    }

    
    public void validateSelfFollow() {
        if (Objects.equals(followerId, followedId)) {
            throw new IllegalArgumentException("User cannot follow themselves");
        }
    }

    public void validate() {
        validateFollowerId();
        validateFollowedId();
        validateSelfFollow();
    }

    
    public boolean isRecentFollow() {
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toHours() < 24;
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getFollowedId() {
        return followedId;
    }

    public void setFollowedId(String followedId) {
        this.followedId = followedId;
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
        Follow follow = (Follow) o;
        return Objects.equals(followerId, follow.followerId) && 
               Objects.equals(followedId, follow.followedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followedId);
    }

    @Override
    public String toString() {
        return "Follow{" +
                "id=" + id +
                ", followerId=" + followerId +
                ", followedId=" + followedId +
                ", createdAt=" + createdAt +
                '}';
    }
} 