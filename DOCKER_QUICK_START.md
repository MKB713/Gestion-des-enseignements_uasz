# Docker Compose - Démarrage Rapide

## Installation Rapide

### 1. Copier le fichier d'environnement
```bash
cp .env.example .env
```

### 2. Modifier les variables sensibles dans .env
```bash
# Changer au minimum:
MYSQL_ROOT_PASSWORD=your_secure_password
DB_PASSWORD=your_secure_password
JWT_SECRET=your_super_secret_jwt_key
```

### 3. Démarrer toute l'infrastructure
```bash
# Linux/Mac
./docker-start.sh start

# Ou manuellement
docker-compose up -d
```

```cmd
:: Windows
docker-start.bat start

:: Ou manuellement
docker-compose up -d
```

### 4. Vérifier que tout fonctionne
```bash
# Voir le statut
docker-compose ps

# Voir les logs
docker-compose logs -f
```

## Services Disponibles

Une fois démarrés, les services sont accessibles sur:

| Service | URL | Description |
|---------|-----|-------------|
| **Front-End** | http://localhost:3000 | Application React |
| **API Gateway** | http://localhost:8080 | Point d'entrée API |
| **Eureka Dashboard** | http://localhost:8761 | Service Registry |
| **Config Server** | http://localhost:8888 | Configuration |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Documentation API |

## Commandes Essentielles

### Démarrage
```bash
docker-compose up -d              # Démarrer en arrière-plan
docker-compose up                 # Démarrer avec logs
```

### Arrêt
```bash
docker-compose down               # Arrêter tous les services
docker-compose down -v            # Arrêter + supprimer volumes
```

### Logs
```bash
docker-compose logs -f            # Tous les logs
docker-compose logs -f auth-service  # Logs d'un service
```

### Statut
```bash
docker-compose ps                 # Voir le statut
docker stats                      # Voir l'utilisation des ressources
```

### Rebuild
```bash
docker-compose build              # Rebuild toutes les images
docker-compose up --build         # Rebuild et démarrer
```

## Scripts de Démarrage

### Linux/Mac
```bash
./docker-start.sh                 # Menu interactif
./docker-start.sh start           # Démarrer
./docker-start.sh stop            # Arrêter
./docker-start.sh status          # Statut
./docker-start.sh logs            # Logs
```

### Windows
```cmd
docker-start.bat                  :: Menu interactif
docker-start.bat start            :: Démarrer
docker-start.bat stop             :: Arrêter
docker-start.bat status           :: Statut
docker-start.bat logs             :: Logs
```

## Architecture

```
Infrastructure:
  ├── MySQL (3306)           - Base de données
  ├── Redis (6379)           - Cache et rate limiting
  ├── Eureka Server (8761)   - Service Registry
  ├── Config Server (8888)   - Configuration
  └── API Gateway (8080)     - Proxy et routage

Microservices:
  ├── auth-service (8081)           - Authentification
  ├── enseignant-service (8082)     - Gestion enseignants
  ├── maquette-service (8083)       - Gestion maquettes
  ├── choix-enseignement (8084)     - Choix enseignements
  ├── emploi-temps (8085)           - Emploi du temps
  └── deroulement (8086)            - Déroulement enseignements

Front-End:
  └── front-end-main (3000)  - Application React + Nginx
```

## Ordre de Démarrage

Docker Compose gère automatiquement l'ordre grâce aux `depends_on` avec health checks:

1. MySQL, Redis
2. Eureka Server (après MySQL healthy)
3. Config Server (après Eureka healthy)
4. API Gateway + Microservices (après Config Server healthy)
5. Front-End (après API Gateway healthy)

## Vérification Rapide

### Test 1: Health Checks
```bash
curl http://localhost:8761/actuator/health  # Eureka
curl http://localhost:8888/actuator/health  # Config
curl http://localhost:8080/actuator/health  # Gateway
curl http://localhost:3000/health           # Frontend
```

### Test 2: Service Registry
Ouvrir http://localhost:8761 et vérifier que tous les services sont enregistrés.

### Test 3: Front-End
Ouvrir http://localhost:3000 et vérifier la connexion à l'API Gateway.

### Test 4: Proxy Nginx
```bash
# Appel via le front-end (proxy Nginx)
curl http://localhost:3000/api/actuator/health

# Doit retourner le health de l'API Gateway
```

## Résolution de Problèmes

### Service ne démarre pas
```bash
# Voir les logs
docker-compose logs service-name

# Redémarrer
docker-compose restart service-name
```

### Port déjà utilisé
```bash
# Identifier le processus
netstat -ano | findstr :8080   # Windows
lsof -i :8080                  # Linux/Mac

# Changer le port dans .env
API_GATEWAY_PORT=8081
```

### Problème de connexion MySQL
```bash
# Vérifier MySQL
docker-compose logs mysql

# Se connecter à MySQL
docker-compose exec mysql mysql -u root -p
```

### Rebuild complet
```bash
# Tout supprimer et reconstruire
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

## Variables d'Environnement

Principales variables à configurer dans `.env`:

```bash
# MySQL
MYSQL_ROOT_PASSWORD=root
DB_PASSWORD=root

# JWT
JWT_SECRET=your_secret_key

# Profil Spring
SPRING_PROFILES_ACTIVE=dev

# Ports (optionnel)
FRONTEND_PORT=3000
API_GATEWAY_PORT=8080
```

## Documentation Complète

Pour plus de détails:
- [DOCKER_COMPOSE_GUIDE.md](./DOCKER_COMPOSE_GUIDE.md) - Guide complet
- [front-end-main/README.md](./front-end-main/README.md) - Documentation Front-End
- [CONFIG_SERVER_MIGRATION_GUIDE.md](./CONFIG_SERVER_MIGRATION_GUIDE.md) - Config Server
- [API_GATEWAY_GUIDE.md](./API_GATEWAY_GUIDE.md) - API Gateway
- [EUREKA_SERVER_GUIDE.md](./EUREKA_SERVER_GUIDE.md) - Eureka Server

## Support

Pour les problèmes ou questions:
1. Consulter les logs: `docker-compose logs -f`
2. Vérifier le statut: `docker-compose ps`
3. Consulter la documentation complète
4. Vérifier les issues GitHub du projet

---

**Développé pour l'Université Assane Seck de Ziguinchor (UASZ)**
