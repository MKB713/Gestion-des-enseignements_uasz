# Guide de Test Eureka Server - DAOS

Ce guide vous permet de tester rapidement que le Discovery Service (Eureka Server) fonctionne correctement.

## Prérequis

- Docker et Docker Compose installés
- Ports 8761, 8080-8086, 3306 disponibles

## Test 1 : Démarrage d'Eureka Server seul

### Étape 1 : Démarrer Eureka Server

```bash
docker-compose up -d eureka-server
```

### Étape 2 : Vérifier les logs

```bash
docker logs eureka-server
```

**Résultat attendu :**
```
Started EurekaServerApplication in X seconds
```

### Étape 3 : Tester le healthcheck

```bash
curl http://localhost:8761/actuator/health
```

**Résultat attendu :**
```json
{
  "status": "UP"
}
```

### Étape 4 : Accéder au dashboard

Ouvrir dans un navigateur : **http://localhost:8761**

**Résultat attendu :**
- Page web du dashboard Eureka
- Titre "Eureka" visible
- Section "Instances currently registered with Eureka"
- Message "No instances available" (normal, aucun service enregistré pour le moment)

---

## Test 2 : Enregistrement des microservices

### Étape 1 : Démarrer tous les services

```bash
docker-compose up -d
```

### Étape 2 : Attendre l'enregistrement

```bash
# Attendre 30-60 secondes pour que tous les services s'enregistrent
sleep 60
```

### Étape 3 : Vérifier le dashboard

Rafraîchir la page : **http://localhost:8761**

**Résultat attendu :**

Vous devez voir les services suivants avec statut **UP** :

| Application | Status |
|-------------|--------|
| API-GATEWAY | UP (1) - api-gateway:8080 |
| AUTH-SERVICE | UP (1) - auth-service:8081 |
| CHOIX-ENSEIGNEMENT-SERVICE | UP (1) - choix-enseignement-service:8084 |
| CONFIG-SERVER | UP (1) - config-server:8888 |
| DEROULEMENT-ENSEIGNEMENT-SERVICE | UP (1) - deroulement-enseignement-service:8086 |
| EMPLOI-TEMPS-SERVICE | UP (1) - emploi-temps-service:8085 |
| ENSEIGNANT-SERVICE | UP (1) - enseignant-service:8082 |
| MAQUETTE-SERVICE | UP (1) - maquette-service:8083 |

### Étape 4 : Vérifier les logs d'un service

```bash
docker logs auth-service | grep -i eureka
```

**Résultat attendu :**
```
DiscoveryClient_AUTH-SERVICE - registration status: 204
```

---

## Test 3 : Communication via Eureka (Service Discovery)

### Étape 1 : Tester le routing du Gateway

Le Gateway utilise Eureka pour découvrir les services.

```bash
# Tester la route vers auth-service
curl http://localhost:8080/

# Tester une route API
curl http://localhost:8080/api/auth/health
```

**Résultat attendu :**
- Pas d'erreur 503 (Service Unavailable)
- Le Gateway trouve auth-service via Eureka

### Étape 2 : Vérifier les logs du Gateway

```bash
docker logs api-gateway | grep -i "auth-service"
```

**Résultat attendu :**
```
Mapped [/api/auth/**] to Route [auth-service-api]
```

---

## Test 4 : Résistance aux pannes

### Étape 1 : Arrêter un service

```bash
docker stop auth-service
```

### Étape 2 : Attendre la détection

```bash
# Attendre 90 secondes (lease expiration)
sleep 90
```

### Étape 3 : Vérifier le dashboard

Rafraîchir **http://localhost:8761**

**Résultat attendu :**
- AUTH-SERVICE n'apparaît plus dans la liste OU est marqué DOWN

### Étape 4 : Redémarrer le service

```bash
docker start auth-service
```

### Étape 5 : Attendre le re-enregistrement

```bash
# Attendre 30 secondes (heartbeat renewal)
sleep 30
```

### Étape 6 : Vérifier le dashboard

Rafraîchir **http://localhost:8761**

**Résultat attendu :**
- AUTH-SERVICE réapparaît avec statut UP

---

## Test 5 : Vérification du healthcheck Docker

### Commande

```bash
docker inspect eureka-server | grep -A 10 "Health"
```

**Résultat attendu :**
```json
"Health": {
    "Status": "healthy",
    "FailingStreak": 0,
    "Log": [...]
}
```

### Test manuel du healthcheck

```bash
docker exec eureka-server curl -f http://localhost:8761/actuator/health
```

**Résultat attendu :**
```json
{
  "status": "UP"
}
```

---

## Test 6 : Vérification des endpoints Actuator

### Health endpoint

```bash
curl http://localhost:8761/actuator/health | jq
```

**Résultat attendu :**
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {...}
    },
    "eureka": {
      "status": "UP",
      "details": {...}
    }
  }
}
```

### Info endpoint

```bash
curl http://localhost:8761/actuator/info
```

---

## Checklist de validation complète

Utilisez cette checklist pour valider tous les critères d'acceptation :

- [ ] **Démarrage sur le port 8761**
  ```bash
  curl http://localhost:8761/actuator/health
  ```

- [ ] **Dashboard accessible**
  - Ouvrir http://localhost:8761 dans un navigateur
  - Vérifier que la page s'affiche

- [ ] **Enregistrement automatique**
  - Démarrer tous les services : `docker-compose up -d`
  - Attendre 60 secondes
  - Vérifier sur http://localhost:8761 que 8 services sont enregistrés

- [ ] **Affichage des instances et statut**
  - Sur le dashboard, vérifier la section "Instances currently registered with Eureka"
  - Tous les services doivent avoir le statut UP

- [ ] **Haute disponibilité**
  - ✅ Configuration standalone (développement)
  - ⚠️ Peer awareness non configuré (acceptable pour dev)

- [ ] **Conteneurisation Docker**
  ```bash
  docker ps | grep eureka-server
  ```

---

## Troubleshooting rapide

### Problème : Eureka ne démarre pas

```bash
# Vérifier les logs
docker logs eureka-server

# Vérifier le port
netstat -an | grep 8761

# Redémarrer proprement
docker-compose down
docker-compose up -d eureka-server
```

### Problème : Services non enregistrés

```bash
# Vérifier les logs du service
docker logs <service-name> | grep -i eureka

# Vérifier la connectivité réseau
docker exec <service-name> ping eureka-server

# Vérifier la configuration
docker exec <service-name> env | grep EUREKA
```

### Problème : Dashboard inaccessible

```bash
# Vérifier que le conteneur tourne
docker ps | grep eureka-server

# Vérifier le port forwarding
docker port eureka-server

# Tester depuis le conteneur
docker exec eureka-server curl http://localhost:8761
```

### Problème : Healthcheck échoue

```bash
# Vérifier que curl est installé
docker exec eureka-server which curl

# Tester manuellement
docker exec eureka-server curl -f http://localhost:8761/actuator/health

# Vérifier les logs Actuator
docker logs eureka-server | grep -i actuator
```

---

## Tests de performance (optionnel)

### Test de charge basique

```bash
# Installer Apache Bench (si pas déjà installé)
# Ubuntu/Debian: apt-get install apache2-utils
# macOS: brew install httpd

# Tester le dashboard (100 requêtes, 10 concurrentes)
ab -n 100 -c 10 http://localhost:8761/

# Tester le health endpoint
ab -n 100 -c 10 http://localhost:8761/actuator/health
```

---

## Script de test automatique

Créez un fichier `test-eureka.sh` :

```bash
#!/bin/bash

echo "=== Test Eureka Server DAOS ==="
echo ""

# Couleurs
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Test 1: Healthcheck
echo "Test 1: Healthcheck..."
if curl -s http://localhost:8761/actuator/health | grep -q "UP"; then
    echo -e "${GREEN}✓ Healthcheck OK${NC}"
else
    echo -e "${RED}✗ Healthcheck FAILED${NC}"
    exit 1
fi

# Test 2: Dashboard accessible
echo "Test 2: Dashboard accessible..."
if curl -s http://localhost:8761 | grep -q "Eureka"; then
    echo -e "${GREEN}✓ Dashboard OK${NC}"
else
    echo -e "${RED}✗ Dashboard FAILED${NC}"
    exit 1
fi

# Test 3: Vérifier les services enregistrés
echo "Test 3: Services enregistrés..."
SERVICES=$(curl -s http://localhost:8761 | grep -o "UP ([0-9]*)" | wc -l)
if [ "$SERVICES" -ge 7 ]; then
    echo -e "${GREEN}✓ $SERVICES services enregistrés${NC}"
else
    echo -e "${RED}✗ Seulement $SERVICES services enregistrés (attendu: 8)${NC}"
fi

# Test 4: Container running
echo "Test 4: Container Docker..."
if docker ps | grep -q "eureka-server"; then
    echo -e "${GREEN}✓ Container running${NC}"
else
    echo -e "${RED}✗ Container not running${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}=== Tous les tests réussis ===${NC}"
```

**Utilisation :**

```bash
chmod +x test-eureka.sh
./test-eureka.sh
```

---

## Résumé des URLs importantes

| Service | URL | Description |
|---------|-----|-------------|
| Dashboard Eureka | http://localhost:8761 | Interface web du Discovery Service |
| Health | http://localhost:8761/actuator/health | État de santé d'Eureka |
| Info | http://localhost:8761/actuator/info | Informations de l'application |
| API Gateway | http://localhost:8080 | Point d'entrée des APIs |

---

## Prochaines étapes

Une fois Eureka validé :

1. ✅ Tester la communication entre microservices via OpenFeign
2. ✅ Valider le load balancing du Gateway
3. ✅ Tester la résilience (circuit breaker)
4. ⚠️ Configurer la haute disponibilité (production uniquement)

---

**Date de création :** 2025-12-24
**Version :** 1.0
**Auteur :** Équipe DAOS - UASZ
