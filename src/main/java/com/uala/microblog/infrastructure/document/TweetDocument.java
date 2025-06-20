package com.uala.microblog.infrastructure.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "tweets")
@CompoundIndex(def = "{'user_id': 1, 'created_at': -1}", name = "user_created_idx")
public class TweetDocument {
    
    @Id
    private String id; 
    
    @Field("content")
    private String content;
    
    @Field("user_id")
    @Indexed
    private String userId;
    
    @Field("created_at")
    @Indexed
    private LocalDateTime createdAt;
    
    public TweetDocument() {
    }
    
    public TweetDocument(String content, String userId, LocalDateTime createdAt) {
        this.content = content;
        this.userId = userId;
        this.createdAt = createdAt;
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
        TweetDocument that = (TweetDocument) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "TweetDocument{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                '}';
    }
} 