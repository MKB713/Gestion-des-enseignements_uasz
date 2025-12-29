# Guide Eureka Server - Service Discovery DAOS

## Vue d'ensemble

Eureka Server est le service de découverte centralisé pour l'architecture microservices DAOS. Il permet aux microservices de se localiser dynamiquement sans couplage fort.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Eureka Server (8761)                     │
│                   Service Registry Dashboard                 │
└─────────────────────────────────────────────────────────────┘
                            ▲
                            │ Enregistrement automatique
                            │
    ┌───────────────────────┼───────────────────────┐
    │                       │                       │
┌───▼────┐         ┌────────▼──────┐        ┌──────▼─────┐
│  Auth  │         │  Enseignant   │        │  Maquette  │
│Service │         │    Service    │        │  Service   │
│ :8081  │         │     :8082     │        │   :8083    │
└────────┘         └───────────────┘        └────────────┘
                            │
                  ┌─────────┴──────────┐
                  │                    │
         ┌────────▼──────┐    ┌────────▼──────┐
         │Choix Enseign. │    │  Emploi Temps │
         │    :8084      │    │     :8085     │
         └───────────────┘    └───────────────┘
                  │
         ┌────────▼──────────┐
         │   Deroulement     │
         │   Enseignement    │
         │      :8086        │
         └───────────────────┘
```

## Configuration

### 1. Eureka Server

**Fichier:** `eureka-server/src/main/resources/application.properties`

```properties
# Application Name
spring.application.name=eureka-server

# Server Port (default for standalone mode)
server.port=8761

# Standalone Mode Configuration (Development)
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# Server Configuration
eureka.server.enable-self-preservation=false
eureka.server.eviction-interval-timer-in-ms=10000

# Dashboard Configuration
eureka.dashboard.enabled=true

# Instance Configuration
eureka.instance.hostname=localhost
eureka.instance.prefer-ip-address=false

# Logging
logging.level.com.netflix.eureka=INFO
logging.level.com.netflix.discovery=INFO

# Actuator Configuration (for health checks)
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

### 2. Clients Eureka (Microservices)

Tous les microservices (auth, enseignant, maquette, etc.) utilisent cette configuration :

```properties
# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.instance.prefer-ip-address=true
```

### 3. API Gateway

Le Gateway utilise Eureka pour la découverte des services et le load balancing :

```properties
# Gateway Discovery
spring.cloud.gateway.discovery.locator.enabled=true
spring.cloud.gateway.discovery.locator.lower-case-service-id=true

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
```

Les routes utilisent le préfixe `lb://` (load balanced) pour découvrir les services :

```properties
spring.cloud.gateway.routes[4].uri=lb://enseignant-service
spring.cloud.gateway.routes[5].uri=lb://maquette-service
# etc...
```

## Dépendances Maven

### Eureka Server

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

### Eureka Client (dans les microservices)

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

## Annotations Java

### Eureka Server Application

```java
package com.uasz.daos.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

### Clients Eureka (optionnel avec Spring Boot 3)

Avec Spring Boot 3, l'annotation `@EnableDiscoveryClient` est automatiquement activée si la dépendance Eureka Client est présente. Vous pouvez l'ajouter explicitement si vous le souhaitez :

```java
@SpringBootApplication
@EnableDiscoveryClient  // Optionnel avec Spring Boot 3
public class AuthServiceApplication {
    // ...
}
```

## Docker Configuration

### Eureka Server Dockerfile

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
eureka-server:
  build:
    context: ./eureka-server
    dockerfile: Dockerfile
  container_name: eureka-server
  ports:
    - "8761:8761"
  networks:
    - daos-network
  healthcheck:
    test: ["CMD", "curl", "-f", "http://localhost:8761/actuator/health"]
    interval: 30s
    timeout: 10s
    retries: 5
```

### Clients Eureka dans Docker Compose

```yaml
auth-service:
  build:
    context: ./auth-service
    dockerfile: Dockerfile
  container_name: auth-service
  ports:
    - "8081:8081"
  depends_on:
    - mysql
    - eureka-server
  environment:
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
  networks:
    - daos-network
```

## Fonctionnalités

### 1. Enregistrement automatique

Les microservices s'enregistrent automatiquement au démarrage :

- Envoient un heartbeat toutes les 30 secondes
- Eureka les marque comme DOWN si 3 heartbeats consécutifs échouent
- Les instances sont supprimées après 90 secondes d'inactivité

### 2. Dashboard Web

Accessible via : `http://localhost:8761`

Le dashboard affiche :
- Liste des instances enregistrées
- Statut de chaque instance (UP, DOWN)
- Métadonnées des instances (hostname, port, zone)
- État de la réplication (peer awareness)
- Statistiques globales

### 3. Service Discovery

Les services peuvent découvrir d'autres services via :

**OpenFeign (Communication inter-services) :**

```java
@FeignClient(name = "auth-service")
public interface AuthServiceClient {
    @GetMapping("/api/users/{id}")
    UserDTO getUser(@PathVariable Long id);
}
```

**API Gateway (Routing dynamique) :**

```properties
spring.cloud.gateway.routes[0].uri=lb://auth-service
```

### 4. Load Balancing

Le préfixe `lb://` active le load balancing côté client :

- Round-robin par défaut
- Répartition automatique entre instances multiples
- Exclusion automatique des instances DOWN

## Endpoints Actuator

| Endpoint | URL | Description |
|----------|-----|-------------|
| Health | `http://localhost:8761/actuator/health` | État de santé du serveur |
| Info | `http://localhost:8761/actuator/info` | Informations de l'application |

## Critères d'acceptation

### ✅ Eureka Server démarre sur le port 8761

**Vérification :**
```bash
curl http://localhost:8761/actuator/health
```

**Résultat attendu :**
```json
{
  "status": "UP"
}
```

### ✅ Le dashboard Eureka est accessible via http://localhost:8761

**Vérification :**
Ouvrir dans le navigateur : `http://localhost:8761`

**Résultat attendu :**
- Page web du dashboard Eureka
- Titre : "Eureka"
- Informations système affichées

### ✅ Les microservices peuvent s'enregistrer automatiquement

**Vérification :**
```bash
# Démarrer les services
docker-compose up -d

# Attendre 30 secondes pour l'enregistrement

# Vérifier les instances enregistrées sur le dashboard
http://localhost:8761
```

**Résultat attendu :**
Le dashboard affiche les services suivants avec statut UP :
- API-GATEWAY
- AUTH-SERVICE
- ENSEIGNANT-SERVICE
- MAQUETTE-SERVICE
- CHOIX-ENSEIGNEMENT-SERVICE
- EMPLOI-TEMPS-SERVICE
- DEROULEMENT-ENSEIGNEMENT-SERVICE
- CONFIG-SERVER

### ✅ Le service affiche les instances enregistrées et leur statut

**Vérification :**
Sur le dashboard `http://localhost:8761`, section "Instances currently registered with Eureka"

**Résultat attendu :**
```
Application             AMIs        Availability Zones  Status
API-GATEWAY             n/a (1)     (1)                 UP (1) - api-gateway:8080
AUTH-SERVICE            n/a (1)     (1)                 UP (1) - auth-service:8081
ENSEIGNANT-SERVICE      n/a (1)     (1)                 UP (1) - enseignant-service:8082
...
```

### ⚠️ La haute disponibilité (peer awareness)

**État actuel :** Configuration standalone (1 instance)

**Raison :** Suffisant pour l'environnement de développement

**Pour la production :** Une configuration avec 2+ instances Eureka est recommandée. Voir section "Haute Disponibilité" ci-dessous.

### ✅ Le service est conteneurisé avec Docker

**Vérification :**
```bash
docker ps | grep eureka-server
```

**Résultat attendu :**
```
eureka-server   Up X minutes   0.0.0.0:8761->8761/tcp
```

## Tests

### Test 1 : Démarrage du serveur Eureka

```bash
# Mode local (sans Docker)
cd eureka-server
mvn spring-boot:run

# Vérifier le démarrage
curl http://localhost:8761/actuator/health

# Mode Docker
docker-compose up eureka-server

# Vérifier dans les logs
docker logs eureka-server
```

**Log attendu :**
```
Started EurekaServerApplication in X seconds
```

### Test 2 : Enregistrement d'un microservice

```bash
# Démarrer Eureka
docker-compose up -d eureka-server

# Démarrer un microservice
docker-compose up -d auth-service

# Vérifier les logs du service
docker logs auth-service

# Vérifier sur le dashboard
http://localhost:8761
```

**Log attendu dans auth-service :**
```
DiscoveryClient_AUTH-SERVICE - registration status: 204
```

### Test 3 : Communication inter-services via Eureka

```bash
# Démarrer tous les services
docker-compose up -d

# Tester une route du Gateway
curl http://localhost:8080/api/auth/health

# Le Gateway doit découvrir auth-service via Eureka
# et router la requête automatiquement
```

### Test 4 : Résistance aux pannes

```bash
# Arrêter un service
docker stop auth-service

# Attendre 90 secondes

# Vérifier sur le dashboard
# auth-service doit être marqué comme DOWN ou supprimé

# Redémarrer le service
docker start auth-service

# Attendre 30 secondes

# Vérifier sur le dashboard
# auth-service doit être marqué comme UP
```

## Haute Disponibilité (Production)

Pour la production, configurez 2+ instances Eureka avec peer awareness.

### Configuration pour 2 instances

**Option 1 : Profils Spring (application-peer1.properties, application-peer2.properties)**

**Option 2 : Variables d'environnement Docker Compose (Recommandé)**

```yaml
eureka-server-1:
  build: ./eureka-server
  container_name: eureka-server-1
  ports:
    - "8761:8761"
  environment:
    EUREKA_INSTANCE_HOSTNAME: eureka-server-1
    EUREKA_CLIENT_REGISTER_WITH_EUREKA: true
    EUREKA_CLIENT_FETCH_REGISTRY: true
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server-2:8762/eureka/
  networks:
    - daos-network

eureka-server-2:
  build: ./eureka-server
  container_name: eureka-server-2
  ports:
    - "8762:8762"
  environment:
    SERVER_PORT: 8762
    EUREKA_INSTANCE_HOSTNAME: eureka-server-2
    EUREKA_CLIENT_REGISTER_WITH_EUREKA: true
    EUREKA_CLIENT_FETCH_REGISTRY: true
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server-1:8761/eureka/
  networks:
    - daos-network
```

**Mise à jour des clients :**

```yaml
auth-service:
  environment:
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server-1:8761/eureka/,http://eureka-server-2:8762/eureka/
```

## Troubleshooting

### Problème : Le dashboard Eureka ne s'affiche pas

**Solutions :**
1. Vérifier que le port 8761 n'est pas utilisé : `netstat -an | grep 8761`
2. Vérifier les logs : `docker logs eureka-server`
3. Vérifier le healthcheck : `curl http://localhost:8761/actuator/health`

### Problème : Les microservices ne s'enregistrent pas

**Solutions :**
1. Vérifier la configuration Eureka dans `application.properties`
2. Vérifier que `eureka.client.register-with-eureka=true`
3. Vérifier la connectivité réseau : `docker network inspect daos-network`
4. Vérifier les logs du microservice : `docker logs <service-name>`
5. Vérifier que l'URL Eureka est correcte (localhost en local, nom du conteneur dans Docker)

### Problème : Instances marquées comme DOWN alors qu'elles fonctionnent

**Solutions :**
1. Désactiver self-preservation mode : `eureka.server.enable-self-preservation=false`
2. Augmenter le timeout de heartbeat
3. Vérifier la configuration réseau Docker

### Problème : Le healthcheck échoue dans Docker

**Solutions :**
1. Vérifier que curl est installé dans l'image : `RUN apk add --no-cache curl`
2. Vérifier que Spring Boot Actuator est dans les dépendances
3. Tester manuellement : `docker exec eureka-server curl http://localhost:8761/actuator/health`

## Bonnes pratiques

### 1. Configuration réseau

- **Environnement local :** Utiliser `localhost`
- **Environnement Docker :** Utiliser les noms de conteneurs
- Toujours utiliser le réseau bridge personnalisé (`daos-network`)

### 2. Timeouts et intervals

```properties
# Temps entre chaque heartbeat (défaut: 30s)
eureka.instance.lease-renewal-interval-in-seconds=30

# Temps avant qu'une instance soit marquée DOWN (défaut: 90s)
eureka.instance.lease-expiration-duration-in-seconds=90

# Intervalle d'éviction des instances DOWN (défaut: 60s)
eureka.server.eviction-interval-timer-in-ms=60000
```

### 3. Sécurité

Pour la production :
- Activer Spring Security sur Eureka Server
- Utiliser HTTPS
- Limiter l'accès au dashboard
- Authentifier les requêtes d'enregistrement

### 4. Monitoring

- Activer Spring Boot Actuator
- Exposer les métriques Prometheus
- Configurer des alertes sur le statut des services

## Références

- [Spring Cloud Netflix Eureka](https://spring.io/projects/spring-cloud-netflix)
- [Netflix Eureka Wiki](https://github.com/Netflix/eureka/wiki)
- [Spring Cloud Gateway + Eureka](https://spring.io/guides/gs/gateway/)
- [Docker Compose Networking](https://docs.docker.com/compose/networking/)

## Contact et Support

Pour toute question ou problème :
- Consulter les logs : `docker logs eureka-server`
- Vérifier le dashboard : `http://localhost:8761`
- Consulter cette documentation

---

**Date de création :** 2025-12-24
**Version :** 1.0
**Auteur :** Équipe DAOS - UASZ
