# Guide de Migration vers Config Server

## Vue d'ensemble

Ce guide documente la migration complète de tous les microservices métier vers l'utilisation de Spring Cloud Config Server pour la gestion centralisée de la configuration.

## Objectifs de la Migration

✅ Centraliser la configuration de tous les microservices
✅ Supporter plusieurs profils d'environnement (dev, test, prod)
✅ Permettre le rafraîchissement dynamique de la configuration sans redémarrage
✅ Améliorer la sécurité en externalisant les configurations sensibles
✅ Faciliter la gestion des configurations multi-environnements

## Architecture Avant/Après

### Avant la Migration
```
Microservice
├── application.properties (configuration locale)
└── Redémarrage requis pour tout changement
```

### Après la Migration
```
Config Server (port 8888)
├── config-repo/
│   ├── application.yml (configuration commune)
│   ├── auth-service.yml (config par défaut)
│   ├── auth-service-dev.yml (config développement)
│   ├── auth-service-prod.yml (config production)
│   └── ... (autres services)

Microservice
├── bootstrap.properties (connexion Config Server)
├── @RefreshScope (beans rafraîchissables)
└── Rafraîchissement dynamique via /actuator/refresh
```

## Changements Effectués

### 1. Structure de Configuration Centralisée

#### Fichiers YAML créés dans `config-server/src/main/resources/config-repo/`

**Configuration commune** (`application.yml`):
- Configuration Eureka partagée
- Configuration JPA commune (dialect MySQL)
- Configuration des logs
- Endpoints Actuator

**Par microservice** (exemple: `auth-service`):
- `auth-service.yml` - Configuration par défaut
- `auth-service-dev.yml` - Profil développement (show-sql=true, logs DEBUG)
- `auth-service-prod.yml` - Profil production (variables d'environnement, sécurité renforcée)

**Services configurés**:
1. auth-service (port 8081)
2. enseignant-service (port 8082)
3. maquette-service (port 8083)
4. choix-enseignement-service (port 8084)
5. emploi-temps-service (port 8085)
6. deroulement-enseignement-service (port 8086)

### 2. Dépendances Ajoutées

Ajout de `spring-cloud-starter-config` à tous les microservices dans `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

### 3. Fichiers Bootstrap

Création de `bootstrap.properties` pour chaque microservice:

```properties
spring.application.name=<nom-du-service>
spring.cloud.config.uri=http://localhost:8888
spring.cloud.config.fail-fast=true
spring.profiles.active=dev
```

**Rôle**: Le fichier bootstrap est chargé AVANT application.properties et permet au microservice de se connecter au Config Server au démarrage.

### 4. Classes @RefreshScope

Création de classes de configuration avec `@RefreshScope` pour permettre le rafraîchissement dynamique:

#### auth-service

**JwtConfigProperties.java**:
```java
@Component
@RefreshScope
@ConfigurationProperties(prefix = "jwt")
public class JwtConfigProperties {
    private String secret;
    private Long expiration;
    // getters/setters
}
```

**ApplicationConfigProperties.java**:
```java
@Component
@RefreshScope
public class ApplicationConfigProperties {
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private Integer serverPort;

    @Value("${spring.jpa.show-sql}")
    private Boolean showSql;
    // getters
}
```

#### Autres services (enseignant, maquette, choix, emploi-temps, deroulement)

Chaque service possède une classe `ApplicationConfigProperties` similaire avec @RefreshScope.

## Utilisation

### 1. Démarrage de l'Infrastructure

```bash
# 1. Démarrer Config Server
cd config-server
mvn spring-boot:run

# Vérifier que Config Server est prêt
curl http://localhost:8888/actuator/health

# 2. Démarrer Eureka Server
cd ../eureka-server
mvn spring-boot:run

# 3. Démarrer les microservices avec le profil désiré
cd ../auth-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 2. Vérification de la Configuration

#### Vérifier la configuration chargée par un service

```bash
# Configuration du service auth en profil dev
curl http://localhost:8888/auth-service/dev

# Configuration du service enseignant en profil prod
curl http://localhost:8888/enseignant-service/prod

# Configuration par défaut (sans profil)
curl http://localhost:8888/maquette-service/default
```

#### Format de réponse
```json
{
  "name": "auth-service",
  "profiles": ["dev"],
  "propertySources": [
    {
      "name": "classpath:/config-repo/auth-service-dev.yml",
      "source": {
        "spring.jpa.show-sql": true,
        "logging.level.com.uasz.daos.auth": "DEBUG"
      }
    },
    {
      "name": "classpath:/config-repo/auth-service.yml",
      "source": {
        "server.port": 8081,
        "jwt.secret": "...",
        "jwt.expiration": 86400000
      }
    }
  ]
}
```

### 3. Rafraîchissement Dynamique de la Configuration

#### Étapes pour rafraîchir la configuration sans redémarrage:

**1. Modifier la configuration dans config-repo**
```bash
# Éditer le fichier YAML
vi config-server/src/main/resources/config-repo/auth-service-dev.yml

# Exemple: changer logging.level.com.uasz.daos.auth: INFO
```

**2. Rafraîchir le service**
```bash
# Appeler l'endpoint /actuator/refresh du microservice
curl -X POST http://localhost:8081/actuator/refresh

# Réponse: liste des propriétés rafraîchies
["logging.level.com.uasz.daos.auth"]
```

**3. Vérifier le changement**
Les beans annotés avec `@RefreshScope` sont automatiquement recréés avec les nouvelles valeurs.

### 4. Gestion des Profils

#### Développement (dev)
```bash
# Démarrer avec profil dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Ou définir dans bootstrap.properties
spring.profiles.active=dev
```

Caractéristiques:
- `spring.jpa.show-sql=true` (affiche les requêtes SQL)
- Logs en niveau DEBUG
- Configuration de développement

#### Production (prod)
```bash
# Démarrer avec profil prod et variables d'environnement
export DB_PASSWORD=secret_password
export JWT_SECRET=super_secret_jwt_key
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Caractéristiques:
- Variables d'environnement pour les secrets
- Swagger UI désactivé
- JWT expiration réduite (1h au lieu de 24h)
- Logs en niveau WARN

#### Test (si configuré)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

## Avantages de la Migration

### 1. Centralisation
- ✅ Une seule source de vérité pour toute la configuration
- ✅ Pas de duplication de configuration entre services
- ✅ Facilité de maintenance

### 2. Multi-environnements
- ✅ Profils distincts pour dev/test/prod
- ✅ Surcharge de configuration par profil
- ✅ Variables d'environnement pour les secrets en production

### 3. Rafraîchissement Dynamique
- ✅ Changement de configuration sans redémarrage
- ✅ Beans `@RefreshScope` automatiquement mis à jour
- ✅ Zéro downtime pour les changements de config

### 4. Sécurité
- ✅ Secrets externalisés (JWT, DB passwords)
- ✅ Variables d'environnement en production
- ✅ Pas de secrets dans le code source

### 5. Audit et Versioning
- ✅ Historique Git des changements de configuration
- ✅ Possibilité de rollback
- ✅ Traçabilité des modifications

## Exemples d'Utilisation

### Exemple 1: Changer le niveau de log dynamiquement

**1. Configuration actuelle** (auth-service-dev.yml):
```yaml
logging:
  level:
    com.uasz.daos.auth: DEBUG
```

**2. Modifier la configuration**:
```yaml
logging:
  level:
    com.uasz.daos.auth: INFO
```

**3. Rafraîchir le service**:
```bash
curl -X POST http://localhost:8081/actuator/refresh
```

**Résultat**: Le niveau de log passe immédiatement à INFO sans redémarrage.

### Exemple 2: Changer l'expiration JWT en production

**1. Configuration actuelle** (auth-service-prod.yml):
```yaml
jwt:
  expiration: 3600000  # 1 heure
```

**2. Augmenter à 2 heures**:
```yaml
jwt:
  expiration: 7200000  # 2 heures
```

**3. Rafraîchir**:
```bash
curl -X POST http://localhost:8081/actuator/refresh
```

**Résultat**: Les nouveaux tokens générés auront une durée de 2h.

### Exemple 3: Activer/désactiver show-sql

**Scénario**: Debugger un problème en production

**1. Activer temporairement show-sql** (auth-service-prod.yml):
```yaml
spring:
  jpa:
    show-sql: true
```

**2. Rafraîchir**:
```bash
curl -X POST http://localhost:8081/actuator/refresh
```

**3. Analyser les logs SQL**

**4. Désactiver après debug**:
```yaml
spring:
  jpa:
    show-sql: false
```

**5. Rafraîchir à nouveau**

## Troubleshooting

### Problème: Le service ne démarre pas

**Erreur**: `Could not locate PropertySource: I/O error`

**Solution**: Vérifier que Config Server est démarré et accessible
```bash
curl http://localhost:8888/actuator/health
```

Si Config Server n'est pas disponible:
- Option 1: Démarrer Config Server d'abord
- Option 2: Désactiver fail-fast temporairement dans bootstrap.properties:
```properties
spring.cloud.config.fail-fast=false
```

### Problème: Configuration non rafraîchie

**Symptôme**: Après `POST /actuator/refresh`, la configuration ne change pas

**Solutions**:
1. Vérifier que la classe utilise `@RefreshScope`
2. Vérifier que Actuator est activé dans application.yml:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: refresh,health,info
```
3. Vérifier que la propriété est bien dans un bean `@RefreshScope`

### Problème: Profil non chargé

**Symptôme**: Le service utilise la configuration par défaut au lieu du profil spécifié

**Solutions**:
1. Vérifier `spring.profiles.active` dans bootstrap.properties
2. Vérifier le nom du fichier YAML (doit être `<service-name>-<profile>.yml`)
3. Vérifier les logs au démarrage:
```
Fetching config from server at: http://localhost:8888
Located environment: name=auth-service, profiles=[dev]
```

### Problème: Variables d'environnement non résolues en production

**Symptôme**: `${DB_PASSWORD}` apparaît littéralement au lieu de la valeur

**Solution**: S'assurer que la variable est définie avant le démarrage:
```bash
export DB_PASSWORD=secret
export JWT_SECRET=super_secret
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Tests de Validation

### Test 1: Vérifier la connexion au Config Server
```bash
# Le service doit afficher au démarrage:
# "Fetching config from server at : http://localhost:8888"
# "Located environment: name=auth-service, profiles=[dev]"
```

### Test 2: Vérifier les profils
```bash
# Profil dev
curl http://localhost:8888/auth-service/dev | jq '.propertySources[0].name'
# Devrait afficher: "classpath:/config-repo/auth-service-dev.yml"

# Profil prod
curl http://localhost:8888/auth-service/prod | jq '.propertySources[0].name'
# Devrait afficher: "classpath:/config-repo/auth-service-prod.yml"
```

### Test 3: Tester le rafraîchissement dynamique
```bash
# 1. Lire la valeur actuelle d'une propriété
curl http://localhost:8081/actuator/env | jq '.propertySources[] | select(.name | contains("configServer")) | .properties."logging.level.com.uasz.daos.auth"'

# 2. Modifier auth-service-dev.yml
# 3. Rafraîchir
curl -X POST http://localhost:8081/actuator/refresh

# 4. Vérifier le changement
curl http://localhost:8081/actuator/env | jq '.propertySources[] | select(.name | contains("configServer")) | .properties."logging.level.com.uasz.daos.auth"'
```

### Test 4: Tester @RefreshScope
```bash
# Créer un endpoint de test qui utilise ApplicationConfigProperties
# GET /config/info retourne applicationName, serverPort, showSql

# 1. Appeler l'endpoint
curl http://localhost:8081/config/info

# 2. Modifier une valeur dans config-repo
# 3. Rafraîchir
curl -X POST http://localhost:8081/actuator/refresh

# 4. Rappeler l'endpoint - devrait afficher la nouvelle valeur
curl http://localhost:8081/config/info
```

## Endpoints Utiles

### Config Server (port 8888)

| Endpoint | Description |
|----------|-------------|
| `GET /{application}/{profile}` | Configuration pour un service et profil |
| `GET /{application}/default` | Configuration par défaut |
| `GET /actuator/health` | Health check |
| `GET /actuator/env` | Variables d'environnement |

### Microservices

| Endpoint | Description |
|----------|-------------|
| `POST /actuator/refresh` | Rafraîchir la configuration |
| `GET /actuator/env` | Voir toutes les propriétés |
| `GET /actuator/health` | Health check |
| `GET /actuator/info` | Informations sur le service |

## Bonnes Pratiques

### 1. Gestion des Secrets
❌ **À éviter**:
```yaml
jwt:
  secret: "hardcoded_secret_in_file"
```

✅ **Recommandé**:
```yaml
jwt:
  secret: ${JWT_SECRET}
```

### 2. Profils
- Utiliser `dev` pour le développement local
- Utiliser `test` pour les tests automatisés
- Utiliser `prod` pour la production
- Ne jamais committer de secrets dans les fichiers YAML

### 3. Rafraîchissement
- Utiliser `@RefreshScope` uniquement sur les beans qui ont vraiment besoin d'être rafraîchis
- Éviter sur les beans singleton critiques pour la performance
- Toujours tester le rafraîchissement en environnement de dev/test avant la prod

### 4. Fail-Fast
- Laisser `fail-fast=true` en production pour détecter rapidement les problèmes
- Peut être désactivé en dev si Config Server n'est pas toujours disponible

## Migration Checklist

Pour migrer un nouveau microservice vers Config Server:

- [ ] Ajouter la dépendance `spring-cloud-starter-config` dans pom.xml
- [ ] Créer `bootstrap.properties` avec connexion au Config Server
- [ ] Créer les fichiers YAML dans config-repo:
  - [ ] `<service-name>.yml` (config par défaut)
  - [ ] `<service-name>-dev.yml` (config dev)
  - [ ] `<service-name>-prod.yml` (config prod)
- [ ] Créer classe `ApplicationConfigProperties` avec `@RefreshScope`
- [ ] Tester le démarrage avec profil dev
- [ ] Tester le rafraîchissement dynamique
- [ ] Tester le démarrage avec profil prod (avec variables d'environnement)
- [ ] Documenter les propriétés spécifiques au service

## Conclusion

La migration vers Config Server apporte une centralisation, une flexibilité multi-environnements et un rafraîchissement dynamique de la configuration.

Tous les microservices métier sont maintenant configurés pour:
- ✅ Récupérer leur configuration depuis Config Server
- ✅ Supporter les profils dev/test/prod
- ✅ Rafraîchir dynamiquement leur configuration sans redémarrage
- ✅ Externaliser les secrets via variables d'environnement

Cette architecture améliore significativement la maintenabilité, la sécurité et la flexibilité du système DAOS.
