package com.uala.microblog.infrastructure.mapper;

import com.uala.microblog.domain.entity.Tweet;
import com.uala.microblog.infrastructure.document.TweetDocument;
public class TweetDocumentMapper {
    
    public static Tweet toDomain(TweetDocument document) {
        if (document == null) {
            return null;
        }
        
        return new Tweet(
            document.getId(),
            document.getContent(),
            document.getUserId(),
            document.getCreatedAt()
        );
    }
    
    public static TweetDocument toDocument(Tweet tweet) {
        if (tweet == null) {
            return null;
        }
        
        TweetDocument document = new TweetDocument(
            tweet.getContent(),
            tweet.getUserId(),
            tweet.getCreatedAt()
        );
        document.setId(tweet.getId());
        return document;
    }
    
    public static TweetDocument toDocumentForCreation(Tweet tweet) {
        if (tweet == null) {
            return null;
        }
        
        TweetDocument document = new TweetDocument(
            tweet.getContent(),
            tweet.getUserId(),
            tweet.getCreatedAt()
        );
        
        
        document.setId(null);
        
        return document;
    }
} 