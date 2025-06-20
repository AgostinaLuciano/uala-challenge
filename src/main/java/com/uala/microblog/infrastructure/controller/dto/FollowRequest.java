package com.uala.microblog.infrastructure.controller.dto;

import jakarta.validation.constraints.NotNull;

public class FollowRequest {
    
    @NotNull(message = "Follower user ID is required")
    private String followerId;
    
    @NotNull(message = "Followed user ID is required")
    private String followedId;
    
    public FollowRequest() {
    }
    
    public FollowRequest(String followerId, String followedId) {
        this.followerId = followerId;
        this.followedId = followedId;
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
    
    @Override
    public String toString() {
        return "FollowRequest{" +
                "followerId=" + followerId +
                ", followedId=" + followedId +
                '}';
    }
} 