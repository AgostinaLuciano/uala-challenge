package com.uala.microblog.infrastructure.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateTweetRequest {
    
    @NotNull(message = "User ID is required")
    @NotBlank(message = "User ID cannot be blank")
    private String userId;
    
    @NotBlank(message = "Tweet content is required")
    @Size(max = 280, message = "Tweet content cannot exceed 280 characters")
    private String content;
    
    public CreateTweetRequest() {
    }
    
    public CreateTweetRequest(String userId, String content) {
        this.userId = userId;
        this.content = content;
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
    
    @Override
    public String toString() {
        return "CreateTweetRequest{" +
                "userId=" + userId +
                ", content='" + content + '\'' +
                '}';
    }
} 