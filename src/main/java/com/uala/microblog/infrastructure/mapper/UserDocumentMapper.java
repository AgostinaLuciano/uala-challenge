package com.uala.microblog.infrastructure.mapper;

import com.uala.microblog.domain.entity.User;
import com.uala.microblog.infrastructure.document.UserDocument;
public class UserDocumentMapper {
    
    public static User toDomain(UserDocument document) {
        if (document == null) {
            return null;
        }
        
        return new User(
            document.getId(),
            document.getUsername(),
            document.getEmail(),
            document.getCreatedAt()
        );
    }
    
    public static UserDocument toDocument(User user) {
        if (user == null) {
            return null;
        }
        
        UserDocument document = new UserDocument(
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt()
        );
        document.setId(user.getId());
        return document;
    }
    
    public static UserDocument toDocumentForCreation(User user) {
        if (user == null) {
            return null;
        }
        
        UserDocument document = new UserDocument(
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt()
        );
        
        
        document.setId(null);
        
        return document;
    }
} 