package com.uala.microblog.infrastructure.repository;

import com.uala.microblog.infrastructure.document.FollowDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MongoFollowRepository extends MongoRepository<FollowDocument, String> {
    
    Optional<FollowDocument> findByFollowerIdAndFollowedId(String followerId, String followedId);
    
    List<FollowDocument> findByFollowerId(String followerId);
    
    List<FollowDocument> findByFollowedId(String followedId);
    
    @Query(value = "{ 'follower_id': ?0 }", fields = "{ 'followed_id': 1 }")
    List<FollowDocument> findFollowedIdsByFollowerId(String followerId);
    
    @Query(value = "{ 'followed_id': ?0 }", fields = "{ 'follower_id': 1 }")
    List<FollowDocument> findFollowerIdsByFollowedId(String followedId);
    
    boolean existsByFollowerIdAndFollowedId(String followerId, String followedId);
    
    void deleteByFollowerIdAndFollowedId(String followerId, String followedId);
    
    long countByFollowerId(String followerId);
    
    long countByFollowedId(String followedId);
} 