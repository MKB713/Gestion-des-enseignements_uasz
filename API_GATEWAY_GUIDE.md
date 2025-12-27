# Guide API Gateway - Point d'entrée unique DAOS

## Vue d'ensemble

L'API Gateway est le point d'entrée unique pour tous les microservices DAOS. Il fournit le routage, le load balancing, la gestion des CORS, le rate limiting, le circuit breaker et le logging centralisé.

## Architecture

```
                        Clients (Web, Mobile, API)
                                    │
                                    ▼
                    ┌───────────────────────────────┐
                    │     API Gateway (:8080)        │
                    │  ┌─────────────────────────┐  │
                    │  │ Filtres Globaux         │  │
                    │  │ - Logging               │  │
                    │  │ - CORS                  │  │
                    │  │ - Rate Limiting         │  │
                    │  │ - Circuit Breaker       │  │
                    │  └─────────────────────────┘  │
                    │                                │
                    │  ┌─────────────────────────┐  │
                    │  │ Routage Dynamique       │  │
                    │  │ (Eureka Discovery)      │  │
                    │  └─────────────────────────┘  │
                    │                                │
                    │  ┌─────────────────────────┐  │
                    │  │ Load Balancing          │  │
                    │  │ (Round-robin)           │  │
                    │  └─────────────────────────┘  │
                    └───────────────────────────────┘
                                    │
            ┌───────────────────────┼───────────────────────┐
            │                       │                       │
      ┌─────▼─────┐         ┌───────▼──────┐      ┌────────▼────────┐
      │   Auth    │         │  Enseignant  │      │    Maquette     │
      │  Service  │         │   Service    │      │    Service      │
      │   :8081   │         │    :8082     │      │     :8083       │
      └───────────┘         └──────────────┘      └─────────────────┘
            │                       │                       │
      ┌─────▼─────┐         ┌───────▼──────┐      ┌────────▼────────┐
      │   Choix   │         │Emploi Temps  │      │  Déroulement    │
      │   :8084   │         │   :8085      │      │     :8086       │
      └───────────┘         └──────────────┘      └─────────────────┘
```

## Configuration

### Dépendances Maven

**api-gateway/pom.xml**
```xml
<dependencies>
    <!-- Spring Cloud Gateway -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>

    <!-- Eureka Client pour Service Discovery -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    <!-- Actuator pour monitoring -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- Swagger Aggregation -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
        <version>2.2.0</version>
    </dependency>

    <!-- Circuit Breaker (Resilience4j) -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
    </dependency>

    <!-- Redis pour Rate Limiting -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
    </dependency>
</dependencies>
```

### Application Principal

**api-gateway/src/main/java/com/uasz/daos/gateway/ApiGatewayApplication.java**
```java
package com.uasz.daos.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
```

### Configuration complète (application.properties)

```properties
# Server Configuration
server.port=8080
spring.application.name=api-gateway

# Gateway Discovery
spring.cloud.gateway.discovery.locator.enabled=true
spring.cloud.gateway.discovery.locator.lower-case-service-id=true

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true

# CORS Global Configuration
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-origin-patterns=*
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-methods=GET,POST,PUT,DELETE,PATCH,OPTIONS,HEAD
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-headers=*
spring.cloud.gateway.globalcors.cors-configurations.[/**].allow-credentials=true
spring.cloud.gateway.globalcors.cors-configurations.[/**].max-age=3600

# Redis Configuration (pour Rate Limiting)
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=2000ms

# Rate Limiting Configuration
spring.cloud.gateway.redis-rate-limiter.replenish-rate=100
spring.cloud.gateway.redis-rate-limiter.burst-capacity=150
spring.cloud.gateway.redis-rate-limiter.requested-tokens=1

# Resilience4j Circuit Breaker Configuration
resilience4j.circuitbreaker.instances.default.sliding-window-size=10
resilience4j.circuitbreaker.instances.default.minimum-number-of-calls=5
resilience4j.circuitbreaker.instances.default.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.default.wait-duration-in-open-state=10000
resilience4j.circuitbreaker.instances.default.permitted-number-of-calls-in-half-open-state=3
resilience4j.circuitbreaker.instances.default.automatic-transition-from-open-to-half-open-enabled=true

# Resilience4j TimeLimiter
resilience4j.timelimiter.instances.default.timeout-duration=3s

# Actuator
management.endpoints.web.exposure.include=*

# Logging
logging.level.org.springframework.cloud.gateway=DEBUG
logging.level.org.springframework.web.cors=DEBUG
```

## Routage Dynamique

### Routes configurées

| Route | Path | Service Cible | Description |
|-------|------|---------------|-------------|
| home-page | `/` | auth-service | Page d'accueil |
| auth-web-pages | `/login`, `/logout`, `/dashboard/**` | auth-service | Pages web Auth |
| auth-static-resources | `/css/**`, `/js/**`, `/img/**` | auth-service | Ressources statiques |
| auth-service-api | `/api/auth/**` | auth-service | API Auth |
| enseignant-service | `/api/enseignants/**` | enseignant-service | API Enseignants |
| maquette-service | `/api/maquettes/**` | maquette-service | API Maquettes |
| choix-enseignement | `/api/choix-enseignements/**` | choix-enseignement-service | API Choix |
| emploi-temps | `/api/emploi-temps/**` | emploi-temps-service | API Emploi du Temps |
| deroulement | `/api/deroulement-enseignements/**` | deroulement-enseignement-service | API Déroulement |

### Load Balancing

Le préfixe `lb://` active automatiquement le load balancing via Eureka :

```properties
spring.cloud.gateway.routes[4].uri=lb://enseignant-service
```

**Fonctionnement :**
- ✅ Round-robin par défaut
- ✅ Découverte automatique des instances via Eureka
- ✅ Health check automatique
- ✅ Exclusion des instances DOWN

**Exemple avec plusieurs instances :**
```
enseignant-service-1 (UP) → Requête 1
enseignant-service-2 (UP) → Requête 2
enseignant-service-3 (DOWN) → Ignorée
enseignant-service-1 (UP) → Requête 3
```

## Filtres de Logging

### Filtre Global de Logging

**LoggingGlobalFilter.java**
```java
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Log de la requête entrante
        logger.info("🔹 Incoming Request: {} {} from {}",
                request.getMethod(),
                request.getPath(),
                getClientIP(request));

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;

            // Log de la réponse sortante
            logger.info("🔸 Outgoing Response: {} {} - Status: {} - Duration: {}ms",
                    request.getMethod(),
                    request.getPath(),
                    exchange.getResponse().getStatusCode(),
                    duration);
        }));
    }

    @Override
    public int getOrder() {
        return -1; // Haute priorité
    }
}
```

**Logs produits :**
```
🔹 Incoming Request: GET /api/enseignants from 192.168.1.10
🔸 Outgoing Response: GET /api/enseignants - Status: 200 OK - Duration: 234ms
```

### Filtre d'Ajout de Headers

**RequestHeaderFilter.java**
```java
@Component
public class RequestHeaderFilter extends AbstractGatewayFilterFactory<Config> {

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var request = exchange.getRequest().mutate()
                    .header("X-Gateway-Request-Id", UUID.randomUUID().toString())
                    .header("X-Gateway-Request-Time", String.valueOf(System.currentTimeMillis()))
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        };
    }
}
```

## Gestion des CORS

### Configuration Globale

**CORS activé pour tous les endpoints :**

```properties
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-origin-patterns=*
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-methods=GET,POST,PUT,DELETE,PATCH,OPTIONS,HEAD
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-headers=*
spring.cloud.gateway.globalcors.cors-configurations.[/**].allow-credentials=true
spring.cloud.gateway.globalcors.cors-configurations.[/**].max-age=3600
```

**Résultat :**
- ✅ Tous les domaines autorisés (`*`)
- ✅ Toutes les méthodes HTTP autorisées
- ✅ Tous les headers autorisés
- ✅ Credentials autorisés (cookies, auth headers)
- ✅ Preflight cache de 1 heure

**Headers de réponse CORS :**
```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD
Access-Control-Allow-Headers: *
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

## Rate Limiting

### Configuration

**Basé sur Redis et l'IP du client :**

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379

# 100 requêtes par minute par IP
spring.cloud.gateway.redis-rate-limiter.replenish-rate=100
spring.cloud.gateway.redis-rate-limiter.burst-capacity=150
spring.cloud.gateway.redis-rate-limiter.requested-tokens=1
```

### KeyResolver

**GatewayConfig.java**
```java
@Configuration
public class GatewayConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress() != null ?
                    exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() :
                    "unknown";
            return Mono.just(ip);
        };
    }
}
```

### Paramètres

- **replenish-rate** : 100 tokens/minute
- **burst-capacity** : 150 tokens maximum en burst
- **requested-tokens** : 1 token par requête

**Exemple :**
- Client A (192.168.1.10) : 100 requêtes/min autorisées
- Client B (192.168.1.20) : 100 requêtes/min autorisées (indépendant)
- Si dépassement : HTTP 429 Too Many Requests

### Application aux routes

Pour appliquer le rate limiting à une route spécifique, ajouter le filtre :

```properties
spring.cloud.gateway.routes[4].filters[1]=RequestRateLimiter
```

## Circuit Breaker (Resilience4j)

### Configuration

**CircuitBreakerConfig.java**
```java
@Configuration
public class CircuitBreakerConfig {

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .failureRateThreshold(50.0f)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(3))
                        .build())
                .build());
    }
}
```

### Paramètres

| Paramètre | Valeur | Description |
|-----------|--------|-------------|
| slidingWindowSize | 10 | Fenêtre glissante de 10 appels |
| minimumNumberOfCalls | 5 | Minimum 5 appels avant décision |
| failureRateThreshold | 50% | Seuil d'échec : 50% |
| waitDurationInOpenState | 10s | Attente avant half-open |
| permittedNumberOfCallsInHalfOpenState | 3 | 3 appels de test en half-open |
| timeoutDuration | 3s | Timeout des requêtes |

### États du Circuit Breaker

```
         ┌─────────┐
         │ CLOSED  │ ← État normal (tout passe)
         └────┬────┘
              │ 50% échecs
              ▼
         ┌─────────┐
         │  OPEN   │ ← Tout bloqué (fallback)
         └────┬────┘
              │ Après 10s
              ▼
       ┌──────────┐
       │HALF-OPEN │ ← 3 appels de test
       └────┬─────┘
            │
      ┌─────┴─────┐
      │           │
  Succès      Échec
      │           │
      ▼           ▼
  ┌───────┐   ┌──────┐
  │CLOSED │   │ OPEN │
  └───────┘   └──────┘
```

### Application aux routes

Pour activer le circuit breaker sur une route :

```properties
spring.cloud.gateway.routes[4].filters[2]=CircuitBreaker=myCircuitBreaker
spring.cloud.gateway.routes[4].filters[3]=FallbackHeaders
```

### Fallback personnalisé

Créer un contrôleur de fallback :

```java
@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public Mono<ResponseEntity<String>> fallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Service temporairement indisponible. Réessayez dans quelques instants."));
    }
}
```

## Docker Configuration

### Dockerfile

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
# Redis (pour Rate Limiting)
redis:
  image: redis:7-alpine
  container_name: daos-redis
  ports:
    - "6379:6379"
  networks:
    - daos-network

# API Gateway
api-gateway:
  build:
    context: ./api-gateway
    dockerfile: Dockerfile
  container_name: api-gateway
  ports:
    - "8080:8080"
  depends_on:
    - eureka-server
    - config-server
    - redis
  environment:
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
    SPRING_DATA_REDIS_HOST: redis
    SPRING_DATA_REDIS_PORT: 6379
  networks:
    - daos-network
```

## Critères d'acceptation

### ✅ Gateway démarre sur le port 8080

**Vérification :**
```bash
curl http://localhost:8080/actuator/health
```

**Résultat attendu :**
```json
{"status":"UP"}
```

### ✅ Routage dynamique basé sur Eureka Discovery

**Configuration :**
```properties
spring.cloud.gateway.discovery.locator.enabled=true
spring.cloud.gateway.routes[4].uri=lb://enseignant-service
```

**Vérification :**
```bash
# Appeler via Gateway
curl http://localhost:8080/api/enseignants

# Vérifier les routes actives
curl http://localhost:8080/actuator/gateway/routes | jq
```

### ✅ Load balancing automatique entre instances

**Fonctionnement :**
- Préfixe `lb://` active le load balancing
- Eureka fournit la liste des instances disponibles
- Spring Cloud LoadBalancer fait le round-robin

**Test avec plusieurs instances :**
```bash
# Démarrer 2 instances d'un service
docker-compose up --scale enseignant-service=2

# Faire plusieurs requêtes
for i in {1..10}; do
  curl http://localhost:8080/api/enseignants
done

# Vérifier les logs : les requêtes sont distribuées
```

### ✅ Filtres de logging des requêtes/réponses

**Implémentation :**
- `LoggingGlobalFilter.java` : Filtre global
- `RequestHeaderFilter.java` : Ajout de headers

**Vérification :**
```bash
# Faire une requête
curl http://localhost:8080/api/enseignants

# Vérifier les logs du Gateway
docker logs api-gateway

# Logs attendus :
# 🔹 Incoming Request: GET /api/enseignants from 172.18.0.1
# 🔸 Outgoing Response: GET /api/enseignants - Status: 200 OK - Duration: 156ms
```

### ✅ Gestion des CORS

**Configuration :**
```properties
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-origin-patterns=*
```

**Vérification :**
```bash
# Faire une requête OPTIONS (preflight)
curl -X OPTIONS http://localhost:8080/api/enseignants \
  -H "Origin: http://example.com" \
  -H "Access-Control-Request-Method: POST" \
  -v

# Vérifier les headers de réponse :
# Access-Control-Allow-Origin: *
# Access-Control-Allow-Methods: GET, POST, PUT, DELETE...
```

### ✅ Rate limiting configuré

**Configuration :**
- Redis déployé
- KeyResolver basé sur IP
- Limite : 100 req/min par IP

**Vérification :**
```bash
# Faire 150 requêtes rapidement
for i in {1..150}; do
  curl http://localhost:8080/api/enseignants
done

# Les 100 premières passent (200 OK)
# Les suivantes sont bloquées (429 Too Many Requests)
```

**Vérifier dans Redis :**
```bash
docker exec -it daos-redis redis-cli
> KEYS *
> GET request_rate_limiter.{192.168.1.10}.tokens
```

### ✅ Circuit breaker implémenté (Resilience4j)

**Configuration :**
- Resilience4j configuré
- Seuil : 50% d'échecs sur 10 appels
- État OPEN pendant 10 secondes

**Vérification :**
```bash
# Arrêter un service backend
docker stop enseignant-service

# Faire plusieurs requêtes
for i in {1..10}; do
  curl http://localhost:8080/api/enseignants
done

# Les 5 premières tentent de contacter le service
# Après 50% d'échecs, circuit breaker s'ouvre
# Les suivantes retournent immédiatement une erreur/fallback

# Vérifier l'état du circuit breaker
curl http://localhost:8080/actuator/circuitbreakers
```

### ✅ Le service est conteneurisé avec Docker

**Vérification :**
```bash
docker ps | grep api-gateway
```

**Résultat attendu :**
```
api-gateway   Up X minutes   0.0.0.0:8080->8080/tcp
```

## Endpoints de monitoring

| Endpoint | URL | Description |
|----------|-----|-------------|
| Health | http://localhost:8080/actuator/health | État de santé |
| Routes | http://localhost:8080/actuator/gateway/routes | Liste des routes |
| Filters | http://localhost:8080/actuator/gateway/globalfilters | Filtres globaux |
| Circuit Breakers | http://localhost:8080/actuator/circuitbreakers | État des circuit breakers |
| Metrics | http://localhost:8080/actuator/metrics | Métriques Micrometer |

## Tests

### Test 1 : Routage basique

```bash
# Tester chaque route
curl http://localhost:8080/
curl http://localhost:8080/api/auth/health
curl http://localhost:8080/api/enseignants
curl http://localhost:8080/api/maquettes
```

### Test 2 : Load Balancing

```bash
# Scaler un service
docker-compose up -d --scale enseignant-service=3

# Vérifier dans Eureka
curl http://localhost:8761

# Faire plusieurs requêtes et vérifier la distribution
for i in {1..30}; do
  curl http://localhost:8080/api/enseignants
  sleep 0.1
done

# Vérifier les logs des 3 instances
docker logs enseignant-service-1
docker logs enseignant-service-2
docker logs enseignant-service-3
```

### Test 3 : CORS

```bash
# Requête preflight
curl -X OPTIONS http://localhost:8080/api/enseignants \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v

# Vérifier les headers CORS dans la réponse
```

### Test 4 : Rate Limiting

```bash
# Script pour tester le rate limiting
for i in {1..200}; do
  response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/enseignants)
  echo "Request $i: HTTP $response"
done

# Attendu : 100-150 requêtes avec 200, puis 429
```

### Test 5 : Circuit Breaker

```bash
# Simuler une défaillance
docker stop enseignant-service

# Faire 10 requêtes
for i in {1..10}; do
  curl http://localhost:8080/api/enseignants
  sleep 0.5
done

# Observer : circuit breaker s'ouvre après 5 échecs
# Requêtes suivantes échouent immédiatement (fallback)

# Redémarrer le service
docker start enseignant-service

# Attendre 10 secondes (wait-duration)
sleep 10

# Circuit breaker passe en HALF-OPEN
# Faire 3 requêtes de test
for i in {1..3}; do
  curl http://localhost:8080/api/enseignants
done

# Si succès : circuit breaker se ferme (CLOSED)
```

## Troubleshooting

### Problème : Gateway ne démarre pas

**Solutions :**
1. Vérifier que le port 8080 n'est pas utilisé
2. Vérifier qu'Eureka est démarré
3. Vérifier les logs : `docker logs api-gateway`

### Problème : Routes ne fonctionnent pas

**Solutions :**
1. Vérifier que le service est enregistré dans Eureka : http://localhost:8761
2. Vérifier les routes actives : `curl http://localhost:8080/actuator/gateway/routes`
3. Vérifier les logs du Gateway

### Problème : Rate limiting ne fonctionne pas

**Solutions :**
1. Vérifier que Redis est démarré : `docker ps | grep redis`
2. Tester Redis : `docker exec -it daos-redis redis-cli ping`
3. Vérifier la connexion : logs du Gateway

### Problème : Circuit breaker ne s'active pas

**Solutions :**
1. Vérifier la configuration Resilience4j
2. Faire au moins 5 requêtes (minimum-number-of-calls)
3. Vérifier l'état : `curl http://localhost:8080/actuator/circuitbreakers`

### Problème : CORS bloqué

**Solutions :**
1. Vérifier la configuration CORS dans application.properties
2. Vérifier les logs : `logging.level.org.springframework.web.cors=DEBUG`
3. Tester avec `curl -X OPTIONS`

## Bonnes pratiques

1. **Sécurité**
   - ✅ Ajouter Spring Security pour authentification
   - ✅ Limiter les origines CORS en production
   - ✅ Utiliser HTTPS
   - ✅ Rate limiting par utilisateur authentifié

2. **Performance**
   - ✅ Configurer le timeout approprié
   - ✅ Optimiser le cache Redis
   - ✅ Monitorer les métriques

3. **Résilience**
   - ✅ Configurer des fallbacks pour tous les services
   - ✅ Ajuster les seuils du circuit breaker selon le service
   - ✅ Implémenter des retry policies

4. **Monitoring**
   - ✅ Activer tous les endpoints Actuator
   - ✅ Exporter les métriques vers Prometheus
   - ✅ Configurer des alertes

## Résumé

L'API Gateway DAOS fournit :

- ✅ **Point d'entrée unique** sur le port 8080
- ✅ **Routage dynamique** basé sur Eureka (9 routes configurées)
- ✅ **Load balancing** automatique (round-robin)
- ✅ **Filtres de logging** pour tracer toutes les requêtes
- ✅ **CORS** configuré globalement
- ✅ **Rate limiting** (100 req/min par IP via Redis)
- ✅ **Circuit breaker** (Resilience4j avec fallback)
- ✅ **Conteneurisé** avec Docker
- ✅ **Swagger agrégé** pour tous les services
- ✅ **Monitoring** complet via Actuator

---

**Date de création :** 2025-12-24
**Version :** 1.0
**Auteur :** Équipe DAOS - UASZ
