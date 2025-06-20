package com.uala.microblog.infrastructure.repository;

import com.uala.microblog.infrastructure.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoUserRepository extends MongoRepository<UserDocument, String> {
    

    
    Optional<UserDocument> findByUsername(String username);
    
    Optional<UserDocument> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    long count();
} 