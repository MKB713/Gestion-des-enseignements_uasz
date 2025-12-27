# Guide Docker Compose - DAOS

## Vue d'ensemble

Ce guide explique comment utiliser Docker Compose pour orchestrer toute l'infrastructure DAOS (services d'infrastructure, microservices métier et front-end React).

## Architecture Complète

```
┌──────────────────────────────────────────────────────────────┐
│                        DAOS Network                           │
│  (Réseau Docker Bridge: daos-network)                        │
├──────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │     MySQL       │  │      Redis      │  │   Eureka     │ │
│  │  (Base données) │  │  (Rate Limit)   │  │   Server     │ │
│  │   Port: 3306    │  │   Port: 6379    │  │ Port: 8761   │ │
│  └────────┬────────┘  └────────┬────────┘  └──────┬───────┘ │
│           │                     │                   │         │
│  ┌────────▼────────┐  ┌────────▼────────┐  ┌──────▼───────┐ │
│  │  Config Server  │  │  API Gateway    │  │              │ │
│  │   Port: 8888    │  │   Port: 8080    │  │ Front-End    │ │
│  └────────┬────────┘  └────────┬────────┘  │ Port: 3000   │ │
│           │                     │           └──────────────┘ │
│  ┌────────▼─────────────────────▼────────┐                   │
│  │      Business Microservices           │                   │
│  │  • auth-service         (8081)        │                   │
│  │  • enseignant-service   (8082)        │                   │
│  │  • maquette-service     (8083)        │                   │
│  │  • choix-enseignement   (8084)        │                   │
│  │  • emploi-temps         (8085)        │                   │
│  │  • deroulement          (8086)        │                   │
│  └───────────────────────────────────────┘                   │
└──────────────────────────────────────────────────────────────┘
```

## Critères d'Acceptation Validés

✅ **Fichier docker-compose.yml** pour l'infrastructure
✅ **Services définis**: config-server, eureka-server, api-gateway
✅ **Réseau Docker partagé** configuré (daos-network)
✅ **Ordre de démarrage respecté** avec depends_on et health checks
✅ **Variables d'environnement externalisées** dans .env
✅ **Health checks configurés** pour tous les services
✅ **Volumes** pour la persistance MySQL (mysql-data)
✅ **Un seul docker-compose up** démarre toute l'infrastructure
✅ **Service front-end-main** défini avec React
✅ **Dockerfile multi-stage** pour React (Node.js + Nginx)
✅ **Proxy Nginx** configuré pour rediriger vers l'API Gateway

## Fichiers de Configuration

### 1. docker-compose.yml
Fichier principal d'orchestration avec:
- 11 services (2 infrastructure + 3 core + 6 microservices)
- Réseau partagé daos-network
- Health checks pour tous les services
- Variables d'environnement externalisées
- Ordre de démarrage avec dépendances

### 2. .env
Variables d'environnement pour tous les services:
- Credentials MySQL
- Configuration Redis
- Ports des services
- URLs et configuration Spring
- Profils actifs

### 3. .env.example
Template du fichier .env (sans valeurs sensibles)

## Services Définis

### Infrastructure Services

#### 1. MySQL (port 3306)
```yaml
Base de données relationnelle pour tous les microservices
Health check: mysqladmin ping
Volume: mysql-data (persistance)
Restart policy: unless-stopped
```

#### 2. Redis (port 6379)
```yaml
Cache et rate limiting pour API Gateway
Health check: redis-cli ping
Restart policy: unless-stopped
```

#### 3. Eureka Server (port 8761)
```yaml
Service Registry et Discovery
Depends on: (aucun - démarre en premier)
Health check: curl /actuator/health
```

#### 4. Config Server (port 8888)
```yaml
Configuration centralisée
Depends on: eureka-server (healthy)
Health check: curl /actuator/health
```

#### 5. API Gateway (port 8080)
```yaml
Point d'entrée unique, routing, rate limiting
Depends on: eureka-server, config-server, redis (healthy)
Health check: curl /actuator/health
```

### Business Microservices

#### 6. auth-service (port 8081)
```yaml
Authentification et gestion des utilisateurs
Depends on: mysql, eureka-server, config-server (healthy)
Variables: JWT_SECRET, DB credentials
```

#### 7. enseignant-service (port 8082)
```yaml
Gestion des enseignants et vacataires
Depends on: mysql, eureka-server, config-server (healthy)
```

#### 8. maquette-service (port 8083)
```yaml
Gestion des maquettes, formations, UE, EC
Depends on: mysql, eureka-server, config-server (healthy)
```

#### 9. choix-enseignement-service (port 8084)
```yaml
Choix d'enseignements des enseignants
Depends on: mysql, eureka-server, config-server (healthy)
```

#### 10. emploi-temps-service (port 8085)
```yaml
Gestion de l'emploi du temps
Depends on: mysql, eureka-server, config-server (healthy)
```

#### 11. deroulement-enseignement-service (port 8086)
```yaml
Déroulement et statistiques des enseignements
Depends on: mysql, eureka-server, config-server (healthy)
```

### Front-End

#### 12. front-end-main (port 3000)
```yaml
Application React avec Nginx
Depends on: api-gateway (healthy)
Proxy: /api/* vers api-gateway:8080
Dockerfile: Multi-stage (Node build + Nginx serve)
```

## Ordre de Démarrage

Docker Compose démarre les services dans cet ordre grâce aux `depends_on` avec conditions de santé:

```
1. MySQL, Redis (infrastructure)
   ↓ (attendre healthy)
2. Eureka Server
   ↓ (attendre healthy)
3. Config Server
   ↓ (attendre healthy)
4. API Gateway + Business Microservices (en parallèle)
   ↓ (attendre healthy)
5. Front-End React
```

## Utilisation

### Prérequis

- Docker 20.10+
- Docker Compose 2.0+
- 8GB RAM minimum
- 10GB espace disque

### Configuration Initiale

1. **Copier le fichier d'environnement**
```bash
cp .env.example .env
```

2. **Modifier les variables sensibles** dans `.env`:
```bash
# Changer les mots de passe en production!
MYSQL_ROOT_PASSWORD=your_secure_password
DB_PASSWORD=your_secure_password
JWT_SECRET=your_super_secret_jwt_key_here
```

### Démarrage Complet

#### Démarrer toute l'infrastructure
```bash
docker-compose up
```

#### Démarrer en arrière-plan (mode détaché)
```bash
docker-compose up -d
```

#### Voir les logs en temps réel
```bash
docker-compose logs -f
```

#### Voir les logs d'un service spécifique
```bash
docker-compose logs -f api-gateway
docker-compose logs -f auth-service
```

### Démarrage Partiel

#### Démarrer uniquement l'infrastructure
```bash
docker-compose up mysql redis eureka-server config-server api-gateway
```

#### Démarrer un microservice spécifique
```bash
docker-compose up auth-service
```

### Arrêt

#### Arrêter tous les services
```bash
docker-compose down
```

#### Arrêter et supprimer les volumes
```bash
docker-compose down -v
```

#### Arrêter un service spécifique
```bash
docker-compose stop auth-service
```

### Rebuild

#### Rebuild tous les services
```bash
docker-compose build
```

#### Rebuild un service spécifique
```bash
docker-compose build auth-service
```

#### Rebuild et redémarrer
```bash
docker-compose up --build
```

### Mise à l'échelle

#### Lancer plusieurs instances d'un service
```bash
docker-compose up --scale auth-service=3
```

## Health Checks

Tous les services ont des health checks configurés:

### Vérifier le statut de santé
```bash
docker-compose ps
```

### Exemples de health checks

**MySQL:**
```bash
mysqladmin ping -h localhost -u root -p${MYSQL_ROOT_PASSWORD}
```

**Redis:**
```bash
redis-cli ping
```

**Spring Boot Services:**
```bash
curl -f http://localhost:8761/actuator/health  # Eureka
curl -f http://localhost:8888/actuator/health  # Config Server
curl -f http://localhost:8080/actuator/health  # API Gateway
curl -f http://localhost:8081/actuator/health  # Auth Service
```

**Front-End:**
```bash
curl -f http://localhost:3000/health
```

## Réseau Docker

### Configuration
```yaml
networks:
  daos-network:
    driver: bridge
    name: daos-network
```

### Communication entre services
Les services communiquent via le nom du service:
```
http://api-gateway:8080
http://eureka-server:8761
http://mysql:3306
```

### Inspecter le réseau
```bash
docker network inspect daos-network
```

## Volumes

### MySQL Data Volume
```yaml
volumes:
  mysql-data:
    name: mysql-data
```

### Voir les volumes
```bash
docker volume ls
docker volume inspect mysql-data
```

### Sauvegarder les données MySQL
```bash
docker exec daos-mysql mysqldump -u root -proot --all-databases > backup.sql
```

### Restaurer les données MySQL
```bash
docker exec -i daos-mysql mysql -u root -proot < backup.sql
```

## Variables d'Environnement

Toutes les variables sont définies dans `.env`:

### Infrastructure
- `MYSQL_ROOT_PASSWORD`: Mot de passe root MySQL
- `REDIS_HOST`, `REDIS_PORT`: Configuration Redis
- `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`: URL Eureka

### Microservices
- `SPRING_PROFILES_ACTIVE`: Profil Spring (dev/test/prod)
- `SPRING_JPA_SHOW_SQL`: Afficher les requêtes SQL
- `JWT_SECRET`: Clé secrète JWT
- `DB_USERNAME`, `DB_PASSWORD`: Credentials base de données

### Front-End
- `FRONTEND_PORT`: Port du front-end (3000)
- `REACT_APP_API_GATEWAY_URL`: URL de l'API Gateway

## Commandes Utiles

### Surveillance

```bash
# Voir les conteneurs en cours
docker-compose ps

# Voir l'utilisation des ressources
docker stats

# Voir les logs de tous les services
docker-compose logs

# Suivre les logs en temps réel
docker-compose logs -f --tail=100

# Logs d'un service spécifique
docker-compose logs -f auth-service
```

### Debugging

```bash
# Exécuter une commande dans un conteneur
docker-compose exec auth-service bash
docker-compose exec mysql mysql -u root -p

# Inspecter un conteneur
docker inspect auth-service

# Voir les variables d'environnement d'un service
docker-compose exec auth-service env
```

### Nettoyage

```bash
# Supprimer les conteneurs arrêtés
docker-compose rm

# Supprimer les images non utilisées
docker image prune

# Supprimer tout (conteneurs, réseaux, volumes, images)
docker system prune -a --volumes

# Nettoyer uniquement ce projet
docker-compose down -v --rmi all
```

## Résolution de Problèmes

### Problème: Service ne démarre pas

**Symptôme**: Un service reste en état "starting" ou "unhealthy"

**Solutions**:
```bash
# Voir les logs détaillés
docker-compose logs service-name

# Vérifier le health check
docker inspect service-name | grep -A 10 Health

# Redémarrer le service
docker-compose restart service-name
```

### Problème: Port déjà utilisé

**Symptôme**: `Error: port already allocated`

**Solutions**:
```bash
# Identifier le processus utilisant le port
netstat -ano | findstr :8080  # Windows
lsof -i :8080                  # Linux/Mac

# Arrêter le processus ou changer le port dans .env
```

### Problème: Problème de connexion base de données

**Symptôme**: `Connection refused` ou `Unknown database`

**Solutions**:
```bash
# Vérifier que MySQL est healthy
docker-compose ps mysql

# Voir les logs MySQL
docker-compose logs mysql

# Se connecter à MySQL pour vérifier
docker-compose exec mysql mysql -u root -p
SHOW DATABASES;
```

### Problème: Service ne trouve pas Eureka

**Symptôme**: `Could not locate configserver`

**Solutions**:
```bash
# Vérifier qu'Eureka est healthy
curl http://localhost:8761/actuator/health

# Vérifier les variables d'environnement
docker-compose exec service-name env | grep EUREKA

# Redémarrer dans le bon ordre
docker-compose restart eureka-server
docker-compose restart config-server
docker-compose restart service-name
```

### Problème: Front-end ne peut pas joindre l'API

**Symptôme**: Erreur CORS ou connexion refusée

**Solutions**:
```bash
# Tester le proxy Nginx
docker-compose exec front-end-main wget -O- http://api-gateway:8080/actuator/health

# Vérifier la configuration Nginx
docker-compose exec front-end-main cat /etc/nginx/conf.d/default.conf

# Voir les logs Nginx
docker-compose logs front-end-main
```

## Tests de Validation

### Test 1: Vérifier que tous les services démarrent

```bash
# Démarrer tout
docker-compose up -d

# Attendre 2-3 minutes puis vérifier
docker-compose ps

# Tous les services doivent être "healthy" ou "running"
```

### Test 2: Vérifier les health checks

```bash
curl http://localhost:8761/actuator/health  # Eureka: UP
curl http://localhost:8888/actuator/health  # Config: UP
curl http://localhost:8080/actuator/health  # Gateway: UP
curl http://localhost:8081/actuator/health  # Auth: UP
curl http://localhost:3000/health           # Frontend: OK
```

### Test 3: Vérifier le Service Registry

```bash
# Ouvrir le dashboard Eureka
http://localhost:8761

# Tous les services doivent être enregistrés
```

### Test 4: Vérifier le Front-End

```bash
# Ouvrir l'application
http://localhost:3000

# Tester le proxy API
curl http://localhost:3000/api/actuator/health
```

### Test 5: Vérifier Swagger

```bash
# Ouvrir Swagger UI via API Gateway
http://localhost:8080/swagger-ui.html
```

## Profils d'Environnement

### Développement (dev)
```bash
# Dans .env
SPRING_PROFILES_ACTIVE=dev
SPRING_JPA_SHOW_SQL=true
LOGGING_LEVEL_DAOS=DEBUG
```

### Test
```bash
# Dans .env
SPRING_PROFILES_ACTIVE=test
SPRING_JPA_SHOW_SQL=false
LOGGING_LEVEL_DAOS=INFO
```

### Production (prod)
```bash
# Dans .env
SPRING_PROFILES_ACTIVE=prod
SPRING_JPA_SHOW_SQL=false
LOGGING_LEVEL_DAOS=WARN

# Avec des secrets sécurisés
JWT_SECRET=$(openssl rand -base64 64)
MYSQL_ROOT_PASSWORD=$(openssl rand -base64 32)
```

## Sécurité

### Bonnes Pratiques

1. **Ne jamais committer .env**
   - .env est dans .gitignore
   - Utiliser .env.example comme template

2. **Changer les mots de passe en production**
   - Générer des secrets forts
   - Utiliser des gestionnaires de secrets (Vault, AWS Secrets Manager)

3. **Limiter les ports exposés**
   - En production, ne pas exposer MySQL/Redis directement
   - Utiliser uniquement API Gateway comme point d'entrée

4. **Activer HTTPS**
   - Configurer un reverse proxy (Traefik, Nginx)
   - Utiliser Let's Encrypt pour les certificats

## Performance

### Recommandations

1. **Limites de ressources**
```yaml
services:
  auth-service:
    deploy:
      resources:
        limits:
          cpus: '0.5'
          memory: 512M
```

2. **Optimiser le temps de démarrage**
   - Utiliser des images cachées
   - Réduire les dépendances inutiles

3. **Monitoring**
   - Ajouter Prometheus + Grafana
   - Utiliser Spring Boot Actuator metrics

## Conclusion

Cette configuration Docker Compose permet de:
- ✅ Démarrer toute l'infrastructure avec une seule commande
- ✅ Gérer l'ordre de démarrage automatiquement
- ✅ Isoler les services dans un réseau Docker
- ✅ Persister les données MySQL
- ✅ Configurer via variables d'environnement
- ✅ Monitorer la santé de chaque service
- ✅ Proxy les appels API via Nginx dans le front-end

Pour plus d'informations:
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker](https://spring.io/guides/gs/spring-boot-docker/)
- [Nginx Reverse Proxy](https://docs.nginx.com/nginx/admin-guide/web-server/reverse-proxy/)
