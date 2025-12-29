# Déploiement sur Docker Desktop

Ce guide vous permet de déployer rapidement toute l'application DAOS sur Docker Desktop en quelques minutes.

## Prérequis

- **Docker Desktop** installé et démarré
  - [Télécharger Docker Desktop pour Windows](https://www.docker.com/products/docker-desktop)
  - [Télécharger Docker Desktop pour Mac](https://www.docker.com/products/docker-desktop)
  - [Télécharger Docker Desktop pour Linux](https://docs.docker.com/desktop/install/linux-install/)
- **8 Go de RAM minimum** recommandés pour Docker
- **20 Go d'espace disque** disponible

## Démarrage Rapide (3 étapes)

### 1. Cloner le projet

```bash
git clone https://github.com/MKB713/Gestion-des-enseignements_uasz.git
cd Gestion-des-enseignements_uasz
```

### 2. Configurer les variables d'environnement

**Windows (PowerShell ou CMD) :**
```cmd
copy .env.example .env
```

**Linux/Mac :**
```bash
cp .env.example .env
```

**Modifier le fichier `.env`** avec vos propres valeurs :
```properties
# Obligatoire - Changez ces valeurs !
MYSQL_ROOT_PASSWORD=votre_mot_de_passe_mysql
DB_PASSWORD=votre_mot_de_passe_db
JWT_SECRET=votre_secret_jwt_très_long_et_sécurisé

# Optionnel - Gardez les valeurs par défaut si vous voulez
SPRING_PROFILES_ACTIVE=dev
FRONTEND_PORT=3000
API_GATEWAY_PORT=8080
```

### 3. Démarrer tous les services

**Windows :**
```cmd
docker-start.bat start
```

**Linux/Mac :**
```bash
chmod +x docker-start.sh
./docker-start.sh start
```

**Ou manuellement avec Docker Compose :**
```bash
docker-compose up -d
```

C'est tout ! Attendez 2-3 minutes que tous les services démarrent.

## Vérification du Déploiement

### Ouvrir Docker Desktop

Ouvrez **Docker Desktop** et vous verrez :
- Un conteneur nommé `gestion-des-enseignements_uasz` (ou le nom du dossier)
- À l'intérieur, vous verrez environ **16 services** en cours d'exécution :
  - daos-mysql
  - daos-redis
  - eureka-server
  - config-server
  - api-gateway
  - auth-service
  - enseignant-service
  - maquette-service
  - choix-enseignement-service
  - emploi-temps-service
  - deroulement-enseignement-service
  - front-end-main
  - daos-prometheus
  - daos-grafana
  - daos-jaeger
  - daos-loki
  - etc.

### Accéder aux Services

Une fois démarrés, les services sont disponibles sur :

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:3000 | Interface utilisateur React |
| **API Gateway** | http://localhost:8080 | Point d'entrée de l'API |
| **Eureka Dashboard** | http://localhost:8761 | Registry des microservices |
| **Config Server** | http://localhost:8888 | Serveur de configuration |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | Documentation API interactive |
| **Grafana** | http://localhost:3001 | Monitoring (admin/admin) |
| **Prometheus** | http://localhost:9090 | Métriques |
| **Jaeger** | http://localhost:16686 | Tracing distribué |

### Test Rapide

1. **Vérifier Eureka** : Ouvrez http://localhost:8761 - Vous devez voir tous les services enregistrés
2. **Vérifier l'API** : http://localhost:8080/actuator/health - Doit retourner `{"status":"UP"}`
3. **Accéder au Frontend** : http://localhost:3000 - La page de connexion doit s'afficher

## Commandes Utiles

### Voir les logs en temps réel
```bash
docker-compose logs -f
```

### Voir les logs d'un service spécifique
```bash
docker-compose logs -f auth-service
docker-compose logs -f api-gateway
```

### Voir le statut de tous les services
```bash
docker-compose ps
```

### Arrêter tous les services
```bash
docker-compose down
```

### Redémarrer tous les services
```bash
docker-compose restart
```

### Rebuild et redémarrer (après modification du code)
```bash
docker-compose up --build -d
```

### Tout supprimer (conteneurs + volumes + données)
```bash
docker-compose down -v
```

## Structure de l'Application

```
┌─────────────────────────────────────────────────┐
│           Frontend React (Port 3000)            │
└─────────────────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│           API Gateway (Port 8080)               │
│              + Rate Limiting                    │
└─────────────────────────────────────────────────┘
                       │
        ┌──────────────┼──────────────┐
        ▼              ▼              ▼
   ┌─────────┐   ┌─────────┐   ┌─────────┐
   │  Auth   │   │Enseignant│   │Maquette │ ...
   │ Service │   │ Service │   │ Service │
   │  8081   │   │  8082   │   │  8083   │
   └─────────┘   └─────────┘   └─────────┘
        │              │              │
        └──────────────┼──────────────┘
                       ▼
            ┌─────────────────────┐
            │   MySQL Database    │
            │   (Partagée)        │
            └─────────────────────┘
```

## Architecture des Microservices

### Services d'Infrastructure
- **MySQL** : Base de données partagée avec des schémas séparés par service
- **Redis** : Cache et rate limiting pour l'API Gateway
- **Eureka Server** : Registry de services (découverte dynamique)
- **Config Server** : Configuration centralisée

### Microservices Métier
- **auth-service** (8081) : Authentification JWT, gestion des utilisateurs
- **enseignant-service** (8082) : Gestion des enseignants
- **maquette-service** (8083) : Gestion des formations et maquettes pédagogiques
- **choix-enseignement-service** (8084) : Choix d'enseignements par les enseignants
- **emploi-temps-service** (8085) : Génération et gestion des emplois du temps
- **deroulement-enseignement-service** (8086) : Suivi du déroulement des cours

### Monitoring & Observabilité
- **Prometheus** : Collecte de métriques
- **Grafana** : Visualisation des métriques
- **Jaeger** : Tracing distribué des requêtes
- **Loki + Promtail** : Agrégation et analyse des logs

## Résolution de Problèmes

### Les services ne démarrent pas

1. **Vérifier Docker Desktop** : Assurez-vous que Docker Desktop est bien démarré
2. **Vérifier les ressources** : Allouez au moins 8 Go de RAM à Docker Desktop
   - Windows/Mac : Docker Desktop → Settings → Resources → Memory
3. **Voir les logs d'erreur** :
   ```bash
   docker-compose logs
   ```

### Port déjà utilisé

Si un port est déjà utilisé (ex: 8080, 3000), modifiez le fichier `.env` :

```properties
# Exemple : changer le port du frontend
FRONTEND_PORT=3001
API_GATEWAY_PORT=8081
```

Puis redémarrez :
```bash
docker-compose down
docker-compose up -d
```

### Service en erreur "Unhealthy"

Attendez 2-3 minutes car les services démarrent dans un ordre spécifique :
1. MySQL et Redis (30 secondes)
2. Eureka Server (1 minute)
3. Config Server (30 secondes)
4. Microservices et API Gateway (1-2 minutes)

Si après 5 minutes le problème persiste :
```bash
# Redémarrer le service problématique
docker-compose restart nom-du-service

# Exemple
docker-compose restart auth-service
```

### Base de données vide

Les bases de données sont créées automatiquement au premier démarrage grâce au paramètre `createDatabaseIfNotExist=true`.

Pour réinitialiser complètement :
```bash
docker-compose down -v
docker-compose up -d
```

### Rebuild complet

Si vous avez modifié le code source :
```bash
# Rebuild toutes les images
docker-compose build --no-cache

# Démarrer avec les nouvelles images
docker-compose up -d
```

## Configuration Avancée

### Modifier les Ports

Éditez le fichier `.env` :
```properties
# Ports personnalisés
FRONTEND_PORT=3000
API_GATEWAY_PORT=8080
EUREKA_SERVER_PORT=8761
MYSQL_PORT=3306
REDIS_PORT=6379

# Ports des microservices
AUTH_SERVICE_PORT=8081
ENSEIGNANT_SERVICE_PORT=8082
MAQUETTE_SERVICE_PORT=8083
# ...
```

### Profils Spring

Changez le profil Spring (dev, prod, test) :
```properties
SPRING_PROFILES_ACTIVE=dev
```

### Configuration Base de Données

```properties
MYSQL_ROOT_PASSWORD=votre_password
DB_USERNAME=root
DB_PASSWORD=votre_password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

Options pour `SPRING_JPA_HIBERNATE_DDL_AUTO` :
- `update` : Mise à jour auto du schéma (recommandé dev)
- `create` : Recrée la base à chaque démarrage
- `create-drop` : Crée et supprime à l'arrêt
- `validate` : Valide uniquement (recommandé prod)
- `none` : Aucune action

## Monitoring

### Grafana

1. Ouvrir http://localhost:3001
2. Login : `admin` / `admin`
3. Les dashboards sont pré-configurés dans `monitoring/grafana/dashboards/`

### Prometheus

1. Ouvrir http://localhost:9090
2. Explorer les métriques des microservices
3. Requêtes utiles :
   - `up` : Services actifs
   - `http_server_requests_seconds_count` : Nombre de requêtes HTTP
   - `jvm_memory_used_bytes` : Utilisation mémoire JVM

### Jaeger (Tracing)

1. Ouvrir http://localhost:16686
2. Sélectionner un service (ex: api-gateway)
3. Cliquer sur "Find Traces" pour voir les requêtes distribuées

## Support

### Logs détaillés

Activer les logs DEBUG dans `.env` :
```properties
LOGGING_LEVEL_DAOS=DEBUG
SPRING_JPA_SHOW_SQL=true
```

### Documentation Complète

- [DOCKER_COMPOSE_GUIDE.md](./DOCKER_COMPOSE_GUIDE.md) - Guide complet Docker Compose
- [DOCKER_QUICK_START.md](./DOCKER_QUICK_START.md) - Démarrage rapide
- [MONITORING_GUIDE.md](./MONITORING_GUIDE.md) - Guide monitoring
- [API_GATEWAY_GUIDE.md](./API_GATEWAY_GUIDE.md) - Guide API Gateway
- [EUREKA_SERVER_GUIDE.md](./EUREKA_SERVER_GUIDE.md) - Guide Eureka

### Aide en Ligne

- Issues GitHub : https://github.com/MKB713/Gestion-des-enseignements_uasz/issues
- Eureka Dashboard : http://localhost:8761 (vérifier l'état des services)
- Swagger API : http://localhost:8080/swagger-ui.html (tester les endpoints)

## Comptes de Test

Une fois l'application démarrée, utilisez ces comptes pour vous connecter :

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| Admin | admin@uasz.sn | admin123 |
| Enseignant | enseignant@uasz.sn | ens123 |
| Étudiant | etudiant@uasz.sn | etu123 |

Ces comptes sont créés automatiquement au premier démarrage de `auth-service`.

---

**Développé pour l'Université Assane Seck de Ziguinchor (UASZ)**

Pour toute question ou problème, consultez les logs avec `docker-compose logs -f` ou ouvrez une issue sur GitHub.
