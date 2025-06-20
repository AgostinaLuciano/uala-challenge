# Microblogging Platform

Una plataforma de microblogging simplificada similar a Twitter, construida con Spring Boot y arquitectura de microservicios escalable.

## 🚀 Características

- **Gestión de usuarios**: Registro y autenticación de usuarios
- **Tweets**: Creación y consulta de tweets con límite de caracteres
- **Sistema de seguimiento**: Follow/Unfollow entre usuarios
- **Timeline**: Timeline personalizado con Push/Pull Fanout para optimizar lecturas
- **Caché Redis**: Almacenamiento en caché para consultas frecuentes
- **Mensajería asíncrona**: RabbitMQ para procesamiento de tareas en background
- **Métricas y monitoreo**: Actuator para observabilidad

## 🛠️ Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **MongoDB** - Base de datos NoSQL
- **Redis** - Caché y almacenamiento en memoria
- **RabbitMQ** - Cola de mensajes
- **Docker & Docker Compose** - Containerización
- **Maven** - Gestión de dependencias
- **JUnit 5** - Testing

## 📋 Requisitos Previos

- Java 17+
- Maven 3.6+
- Docker y Docker Compose
- Git

## 🚦 Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone <repository-url>
cd uala-challenge
```

### 2. Levantar los servicios con Docker
```bash
# Levantar MongoDB, Redis y RabbitMQ
docker-compose -f docker-compose.dev.yml up -d

# Verificar que los servicios estén corriendo
docker-compose -f docker-compose.dev.yml ps

# Ver logs de los servicios (opcional)
docker-compose -f docker-compose.dev.yml logs -f
```

### 3. Ejecutar la aplicación

#### Opción A: Maven
```bash
mvn spring-boot:run
```

#### Opción B: Con JAR compilado
```bash
mvn clean package
java -jar target/microblogging-platform-1.0.0.jar
```

### 4. Verificar instalación
```bash
# Health check
curl http://localhost:8080/actuator/health

# Crear un usuario de prueba
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "email": "test@example.com"}'
```

## 📡 API Endpoints

### Usuarios
```http
POST   /users                     # Crear usuario
GET    /users/{userId}            # Obtener usuario por ID
GET    /users/{userId}/followers  # Obtener seguidores
GET    /users/{userId}/following  # Obtener usuarios seguidos
GET    /users/{userId}/tweets     # Obtener tweets del usuario
```

### Tweets
```http
POST   /tweets                    # Crear tweet
GET    /tweets/{tweetId}          # Obtener tweet por ID
```

### Follows
```http
POST   /follows                   # Seguir usuario
DELETE /follows                   # Dejar de seguir usuario
```

### Timeline
```http
GET    /timeline?userId={userId}  # Obtener timeline del usuario
```

### Monitoreo
```http
GET    /actuator/health           # Estado de la aplicación
GET    /actuator/metrics          # Métricas de la aplicación
```

## 🗂️ Estructura del Proyecto

```
src/main/java/com/uala/microblog/
├── application/service/          # Servicios de aplicación
├── domain/                       # Entidades del dominio
│   ├── entity/                   # Entidades de negocio
│   └── port/                     # Interfaces de repositorio
├── infrastructure/               # Capa de infraestructura
│   ├── adapter/                  # Adaptadores de repositorio
│   ├── config/                   # Configuraciones
│   ├── controller/               # Controladores REST
│   ├── document/                 # Documentos MongoDB
│   ├── mapper/                   # Mappers entre entidades
│   ├── messaging/                # Mensajería RabbitMQ
│   └── repository/               # Implementaciones de repositorio
└── MicroblogApplication.java     # Clase principal
```

## 🧪 Testing

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con reporte de cobertura
mvn clean test jacoco:report

# Ver reporte de cobertura
open target/site/jacoco/index.html
```

## 🔧 Configuración

El proyecto usa perfiles de Spring para diferentes entornos:

- **local** (default): Para desarrollo local
- **prod**: Para producción

Las configuraciones se encuentran en `src/main/resources/application.yml`.

## 📊 Monitoreo y Métricas

- **Health checks**: `http://localhost:8080/actuator/health`
- **Métricas**: `http://localhost:8080/actuator/metrics`
- **Logs**: Se almacenan en el directorio `logs/`

## 🐳 Docker

Los servicios de infraestructura se pueden levantar con:

```bash
# Desarrollo
docker-compose -f docker-compose.dev.yml up -d

# Parar servicios
docker-compose -f docker-compose.dev.yml down

# Parar servicios y eliminar volúmenes
docker-compose -f docker-compose.dev.yml down -v

# Ver logs
docker-compose -f docker-compose.dev.yml logs -f
```

## 🛠️ Scripts y Herramientas Útiles

- **`comandos utiles.txt`**: Comandos frecuentes para desarrollo
- **`diagnose-redis.sh`**: Script para diagnosticar problemas con Redis
- **`Uala_Microblog_Collection.postman_collection.json`**: Colección de Postman para probar la API

### Uso de la colección de Postman
1. Importar `Uala_Microblog_Collection.postman_collection.json` en Postman
2. Configurar las variables de entorno si es necesario
3. Ejecutar las requests para probar todos los endpoints

### Diagnóstico de Redis
```bash
# Ejecutar diagnóstico completo de Redis
chmod +x diagnose-redis.sh
./diagnose-redis.sh
```

## 🚨 Solución de Problemas

### Puerto ocupado
```bash
# Verificar qué proceso usa el puerto 8080
lsof -i :8080

# Terminar proceso que usa el puerto
lsof -ti :8080 | xargs kill -9
```

### Problemas con Docker
```bash
# Reiniciar todos los servicios
docker-compose -f docker-compose.dev.yml down
docker-compose -f docker-compose.dev.yml up -d

# Ver estado de los contenedores
docker ps -a

# Ver logs específicos de un servicio
docker-compose -f docker-compose.dev.yml logs [servicio]
```

## 📚 Documentación Adicional

- [Optimizaciones Locales](QUICK_LOCAL_OPTIMIZATIONS.md)
- [Recomendaciones de Escalabilidad](ARCHITECTURE_SCALING_RECOMMENDATIONS.md)
- [Implementación Push Fanout](PUSH_FANOUT_IMPLEMENTATION.md)

## 📋 Credenciales por Defecto

### MongoDB
- **Host**: localhost:27017
- **Database**: microblog
- **Autenticación**: No requerida en desarrollo

### Redis
- **Host**: localhost:6379
- **Password**: password123
- **Database**: 0

### RabbitMQ
- **Host**: localhost:5672
- **Management UI**: http://localhost:15672
- **Usuario**: admin
- **Password**: password123

---

**Versión**: 1.0.0  
**Autor**: Uala Challenge  
**Tecnología**: Spring Boot 3.2.0 + Java 17 