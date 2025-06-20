package com.uala.microblog.infrastructure.adapter;

import com.uala.microblog.domain.entity.Tweet;
import com.uala.microblog.domain.port.TweetRepository;
import com.uala.microblog.infrastructure.mapper.TweetDocumentMapper;
import com.uala.microblog.infrastructure.repository.MongoTweetRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Primary
public class TweetRepositoryAdapter implements TweetRepository {
    
    private final MongoTweetRepository mongoTweetRepository;
    
    public TweetRepositoryAdapter(MongoTweetRepository mongoTweetRepository) {
        this.mongoTweetRepository = mongoTweetRepository;
    }
    
    @Override
    public Tweet save(Tweet tweet) {
        if (tweet.getId() == null) {
            
            Tweet tweetForCreation = new Tweet(tweet.getContent(), tweet.getUserId());
            return TweetDocumentMapper.toDomain(
                mongoTweetRepository.save(TweetDocumentMapper.toDocumentForCreation(tweetForCreation))
            );
        } else {
            
            return TweetDocumentMapper.toDomain(
                mongoTweetRepository.save(TweetDocumentMapper.toDocument(tweet))
            );
        }
    }
    

    
    @Override
    public Optional<Tweet> findById(String id) {
        return mongoTweetRepository.findById(id)
            .map(TweetDocumentMapper::toDomain);
    }
    
    @Override
    public List<Tweet> findByUserId(String userId) {
        return mongoTweetRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(TweetDocumentMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Tweet> findByUserIdInOrderByCreatedAtDesc(List<String> userIds) {
        return mongoTweetRepository.findByUserIdInOrderByCreatedAtDesc(userIds).stream()
            .map(TweetDocumentMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Tweet> findRecentTweetsByUserIds(List<String> userIds, int limit) {
        return mongoTweetRepository.findRecentTweetsByUserIds(userIds, Limit.of(limit)).stream()
            .map(TweetDocumentMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    
    
    public List<Tweet> findAll() {
        return mongoTweetRepository.findAllOrderByCreatedAtDesc().stream()
            .map(TweetDocumentMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    public List<Tweet> findRecentTweetsByUserId(String userId, int limit) {
        return mongoTweetRepository.findRecentTweetsByUserId(userId, Limit.of(limit)).stream()
            .map(TweetDocumentMapper::toDomain)
            .collect(Collectors.toList());
    }
} 