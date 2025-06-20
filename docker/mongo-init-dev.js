// Mongo Init Script for Development (No Authentication)
// Este script se ejecuta cuando se inicializa MongoDB por primera vez

// Usar la base de datos microblog
db = db.getSiblingDB('microblog');

print('Setting up microblog database for development...');

// Crear colecciones si no existen
db.createCollection("users");
db.createCollection("tweets");
db.createCollection("follows");
db.createCollection("user_timeline");

print('Collections created successfully');

// Índices para Users
db.users.createIndex({ "username": 1 }, { unique: true });
db.users.createIndex({ "email": 1 }, { unique: true });
db.users.createIndex({ "created_at": 1 });

print('User indexes created successfully');

// Índices para Tweets
db.tweets.createIndex({ "user_id": 1, "created_at": -1 });
db.tweets.createIndex({ "created_at": -1 });

print('Tweet indexes created successfully');

// Índices para Follows
db.follows.createIndex({ "follower_id": 1, "followed_id": 1 }, { unique: true });
db.follows.createIndex({ "follower_id": 1 });
db.follows.createIndex({ "followed_id": 1 });

print('Follow indexes created successfully');

// Índices para Timeline
db.user_timeline.createIndex({ "user_id": 1, "created_at": -1 });
db.user_timeline.createIndex({ "tweet_id": 1 });

print('Timeline indexes created successfully');

print('MongoDB development database setup completed successfully!'); 