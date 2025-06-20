package com.uala.microblog.application.service;

import com.uala.microblog.domain.entity.Tweet;
import com.uala.microblog.domain.port.TweetRepository;
import com.uala.microblog.domain.port.UserRepository;
import com.uala.microblog.infrastructure.messaging.FanoutMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TweetService {
    
    private static final Logger logger = LoggerFactory.getLogger(TweetService.class);
    
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final FanoutMessageService fanoutMessageService;
    
    public TweetService(TweetRepository tweetRepository, UserRepository userRepository, FanoutMessageService fanoutMessageService) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.fanoutMessageService = fanoutMessageService;
    }
    
    public Tweet createTweet(String content, String userId) {
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Tweet content is required");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        
        
        if (!userRepository.findById(userId).isPresent()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        
        
        Tweet tweet = new Tweet(content.trim(), userId);
        tweet.validate();
        
        
        Tweet savedTweet = tweetRepository.save(tweet);
        
        
        try {
            fanoutMessageService.sendFanoutMessage(savedTweet);
            logger.info("Tweet {} created by user {} - PUSH FANOUT message sent for timeline distribution", 
                savedTweet.getId(), userId);
        } catch (Exception e) {
            logger.error("Error sending fanout message for tweet {}: {}", savedTweet.getId(), e.getMessage(), e);
            
        }
        
        return savedTweet;
    }
    
    public List<Tweet> findTweetsByUserIds(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        
        List<String> validUserIds = userIds.stream()
            .filter(id -> id != null && !id.trim().isEmpty())
            .toList();
        
        if (validUserIds.isEmpty()) {
            return List.of();
        }
        
        return tweetRepository.findByUserIdInOrderByCreatedAtDesc(validUserIds);
    }
    
    /**
     * Find recent tweets by multiple user IDs with limit (optimized for timeline)
     * This method is crucial for performance at scale - limits results to avoid memory issues
     */
    public List<Tweet> findRecentTweetsByUserIds(List<String> userIds, int limit) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        
        
        List<String> validUserIds = userIds.stream()
            .filter(id -> id != null && !id.trim().isEmpty())
            .toList();
        
        if (validUserIds.isEmpty()) {
            return List.of();
        }
        
        return tweetRepository.findRecentTweetsByUserIds(validUserIds, limit);
    }
} 