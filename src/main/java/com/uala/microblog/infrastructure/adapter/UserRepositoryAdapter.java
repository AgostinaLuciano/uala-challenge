package com.uala.microblog.infrastructure.adapter;

import com.uala.microblog.domain.entity.User;
import com.uala.microblog.domain.port.UserRepository;
import com.uala.microblog.infrastructure.mapper.UserDocumentMapper;
import com.uala.microblog.infrastructure.repository.MongoUserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Primary
public class UserRepositoryAdapter implements UserRepository {
    
    private final MongoUserRepository mongoUserRepository;
    
    public UserRepositoryAdapter(MongoUserRepository mongoUserRepository) {
        this.mongoUserRepository = mongoUserRepository;
    }
    
    @Override
    public User save(User user) {
        if (user.getId() == null) {
            
            User userForCreation = new User(user.getUsername(), user.getEmail());
            return UserDocumentMapper.toDomain(
                mongoUserRepository.save(UserDocumentMapper.toDocumentForCreation(userForCreation))
            );
        } else {
            
            return UserDocumentMapper.toDomain(
                mongoUserRepository.save(UserDocumentMapper.toDocument(user))
            );
        }
    }
    

    
    @Override
    public Optional<User> findById(String id) {
        return mongoUserRepository.findById(id)
            .map(UserDocumentMapper::toDomain);
    }
    
    @Override
    public List<User> findAll() {
        return mongoUserRepository.findAll().stream()
            .map(UserDocumentMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return mongoUserRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return mongoUserRepository.existsByEmail(email);
    }
    
    
    public Optional<User> findByUsername(String username) {
        return mongoUserRepository.findByUsername(username)
            .map(UserDocumentMapper::toDomain);
    }
    
    public Optional<User> findByEmail(String email) {
        return mongoUserRepository.findByEmail(email)
            .map(UserDocumentMapper::toDomain);
    }
} 