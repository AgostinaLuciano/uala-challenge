package com.uala.microblog.infrastructure.repository;

import com.uala.microblog.infrastructure.document.TweetDocument;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MongoTweetRepository extends MongoRepository<TweetDocument, String> {
    

    
    List<TweetDocument> findByUserIdOrderByCreatedAtDesc(String userId);
    
    @Query("{ 'user_id': { $in: ?0 } }")
    List<TweetDocument> findByUserIdInOrderByCreatedAtDesc(List<String> userIds);
    
    @Query(value = "{ 'user_id': { $in: ?0 } }", sort = "{ 'created_at': -1 }")
    List<TweetDocument> findRecentTweetsByUserIds(List<String> userIds, Limit limit);
    
    @Query(value = "{ 'user_id': ?0 }", sort = "{ 'created_at': -1 }")
    List<TweetDocument> findRecentTweetsByUserId(String userId, Limit limit);
    
    @Query(value = "{}", sort = "{ 'created_at': -1 }")
    List<TweetDocument> findAllOrderByCreatedAtDesc();
    
    long countByUserId(String userId);
    

} 