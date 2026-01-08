# Intégration Backend - Guide Complet

## Vue d'ensemble

Le frontend est maintenant entièrement connecté à tous les microservices backend via l'API Gateway. Ce document décrit comment démarrer et tester l'ensemble du système.

## Architecture de Connexion

```
Frontend (React)
    ↓
API Gateway (Port 8080)
    ↓
├── Auth Service (Port 8081)
├── Enseignant Service (Port 8082)
├── Maquette Service (Port 8083)
├── Choix Enseignement Service (Port 8084)
├── Emploi du Temps Service (Port 8085)
└── Déroulement Enseignement Service (Port 8086)
```

## Modifications Effectuées

### 1. Service d'Authentification (`src/services/authService.js`)
- Gestion complète de l'authentification JWT
- Stockage sécurisé des tokens (access + refresh)
- Rafraîchissement automatique des tokens
- Méthodes : `login()`, `logout()`, `refreshToken()`, `validateToken()`

### 2. Contexte d'Authentification (`src/context/AuthContext.jsx`)
- Intégration du service d'authentification
- Validation automatique au chargement de l'application
- Gestion d'état avec loading/isAuthenticated
- Rafraîchissement automatique des tokens expirés

### 3. Configuration API (`src/config/api.js`)
- Endpoints mappés exactement aux controllers backend
- Helper `apiRequest` avec :
  - Injection automatique du token Bearer
  - Rafraîchissement automatique en cas de 401
  - Gestion des erreurs centralisée
  - Support des réponses 204 No Content
- Méthodes : `api.get()`, `api.post()`, `api.put()`, `api.delete()`

### 4. Formulaire de Login (`src/pages/Login.jsx`)
- Connexion réelle au backend
- Affichage des erreurs backend
- Redirection automatique selon le rôle
- État de chargement pendant l'authentification

## Endpoints Backend Mappés

### Auth Service
- `POST /api/auth/login` - Authentification
- `POST /api/auth/refresh` - Rafraîchissement du token
- `POST /api/auth/logout` - Déconnexion
- `GET /api/auth/validate` - Validation du token

### Maquette Service
Tous les endpoints suivent le pattern : `/api/maquette/{resource}`

- **Formations** : `/api/maquette/formations`
- **Filières** : `/api/maquette/filieres`
- **Classes** : `/api/maquette/classes`
- **Niveaux** : `/api/maquette/niveaux`
- **Modules** : `/api/maquette/modules`
- **UE (Unités d'Enseignement)** : `/api/maquette/ues`
- **EC (Éléments Constitutifs)** : `/api/maquette/ecs`
- **Structures** : `/api/maquette/structures`
- **Maquettes** : `/api/maquette/maquettes`

Opérations disponibles pour chaque ressource :
- `GET /` - Liste toutes les ressources
- `GET /{id}` - Détails d'une ressource
- `POST /` - Créer une ressource
- `PUT /{id}` - Modifier une ressource
- `DELETE /{id}` - Supprimer une ressource
- `PATCH /{id}/archiver` - Archiver (pour certaines ressources)

### Enseignant Service
- `GET /api/enseignants` - Liste des enseignants
- `GET /api/enseignants/{id}` - Détails d'un enseignant
- `POST /api/enseignants` - Créer un enseignant
- `PUT /api/enseignants/{id}` - Modifier un enseignant
- `POST /api/enseignants/{id}/archive` - Archiver
- `POST /api/enseignants/{id}/activate` - Activer

### Emploi du Temps Service
- `GET /api/emploi-temps` - Liste
- `GET /api/emploi-temps/classe/{classeId}` - Par classe
- `GET /api/emploi-temps/enseignant/{enseignantId}` - Par enseignant
- `POST /api/emploi-temps` - Créer
- `PUT /api/emploi-temps/{id}` - Modifier
- `DELETE /api/emploi-temps/{id}` - Supprimer

### Déroulement Enseignement Service (Cahier de Texte)
- `GET /api/cahier-texte` - Liste
- `GET /api/cahier-texte/{id}` - Détails
- `GET /api/cahier-texte/classe/{classeId}` - Par classe
- `POST /api/cahier-texte` - Créer
- `PUT /api/cahier-texte/{id}` - Modifier
- `DELETE /api/cahier-texte/{id}` - Supprimer

## Démarrage du Système

### 1. Démarrer tous les Microservices Backend

```bash
# Dans des terminaux séparés ou avec un script de démarrage

# Config Server (doit être démarré en premier)
cd config-server
mvn spring-boot:run

# Eureka Server
cd eureka-server
mvn spring-boot:run

# API Gateway
cd api-gateway
mvn spring-boot:run

# Auth Service
cd auth-service
mvn spring-boot:run

# Maquette Service
cd maquette-service
mvn spring-boot:run

# Enseignant Service
cd enseignant-service
mvn spring-boot:run

# Emploi Temps Service
cd emploi-temps-service
mvn spring-boot:run

# Déroulement Enseignement Service
cd deroulement-enseignement-service
mvn spring-boot:run

# Choix Enseignement Service
cd choix-enseignement-service
mvn spring-boot:run
```

### 2. Vérifier que tous les services sont UP

Accédez à Eureka Dashboard :
```
http://localhost:8761
```

Vous devriez voir tous les services enregistrés et UP.

### 3. Démarrer le Frontend

Le serveur frontend Vite est déjà en cours d'exécution sur http://localhost:5173

Si ce n'est pas le cas :
```bash
cd front-end-General
npm run dev
```

## Tester l'Intégration

### 1. Test de Connexion

1. Ouvrez http://localhost:5173
2. Cliquez sur "Connexion"
3. Essayez de vous connecter avec un compte existant

**Comptes de test** (à créer si nécessaire via l'auth-service) :
- Email : `admin@uasz.sn`, Rôle : ADMIN
- Email : `master@uasz.sn`, Rôle : RESPONSABLE_MASTER
- Email : `coord@uasz.sn`, Rôle : COORDONATEUR_DES_LICENCES

### 2. Test des Fonctionnalités CRUD

#### Test des Formations (Responsable Master)

1. Connectez-vous en tant que Responsable Master
2. Allez dans "Formations" dans le menu
3. Vérifiez que la liste se charge depuis le backend
4. Créez une nouvelle formation :
   ```json
   {
     "code": "M2_IA",
     "libelle": "Master 2 Intelligence Artificielle",
     "description": "Formation en IA et ML"
   }
   ```
5. Vérifiez qu'elle apparaît dans la liste
6. Modifiez-la
7. Supprimez-la

#### Test des Filières

1. Allez dans "Filières"
2. Créez une filière :
   ```json
   {
     "code": "INFO",
     "libelle": "Informatique",
     "description": "Filière informatique"
   }
   ```

#### Test des Modules

1. Allez dans "Pédagogie" > "Modules"
2. Créez un module :
   ```json
   {
     "code": "MOD_001",
     "libelle": "Programmation Avancée",
     "coefficient": 3,
     "credits": 6
   }
   ```

### 3. Vérification des Tokens

Ouvrez la console du navigateur (F12) :

```javascript
// Vérifier le token stocké
console.log('Access Token:', localStorage.getItem('access_token'));
console.log('Refresh Token:', localStorage.getItem('refresh_token'));
console.log('User:', localStorage.getItem('user'));
```

### 4. Test du Rafraîchissement Automatique

Le token sera automatiquement rafraîchi si :
- Une requête API retourne 401 Unauthorized
- Le token actuel est expiré

Vous pouvez tester en :
1. Supprimant manuellement l'access_token dans localStorage
2. Faisant une requête (ex: charger la liste des formations)
3. Le système devrait utiliser le refresh_token pour obtenir un nouveau access_token

### 5. Monitoring des Requêtes API

Dans l'onglet "Network" de la console :
- Filtrez par "XHR" ou "Fetch"
- Chaque requête doit avoir le header `Authorization: Bearer <token>`
- Status 200 = Succès
- Status 401 = Token expiré (rafraîchissement automatique)
- Status 403 = Accès interdit (manque de permissions)

## Dépannage

### Problème : CORS Error

**Solution :** Vérifiez que l'API Gateway a la configuration CORS correcte :

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins: "http://localhost:5173"
            allowedMethods: "*"
            allowedHeaders: "*"
```

### Problème : 401 en boucle

**Causes possibles :**
1. Le refresh token est expiré
2. L'endpoint de rafraîchissement ne fonctionne pas

**Solution :**
1. Supprimez tous les tokens : `localStorage.clear()`
2. Reconnectez-vous
3. Vérifiez les logs du auth-service

### Problème : Données vides

**Causes :**
1. Service non démarré
2. Base de données vide
3. Mauvais endpoint

**Solution :**
1. Vérifiez Eureka : tous les services sont UP ?
2. Testez directement l'endpoint avec curl/Postman
3. Vérifiez les logs du microservice concerné

### Problème : "Service Unavailable"

**Cause :** Le service n'est pas enregistré dans Eureka

**Solution :**
1. Attendez 30 secondes (délai d'enregistrement)
2. Redémarrez le service
3. Vérifiez le fichier `application.properties` du service

## Tests avec cURL

### Login
```bash
curl -X POST http://localhost:8080/auth-service/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@uasz.sn","password":"votre_mot_de_passe"}'
```

### Liste des Formations (avec token)
```bash
curl http://localhost:8080/maquette-service/api/maquette/formations \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Créer une Formation
```bash
curl -X POST http://localhost:8080/maquette-service/api/maquette/formations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{"code":"TEST","libelle":"Formation Test","description":"Test"}'
```

## Structure des Données Retournées

### Réponse de Login
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "user": {
    "id": 1,
    "email": "admin@uasz.sn",
    "nom": "Admin",
    "prenom": "Super",
    "role": "ADMIN"
  }
}
```

### Réponse de Liste (Formations)
```json
[
  {
    "id": 1,
    "code": "M2_GL",
    "libelle": "Master 2 Génie Logiciel",
    "description": "Formation avancée en génie logiciel",
    "actif": true,
    "archive": false
  }
]
```

## Prochaines Étapes

1. **Ajouter des données de test** dans les microservices
2. **Tester tous les CRUD** pour chaque entité
3. **Implémenter la pagination** pour les grandes listes
4. **Ajouter la gestion des erreurs** plus fine (messages personnalisés)
5. **Implémenter les filtres** et recherches côté backend
6. **Ajouter la validation** des formulaires côté client
7. **Implémenter les relations** entre entités (ex: Formation → Filières)

## Support

En cas de problème :
1. Vérifiez les logs du microservice concerné
2. Vérifiez la console du navigateur (erreurs JS)
3. Vérifiez l'onglet Network pour les détails de la requête
4. Testez l'endpoint directement avec curl/Postman
5. Vérifiez qu'Eureka voit tous les services

## Remarques Importantes

- **Tous les appels passent par l'API Gateway** (port 8080)
- **Les tokens JWT sont automatiquement gérés** par le frontend
- **Le rafraîchissement des tokens est automatique** (transparent pour l'utilisateur)
- **Les erreurs 401 sont gérées automatiquement** avec retry après refresh
- **CORS est configuré** au niveau de l'API Gateway

---

**Le système est maintenant prêt pour l'utilisation !** 🎉
