#!/bin/bash

echo "🔍 DIAGNÓSTICO DE REDIS - Microblog Uala Challenge"
echo "================================================="
echo ""

# Verificar si Docker está corriendo
echo "1. Verificando Docker..."
if docker info >/dev/null 2>&1; then
    echo "✅ Docker está corriendo"
else
    echo "❌ Docker no está corriendo o no está accesible"
    exit 1
fi

echo ""

# Verificar si Redis container está corriendo
echo "2. Verificando contenedor Redis..."
REDIS_CONTAINER=$(docker ps --filter "name=microblog-redis-dev" --format "{{.Names}}")
if [ -n "$REDIS_CONTAINER" ]; then
    echo "✅ Contenedor Redis está corriendo: $REDIS_CONTAINER"
    
    # Obtener status del contenedor
    REDIS_STATUS=$(docker ps --filter "name=microblog-redis-dev" --format "{{.Status}}")
    echo "   Status: $REDIS_STATUS"
else
    echo "❌ Contenedor Redis no está corriendo"
    echo "💡 Solución: docker-compose -f docker-compose.dev.yml up -d redis"
    exit 1
fi

echo ""

# Test de conexión Redis
echo "3. Testing conexión Redis..."
if docker exec $REDIS_CONTAINER redis-cli -a password123 ping >/dev/null 2>&1; then
    echo "✅ Redis responde correctamente"
else
    echo "❌ Redis no responde o hay problema de autenticación"
    echo "🔧 Verificando logs del contenedor..."
    docker logs --tail 10 $REDIS_CONTAINER
fi

echo ""

# Verificar configuración Redis
echo "4. Información de configuración Redis..."
echo "   Host: localhost"
echo "   Puerto: 6379"
echo "   Password: password123"
echo "   Database: 0"

echo ""

# Test de conectividad desde host
echo "5. Testing conectividad desde host..."
if command -v redis-cli &> /dev/null; then
    if redis-cli -h localhost -p 6379 -a password123 ping >/dev/null 2>&1; then
        echo "✅ Conexión desde host exitosa"
    else
        echo "❌ No se puede conectar desde host"
        echo "💡 Verificar que el puerto 6379 esté expuesto"
    fi
else
    echo "ℹ️ redis-cli no está instalado en el host (esto es normal)"
    echo "   Para instalarlo: brew install redis (macOS) o apt-get install redis-tools (Ubuntu)"
fi

echo ""

# Verificar health check de aplicación
echo "6. Verificando health check de aplicación..."
if curl -s http://localhost:8080/actuator/health >/dev/null 2>&1; then
    echo "✅ Aplicación está corriendo"
    echo "🔗 Health check disponible en: http://localhost:8080/actuator/health"
    
    # Verificar específicamente Redis health
    REDIS_HEALTH=$(curl -s http://localhost:8080/actuator/health | grep -o '"redis":[^}]*}' 2>/dev/null)
    if [ -n "$REDIS_HEALTH" ]; then
        echo "📊 Redis health check: $REDIS_HEALTH"
    fi
else
    echo "ℹ️ Aplicación no está corriendo (esto es normal si no has iniciado Spring Boot)"
fi

echo ""

# Sugerencias de diagnóstico
echo "💡 SUGERENCIAS DE DIAGNÓSTICO:"
echo "   - Para ver logs de Redis: docker logs -f microblog-redis-dev"
echo "   - Para conectarse a Redis: docker exec -it microblog-redis-dev redis-cli -a password123"
echo "   - Para reiniciar Redis: docker-compose -f docker-compose.dev.yml restart redis"
echo "   - Para habilitar debug de Redis en app: descomentar líneas de logging en application.yml"

echo ""
echo "🏁 Diagnóstico completado" 