package com.uala.microblog.infrastructure.adapter;

import com.uala.microblog.domain.entity.Follow;
import com.uala.microblog.domain.port.FollowRepository;
import com.uala.microblog.infrastructure.mapper.FollowDocumentMapper;
import com.uala.microblog.infrastructure.repository.MongoFollowRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Primary
public class FollowRepositoryAdapter implements FollowRepository {
    
    private final MongoFollowRepository mongoFollowRepository;
    
    public FollowRepositoryAdapter(MongoFollowRepository mongoFollowRepository) {
        this.mongoFollowRepository = mongoFollowRepository;
    }
    
    @Override
    public Follow save(Follow follow) {
        if (follow.getId() == null) {
            
            Follow followForCreation = new Follow(follow.getFollowerId(), follow.getFollowedId());
            return FollowDocumentMapper.toDomain(
                mongoFollowRepository.save(FollowDocumentMapper.toDocumentForCreation(followForCreation))
            );
        } else {
            
            return FollowDocumentMapper.toDomain(
                mongoFollowRepository.save(FollowDocumentMapper.toDocument(follow))
            );
        }
    }
    
    @Override
    public List<String> findFollowedUserIdsByFollowerId(String followerId) {
        return mongoFollowRepository.findFollowedIdsByFollowerId(followerId).stream()
            .map(doc -> doc.getFollowedId())
            .collect(Collectors.toList());
    }
    
    @Override
    public List<String> findFollowersByUserId(String userId) {
        return mongoFollowRepository.findFollowerIdsByFollowedId(userId).stream()
            .map(doc -> doc.getFollowerId())
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByFollowerIdAndFollowedId(String followerId, String followedId) {
        return mongoFollowRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
    }
    
    
    public Optional<Follow> findById(String id) {
        return mongoFollowRepository.findById(id)
            .map(FollowDocumentMapper::toDomain);
    }
    
    public List<Follow> findByFollowerId(String followerId) {
        return mongoFollowRepository.findByFollowerId(followerId).stream()
            .map(FollowDocumentMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    public List<Follow> findByFollowedId(String followedId) {
        return mongoFollowRepository.findByFollowedId(followedId).stream()
            .map(FollowDocumentMapper::toDomain)
            .collect(Collectors.toList());
    }
} 