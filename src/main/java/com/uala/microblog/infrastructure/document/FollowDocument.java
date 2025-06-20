package com.uala.microblog.infrastructure.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "follows")
@CompoundIndex(def = "{'follower_id': 1, 'followed_id': 1}", name = "follower_followed_idx", unique = true)
public class FollowDocument {
    
    @Id
    private String id; 
    
    @Field("follower_id")
    @Indexed
    private String followerId; 
    
    @Field("followed_id")
    @Indexed
    private String followedId; 
    
    @Field("created_at")
    @Indexed
    private LocalDateTime createdAt;
    
    public FollowDocument() {
    }
    
    public FollowDocument(String followerId, String followedId, LocalDateTime createdAt) {
        this.followerId = followerId;
        this.followedId = followedId;
        this.createdAt = createdAt;
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
        FollowDocument that = (FollowDocument) o;
        return Objects.equals(followerId, that.followerId) && 
               Objects.equals(followedId, that.followedId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(followerId, followedId);
    }
    
    @Override
    public String toString() {
        return "FollowDocument{" +
                "id='" + id + '\'' +
                ", followerId='" + followerId + '\'' +
                ", followedId='" + followedId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
} 