package com.uala.microblog.domain.port;

import com.uala.microblog.domain.entity.Tweet;
import java.util.List;
import java.util.Optional;

public interface TweetRepository {
    
    Tweet save(Tweet tweet);
    
    Optional<Tweet> findById(String id);
    
    List<Tweet> findByUserIdInOrderByCreatedAtDesc(List<String> userIds);
    
    List<Tweet> findByUserId(String userId);
    
    /**
     * Find recent tweets by user IDs with limit for performance optimization
     */
    List<Tweet> findRecentTweetsByUserIds(List<String> userIds, int limit);
} 