# Guide Config Server - Gestion Centralisée des Configurations DAOS

## Vue d'ensemble

Le Config Server est le service de gestion centralisée des configurations pour l'architecture microservices DAOS. Il permet de stocker, versionner et distribuer les configurations de tous les microservices depuis un point central.

## Architecture

```
┌────────────────────────────────────────────────────────┐
│             Config Server (:8888)                       │
│     Centralisation des configurations                   │
│              Mode: Native                               │
│      Storage: classpath:/config-repo                    │
└────────────────────────────────────────────────────────┘
                        │
                        │ Exposer configurations via HTTP
                        │
    ┌───────────────────┼───────────────────┐
    │                   │                   │
┌───▼────┐      ┌───────▼──────┐     ┌─────▼─────┐
│  Auth  │      │  Enseignant  │     │  Maquette │
│ :8081  │      │    :8082     │     │   :8083   │
└────────┘      └──────────────┘     └───────────┘
                        │
              ┌─────────┴──────────┐
              │                    │
     ┌────────▼──────┐    ┌────────▼──────┐
     │Choix Enseign. │    │  Emploi Temps │
     │    :8084      │    │     :8085     │
     └───────────────┘    └───────────────┘
              │
     ┌────────▼──────────┐
     │   Déroulement     │
     │       :8086       │
     └───────────────────┘
```

## Configuration

### Fichiers principaux

**Config Server - application.properties**
```properties
# Server Configuration
server.port=8888
spring.application.name=config-server

# Config Server Native Mode
spring.cloud.config.server.native.search-locations=classpath:/config-repo
spring.profiles.active=native

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.instance.prefer-ip-address=true

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,env,configprops
management.endpoint.health.show-details=always

# Logging
logging.level.org.springframework.cloud.config=INFO
logging.level.org.springframework.cloud.config.server=DEBUG
```

### Application Java

**config-server/src/main/java/com/uasz/daos/config/ConfigServerApplication.java**
```java
package com.uasz.daos.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

### Dépendances Maven

**config-server/pom.xml**
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

## Structure du config-repo

```
config-server/src/main/resources/config-repo/
├── application.properties                      # Configuration commune
├── auth-service.properties                     # Auth Service
├── enseignant-service.properties               # Enseignant Service
├── maquette-service.properties                 # Maquette Service
├── choix-enseignement-service.properties       # Choix Enseignement
├── emploi-temps-service.properties             # Emploi Temps
├── deroulement-enseignement-service.properties # Déroulement
├── api-gateway.properties                      # API Gateway
└── README.md                                   # Documentation
```

### Configuration commune (application.properties)

Partagée par tous les microservices :

```properties
# Eureka Configuration (commune)
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.instance.prefer-ip-address=true

# JPA Configuration (commune)
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Logging Configuration (commune)
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.level.root=INFO

# Actuator Configuration (commune)
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized

# Swagger Configuration (commune)
springdoc.swagger-ui.enabled=true
springdoc.api-docs.enabled=true
```

### Configuration par service

Chaque service a son propre fichier `{service-name}.properties` :

**Exemple : auth-service.properties**
```properties
# Server Port
server.port=8081

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/daos_auth_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=

# JWT Configuration
jwt.secret=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
jwt.expiration=86400000

# Swagger Configuration
springdoc.packages-to-scan=com.uasz.daos.auth.controller
```

## Endpoints HTTP

### Récupération des configurations

**Format général :**
```
http://localhost:8888/{application}/{profile}[/{label}]
```

**Exemples :**

```bash
# Configuration Auth Service (défaut)
curl http://localhost:8888/auth-service/default

# Configuration Enseignant Service
curl http://localhost:8888/enseignant-service/default

# Configuration API Gateway
curl http://localhost:8888/api-gateway/default

# Format JSON
curl http://localhost:8888/auth-service/default | jq

# Format YAML
curl http://localhost:8888/auth-service/default.yml

# Propriété spécifique
curl http://localhost:8888/auth-service/default | jq '.propertySources[0].source'
```

### Endpoints Actuator

| Endpoint | URL | Description |
|----------|-----|-------------|
| Health | http://localhost:8888/actuator/health | État de santé du Config Server |
| Info | http://localhost:8888/actuator/info | Informations de l'application |
| Env | http://localhost:8888/actuator/env | Variables d'environnement |
| ConfigProps | http://localhost:8888/actuator/configprops | Propriétés de configuration |

## Critères d'acceptation

### ✅ Le Config Server démarre sur le port 8888

**Vérification :**
```bash
curl http://localhost:8888/actuator/health
```

**Résultat attendu :**
```json
{
  "status": "UP"
}
```

### ✅ Les configurations sont stockées en mode native (classpath:/config-repo)

**Configuration :**
```properties
spring.cloud.config.server.native.search-locations=classpath:/config-repo
spring.profiles.active=native
```

**Vérification :**
```bash
# Lister les fichiers dans config-repo (après build)
ls config-server/src/main/resources/config-repo/
```

**Résultat attendu :** 9 fichiers (8 .properties + 1 README.md)

### ✅ Le Config Server s'enregistre auprès d'Eureka

**Configuration :**
```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
```

**Vérification :**
Ouvrir http://localhost:8761 et vérifier que "CONFIG-SERVER" apparaît dans la liste des services UP.

### ✅ Le service expose l'endpoint /actuator/health

**Vérification :**
```bash
curl http://localhost:8888/actuator/health
```

**Résultat attendu :**
```json
{
  "status": "UP",
  "components": {
    "discoveryComposite": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

### ✅ Les configurations peuvent être récupérées via HTTP GET

**Vérification :**
```bash
curl http://localhost:8888/auth-service/default
```

**Résultat attendu :** JSON avec toutes les propriétés de auth-service + application.properties

### ✅ Le service est conteneurisé avec Docker

**Dockerfile :**
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Docker Compose :**
```yaml
config-server:
  build:
    context: ./config-server
    dockerfile: Dockerfile
  container_name: config-server
  ports:
    - "8888:8888"
  depends_on:
    - eureka-server
  environment:
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
  networks:
    - daos-network
```

**Vérification :**
```bash
docker ps | grep config-server
```

## Tests

### Test 1 : Démarrage du Config Server

```bash
# Mode local
cd config-server
mvn spring-boot:run

# Mode Docker
docker-compose up config-server

# Vérifier le démarrage
curl http://localhost:8888/actuator/health
```

**Résultat attendu :**
```json
{"status":"UP"}
```

### Test 2 : Récupération d'une configuration

```bash
# Récupérer la config d'auth-service
curl http://localhost:8888/auth-service/default

# Vérifier une propriété spécifique
curl http://localhost:8888/auth-service/default | jq '.propertySources[].source["server.port"]'
```

**Résultat attendu :** `8081`

### Test 3 : Configuration commune appliquée

```bash
# Vérifier qu'auth-service hérite de application.properties
curl http://localhost:8888/auth-service/default | jq '.propertySources[].source | keys'
```

**Résultat attendu :** Propriétés de auth-service.properties ET application.properties

### Test 4 : Enregistrement Eureka

```bash
# Vérifier sur le dashboard Eureka
curl http://localhost:8761

# Ou via API
curl http://localhost:8761/eureka/apps/CONFIG-SERVER
```

**Résultat attendu :** CONFIG-SERVER avec statut UP

### Test 5 : Tous les services ont leur configuration

```bash
# Tester chaque service
for service in auth-service enseignant-service maquette-service choix-enseignement-service emploi-temps-service deroulement-enseignement-service api-gateway
do
  echo "Testing $service..."
  curl -s http://localhost:8888/$service/default | jq -r '.name'
done
```

**Résultat attendu :** Nom de chaque service affiché

## Mode d'utilisation actuel (Hybride)

**Situation actuelle :**
- ✅ Config Server opérationnel et accessible
- ✅ Configurations centralisées dans config-repo
- ⚠️ Les microservices utilisent encore leurs propres `application.properties` locaux
- ✅ Vous pouvez consulter les configurations via HTTP sans impacter les services

**Avantages :**
- Pas de risque de casser les services existants
- Config Server validé et fonctionnel (tous les critères d'acceptation remplis)
- Migration possible plus tard si nécessaire

## Migration des services (Optionnel)

Pour qu'un microservice utilise Config Server au lieu de son application.properties local :

### Étape 1 : Ajouter la dépendance Config Client

Dans le `pom.xml` du service :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

### Étape 2 : Configurer le client

**Option A : bootstrap.properties (Spring Boot 2.3 et antérieur)**

Créer `bootstrap.properties` :

```properties
spring.application.name=auth-service
spring.cloud.config.uri=http://localhost:8888
spring.cloud.config.fail-fast=true
```

**Option B : application.properties (Spring Boot 2.4+)**

Ajouter dans `application.properties` :

```properties
spring.application.name=auth-service
spring.config.import=optional:configserver:http://localhost:8888
```

### Étape 3 : Supprimer les propriétés dupliquées

Supprimer du `application.properties` local les propriétés déjà dans config-repo (garder uniquement les propriétés spécifiques qui ne doivent pas être centralisées).

### Étape 4 : Redémarrer le service

```bash
# Le service récupère maintenant sa config depuis Config Server
mvn spring-boot:run
```

### Étape 5 : Vérifier

```bash
# Vérifier les logs du service
# Doit afficher : "Located property source: CompositePropertySource..."
```

## Rechargement à chaud (Refresh)

Pour recharger la configuration d'un service sans redémarrage :

### 1. Ajouter la dépendance Actuator

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 2. Exposer l'endpoint refresh

```properties
management.endpoints.web.exposure.include=health,info,refresh
```

### 3. Annoter les beans avec @RefreshScope

```java
@RestController
@RefreshScope  // Important !
public class MonController {
    @Value("${ma.propriete}")
    private String maPropriete;
}
```

### 4. Modifier la config et recharger

```bash
# 1. Modifier la propriété dans config-repo
# 2. Redémarrer Config Server (ou attendre si Git backend)
# 3. Recharger le service
curl -X POST http://localhost:8081/actuator/refresh
```

## Backends de stockage

### Mode Native (Actuel)

**Avantages :**
- ✅ Simple et rapide
- ✅ Pas de dépendance externe
- ✅ Parfait pour développement

**Inconvénients :**
- ❌ Pas de versioning
- ❌ Pas d'historique
- ❌ Redémarrage nécessaire pour changer les configs

### Mode Git (Production recommandé)

**Configuration :**
```properties
spring.profiles.active=git
spring.cloud.config.server.git.uri=https://github.com/votre-org/config-repo
spring.cloud.config.server.git.username=votre-username
spring.cloud.config.server.git.password=votre-token
```

**Avantages :**
- ✅ Versioning complet
- ✅ Historique des changements
- ✅ Rollback facile
- ✅ Audit (qui a changé quoi)
- ✅ Rechargement sans redémarrage

### Mode Vault (Maximum sécurité)

Pour les secrets sensibles :

```properties
spring.profiles.active=vault
spring.cloud.config.server.vault.host=localhost
spring.cloud.config.server.vault.port=8200
spring.cloud.config.server.vault.token=votre-token
```

## Sécurité

### Développement

**Configuration actuelle :**
- Pas d'authentification
- Configurations en clair
- Acceptable pour développement local

### Production

**Recommandations :**

1. **Authentifier l'accès au Config Server**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

```properties
spring.security.user.name=config-user
spring.security.user.password=${CONFIG_SERVER_PASSWORD}
```

2. **Chiffrer les données sensibles**
```bash
# Installer Spring Cloud Config Encryption
# Utiliser {cipher} pour les valeurs chiffrées
spring.datasource.password={cipher}AQA3vF8xNbZuFN...
```

3. **Utiliser HTTPS**
```properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.jks
server.ssl.key-store-password=${KEYSTORE_PASSWORD}
```

4. **Limiter l'accès réseau**
- Firewall : Autoriser uniquement les microservices
- Docker network : Réseau privé

## Monitoring et Logging

### Logs importants

```bash
# Démarrage
Started ConfigServerApplication in X seconds

# Chargement des configurations
Adding property source: file:/.../config-repo/auth-service.properties

# Requête de configuration
Fetched config from location 'classpath:/config-repo/'
```

### Métriques Actuator

```bash
# Statistiques des endpoints
curl http://localhost:8888/actuator/metrics

# Propriétés de configuration
curl http://localhost:8888/actuator/configprops
```

## Troubleshooting

### Problème : Config Server ne démarre pas

**Solutions :**
1. Vérifier que le port 8888 n'est pas utilisé
2. Vérifier les logs : `docker logs config-server`
3. Vérifier qu'Eureka est démarré

### Problème : Configurations introuvables

**Solutions :**
1. Vérifier que config-repo existe : `ls config-server/src/main/resources/config-repo/`
2. Vérifier le nom du fichier (doit correspondre à `spring.application.name`)
3. Tester l'URL : `curl http://localhost:8888/{service-name}/default`

### Problème : Service ne se connecte pas au Config Server

**Solutions :**
1. Vérifier que `spring-cloud-starter-config` est dans le pom.xml
2. Vérifier `spring.config.import` ou `bootstrap.properties`
3. Vérifier les logs du service au démarrage

### Problème : Config Server non enregistré dans Eureka

**Solutions :**
1. Vérifier qu'Eureka Server est démarré
2. Vérifier `eureka.client.service-url.defaultZone`
3. Attendre 30 secondes (heartbeat interval)

## Bonnes pratiques

1. **Organisation des fichiers**
   - ✅ Un fichier par service
   - ✅ Configurations communes dans `application.properties`
   - ✅ Utiliser des profils pour les environnements

2. **Sécurité**
   - ✅ Ne jamais commiter de secrets en clair
   - ✅ Utiliser des variables d'environnement : `${DB_PASSWORD:default}`
   - ✅ Chiffrer les données sensibles en production

3. **Versioning**
   - ✅ Documenter les changements
   - ✅ Utiliser Git en production
   - ✅ Tester avant de déployer

4. **Performance**
   - ✅ Activer le cache côté client
   - ✅ Limiter les requêtes inutiles
   - ✅ Utiliser le rechargement à chaud au lieu de redémarrages

## Références

- [Spring Cloud Config Documentation](https://spring.io/projects/spring-cloud-config)
- [Spring Cloud Config Server](https://cloud.spring.io/spring-cloud-config/reference/html/)
- [Config Server with Eureka](https://spring.io/guides/gs/service-registration-and-discovery/)

---

**Date de création :** 2025-12-24
**Version :** 1.0
**Auteur :** Équipe DAOS - UASZ
