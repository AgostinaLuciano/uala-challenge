package com.uala.microblog.infrastructure.mapper;

import com.uala.microblog.domain.entity.Follow;
import com.uala.microblog.infrastructure.document.FollowDocument;
public class FollowDocumentMapper {
    
    public static Follow toDomain(FollowDocument document) {
        if (document == null) {
            return null;
        }
        
        return new Follow(
            document.getId(),
            document.getFollowerId(),
            document.getFollowedId(),
            document.getCreatedAt()
        );
    }
    
    public static FollowDocument toDocument(Follow follow) {
        if (follow == null) {
            return null;
        }
        
        FollowDocument document = new FollowDocument(
            follow.getFollowerId(),
            follow.getFollowedId(),
            follow.getCreatedAt()
        );
        document.setId(follow.getId());
        return document;
    }
    
    public static FollowDocument toDocumentForCreation(Follow follow) {
        if (follow == null) {
            return null;
        }
        
        FollowDocument document = new FollowDocument(
            follow.getFollowerId(),
            follow.getFollowedId(),
            follow.getCreatedAt()
        );
        
        
        document.setId(null);
        
        return document;
    }
} 