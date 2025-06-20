package com.uala.microblog.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.uala.microblog.infrastructure.repository")
public class MongoConfig {
    
} 