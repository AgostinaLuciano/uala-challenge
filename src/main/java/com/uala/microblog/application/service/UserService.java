package com.uala.microblog.application.service;

import com.uala.microblog.domain.entity.User;
import com.uala.microblog.domain.entity.Tweet;
import com.uala.microblog.domain.port.UserRepository;
import com.uala.microblog.domain.port.FollowRepository;
import com.uala.microblog.domain.port.TweetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final TweetRepository tweetRepository;
    
    public UserService(UserRepository userRepository, FollowRepository followRepository, TweetRepository tweetRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.tweetRepository = tweetRepository;
    }
    
    public User createUser(String username, String email) {
        
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        
        
        User user = new User(username, email);
        
        return userRepository.save(user);
    }
    
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getUserById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
    }
    
    public List<User> getFollowers(String userId) {
        List<String> followerIds = followRepository.findFollowersByUserId(userId);
        return followerIds.stream()
            .map(this::getUserById)
            .toList();
    }
    
    public List<User> getFollowing(String userId) {
        List<String> followingIds = followRepository.findFollowedUserIdsByFollowerId(userId);
        return followingIds.stream()
            .map(this::getUserById)
            .toList();
    }
    
    public List<Tweet> getUserTweets(String userId) {
        return tweetRepository.findByUserId(userId);
    }
} 