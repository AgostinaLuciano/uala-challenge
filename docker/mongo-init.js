// MongoDB initialization script
// Se ejecuta cuando se crea el contenedor por primera vez

// Cambiar a la base de datos microblog
db = db.getSiblingDB('microblog');

// Crear usuario para la aplicación
db.createUser({
  user: 'microblog_user',
  pwd: 'microblog_password',
  roles: [
    {
      role: 'readWrite',
      db: 'microblog'
    }
  ]
});

// Crear colecciones con índices optimizados
db.createCollection('users');
db.createCollection('tweets');
db.createCollection('follows');
db.createCollection('user_timeline');

// Índices para Users
db.users.createIndex({ "username": 1 }, { unique: true });
db.users.createIndex({ "email": 1 }, { unique: true });
db.users.createIndex({ "created_at": 1 });

// Índices para Tweets
db.tweets.createIndex({ "user_id": 1 });
db.tweets.createIndex({ "created_at": -1 });
db.tweets.createIndex({ "user_id": 1, "created_at": -1 });

// Índices para Follows
db.follows.createIndex({ "follower_id": 1 });
db.follows.createIndex({ "followed_id": 1 });
db.follows.createIndex({ "follower_id": 1, "followed_id": 1 }, { unique: true });
db.follows.createIndex({ "created_at": 1 });

// Índices para User Timeline (Push Fanout)
db.user_timeline.createIndex({ "user_id": 1 });
db.user_timeline.createIndex({ "created_at": -1 });
db.user_timeline.createIndex({ "user_id": 1, "created_at": -1 });

print('MongoDB initialization completed successfully!');
print('Database: microblog');
print('User created: microblog_user');
print('Collections created with optimized indexes'); 