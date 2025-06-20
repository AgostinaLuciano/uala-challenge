package com.uala.microblog.application.service;

import com.uala.microblog.domain.entity.Follow;
import com.uala.microblog.domain.port.FollowRepository;
import com.uala.microblog.domain.port.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class FollowService {
    
    private static final Logger logger = LoggerFactory.getLogger(FollowService.class);
    
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    
    @Autowired
    @Lazy
    private TimelineService timelineService;
    
    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }
    
    @CacheEvict(value = {"follows", "timeline"}, key = "#followerId")
    public Follow followUser(String followerId, String followedId) {
        
        if (followerId == null || followerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Follower ID is required");
        }
        if (followedId == null || followedId.trim().isEmpty()) {
            throw new IllegalArgumentException("Followed ID is required");
        }
        
        
        if (!userRepository.findById(followerId).isPresent()) {
            throw new IllegalArgumentException("Follower user not found with ID: " + followerId);
        }
        if (!userRepository.findById(followedId).isPresent()) {
            throw new IllegalArgumentException("Followed user not found with ID: " + followedId);
        }
        
        
        if (followRepository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
            throw new IllegalArgumentException("User " + followerId + " is already following user " + followedId);
        }
        
        
        Follow follow = new Follow(followerId, followedId);
        follow.validate();
        
        Follow savedFollow = followRepository.save(follow);
        
        
        
        try {
            timelineService.invalidateUserTimeline(followerId);
        } catch (Exception e) {
            logger.warn("Failed to invalidate timeline for user {} after following user {}. Timeline will be rebuilt automatically on next access. Error: {}", 
                       followerId, followedId, e.getMessage(), e);
        }
        
        return savedFollow;
    }
    
    @Cacheable(value = "follows", key = "#followerId")
    public List<String> getFollowedUserIds(String followerId) {
        if (followerId == null || followerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Follower ID is required");
        }
        return followRepository.findFollowedUserIdsByFollowerId(followerId);
    }
    
    @Cacheable(value = "follows", key = "'followers:' + #userId")
    public List<String> getFollowersIds(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        return followRepository.findFollowersByUserId(userId);
    }
} 