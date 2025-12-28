# Déploiement Docker - Configuration Terminée

Date : 28 Décembre 2025

## Résumé

Votre application DAOS est maintenant configurée pour être déployée sur Docker Desktop. Tous les fichiers nécessaires ont été créés et corrigés.

## Ce qui a été fait

###  1. Correction des Dockerfiles

Tous les Dockerfiles des microservices ont été mis à jour pour inclure `curl` (nécessaire pour les healthchecks) :

- ✅ api-gateway/Dockerfile
- ✅ auth-service/Dockerfile
- ✅ config-server/Dockerfile
- ✅ enseignant-service/Dockerfile
- ✅ maquette-service/Dockerfile
- ✅ choix-enseignement-service/Dockerfile
- ✅ emploi-temps-service/Dockerfile
- ✅ deroulement-enseignement-service/Dockerfile
- ✅ eureka-server/Dockerfile
- ✅ front-end-main/Dockerfile

### 2. Correction du fichier pom.xml

Correction d'une erreur XML dans `choix-enseignement-service/pom.xml` :
- Problème : balise `<dependency>` en dehors de `<dependencies>`
- Solution : Restructuration correcte du fichier XML

### 3. Optimisation docker-compose.yml

- Suppression de l'attribut obsolète `version: '3.8'`
- Configuration validée et prête

### 4. Documentation ajoutée

**README-DOCKER.md** : Guide complet de déploiement avec :
- Instructions de démarrage en 3 étapes
- Liste complète des services et leurs ports
- Commandes Docker utiles
- Section de dépannage

### 5. Sécurité

- `.env` ajouté au `.gitignore` (protection des secrets)

## Démarrage Rapide

### Option 1 : Script automatique (Recommandé)

```cmd
:: Windows
docker-start.bat start
```

```bash
# Linux/Mac
./docker-start.sh start
```

### Option 2 : Docker Compose manuel

```bash
# Démarrer tous les services
docker-compose up -d

# Ou sans monitoring (plus rapide)
docker-compose up -d mysql redis eureka-server config-server api-gateway auth-service enseignant-service maquette-service choix-enseignement-service emploi-temps-service deroulement-enseignement-service
```

## Services disponibles après démarrage

| Service | URL | Port |
|---------|-----|------|
| **Frontend** | http://localhost:3000 | 3000 |
| **API Gateway** | http://localhost:8080 | 8080 |
| **Eureka Dashboard** | http://localhost:8761 | 8761 |
| **Config Server** | http://localhost:8888 | 8888 |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | 8080 |
| **Grafana** (optionnel) | http://localhost:3001 | 3001 |
| **Prometheus** (optionnel) | http://localhost:9090 | 9090 |
| **Jaeger** (optionnel) | http://localhost:16686 | 16686 |

## Architecture déployée

```
Frontend (React + Nginx)
        ↓
API Gateway (Rate Limiting + Routing)
        ↓
    ┌───┴───────────────┐
    ↓         ↓         ↓
Services  Services  Services
Métier    Métier    Métier
    ↓         ↓         ↓
    └────┬────┴────┬────┘
         ↓         ↓
      MySQL     Redis
```

## Microservices déployés

### Infrastructure
1. **MySQL** (3306) - Base de données partagée
2. **Redis** (6379) - Cache et rate limiting
3. **Eureka Server** (8761) - Service Registry
4. **Config Server** (8888) - Configuration centralisée

### API & Gateway
5. **API Gateway** (8080) - Point d'entrée unique avec rate limiting

### Microservices métier
6. **auth-service** (8081) - Authentification JWT
7. **enseignant-service** (8082) - Gestion des enseignants
8. **maquette-service** (8083) - Gestion des maquettes pédagogiques
9. **choix-enseignement-service** (8084) - Choix d'enseignements
10. **emploi-temps-service** (8085) - Emplois du temps
11. **deroulement-enseignement-service** (8086) - Déroulement des cours

### Frontend
12. **front-end-main** (3000) - Interface utilisateur React

### Monitoring (optionnel)
13. **Prometheus** - Collecte de métriques
14. **Grafana** - Visualisation
15. **Jaeger** - Tracing distribué
16. **Loki + Promtail** - Logs

## Vérification du déploiement

### 1. Vérifier Docker Desktop

Ouvrez **Docker Desktop** et vous devriez voir tous les conteneurs en cours d'exécution.

### 2. Vérifier Eureka

Ouvrez http://localhost:8761 - Tous les microservices doivent être enregistrés.

### 3. Tester l'API

```bash
curl http://localhost:8080/actuator/health
```

Résultat attendu : `{"status":"UP"}`

### 4. Accéder au Frontend

Ouvrez http://localhost:3000 - La page de connexion doit s'afficher.

## Commandes utiles

### Voir les logs
```bash
# Tous les services
docker-compose logs -f

# Un service spécifique
docker-compose logs -f auth-service
docker-compose logs -f eureka-server
```

### Voir le statut
```bash
docker-compose ps
```

### Redémarrer un service
```bash
docker-compose restart auth-service
```

### Arrêter tous les services
```bash
docker-compose down
```

### Rebuild complet
```bash
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

## Comptes de test

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| Admin | admin@uasz.sn | admin123 |
| Enseignant | enseignant@uasz.sn | ens123 |
| Étudiant | etudiant@uasz.sn | etu123 |

## Problèmes courants

### Port déjà utilisé

Modifiez le fichier `.env` :
```properties
FRONTEND_PORT=3001
API_GATEWAY_PORT=8081
```

### Service ne démarre pas

Voir les logs :
```bash
docker-compose logs nom-du-service
```

### Rebuild nécessaire

Après modification du code :
```bash
docker-compose up --build -d
```

## Temps de démarrage

- **Premier démarrage** : 10-15 minutes (téléchargement images + build)
- **Démarrages suivants** : 2-3 minutes (images en cache)

L'ordre de démarrage est géré automatiquement par les healthchecks :
1. MySQL + Redis (30 secondes)
2. Eureka Server (1 minute)
3. Config Server (30 secondes)
4. Microservices + API Gateway (1-2 minutes)
5. Frontend (30 secondes)

## Fichiers importants

- `docker-compose.yml` - Configuration complète de l'infrastructure
- `.env` - Variables d'environnement (ne PAS commit !)
- `.env.example` - Exemple de configuration
- `README-DOCKER.md` - Guide détaillé de déploiement
- `docker-start.bat` / `docker-start.sh` - Scripts de démarrage

## Prochaines étapes

1. Démarrer les services : `docker-compose up -d`
2. Attendre 2-3 minutes
3. Vérifier Eureka : http://localhost:8761
4. Accéder au frontend : http://localhost:3000
5. Tester l'authentification avec les comptes de test

## Support

- Documentation complète : Voir `README-DOCKER.md`
- Logs : `docker-compose logs -f`
- Eureka Dashboard : http://localhost:8761
- Swagger API : http://localhost:8080/swagger-ui.html

---

**Projet : Gestion des Enseignements - UASZ**
**Infrastructure : Microservices Spring Boot + React**
**Déploiement : Docker Compose**

Pour toute question, consultez les logs ou la documentation complète.
