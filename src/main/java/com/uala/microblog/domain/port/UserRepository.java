package com.uala.microblog.domain.port;

import com.uala.microblog.domain.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    
    User save(User user);
    
    Optional<User> findById(String id);
    
    List<User> findAll();
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
} 