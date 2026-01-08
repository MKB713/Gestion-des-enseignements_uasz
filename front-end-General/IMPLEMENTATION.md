# Documentation d'Implémentation - Front-End General

## Vue d'ensemble

Ce document décrit l'implémentation complète du système de gestion pour les rôles Responsable Master et Coordinateur des Licences.

## Architecture

### Structure des Dossiers

```
front-end-General/
├── src/
│   ├── components/
│   │   ├── CrudPage.jsx          # Composant générique réutilisable pour les opérations CRUD
│   │   ├── Sidebar.jsx            # Menu de navigation latéral
│   │   └── PrivateRoute.jsx       # Protection des routes
│   ├── config/
│   │   └── api.js                 # Configuration centralisée des API
│   ├── pages/
│   │   ├── master/                # Pages pour Responsable Master
│   │   │   ├── Formations.jsx
│   │   │   ├── Filieres.jsx
│   │   │   ├── Classes.jsx
│   │   │   ├── Structures.jsx
│   │   │   ├── Maquettes.jsx
│   │   │   ├── Modules.jsx
│   │   │   ├── UE.jsx
│   │   │   ├── EC.jsx
│   │   │   ├── Enseignants.jsx
│   │   │   ├── EmploiTemps.jsx
│   │   │   ├── CahierTexte.jsx
│   │   │   └── MasterPages.css
│   │   └── coordinator/           # Pages pour Coordinateur des Licences
│   │       ├── (même structure que master/)
│   │       └── CoordinatorPages.css
│   └── context/
│       └── AuthContext.jsx        # Gestion de l'authentification
```

## Fonctionnalités Implémentées

### 1. Sidebar Amélioré

Le Sidebar a été enrichi avec tous les éléments demandés :

**Pour Responsable Master :**
- Tableau de bord
- Formations
- Filières
- Classes
- Structures (Départements, UFR, etc.)
- Maquettes
- Pédagogie
  - Modules
  - Unités d'Enseignement (UE)
  - Éléments Constitutifs (EC)
- Enseignants
- Emploi du Temps
- Cahier de Texte
- Déconnexion (dans le footer)

**Pour Coordinateur des Licences :**
- Même structure que Responsable Master

### 2. Configuration API

Le fichier `src/config/api.js` centralise tous les endpoints :

**Microservices connectés :**
- **Auth Service** (Port 8081) - Authentification
- **Enseignant Service** (Port 8082) - Gestion des enseignants
- **Maquette Service** (Port 8083) - Formations, Filières, Modules, UE, EC, etc.
- **Choix Enseignement Service** (Port 8084) - Choix d'enseignements
- **Emploi du Temps Service** (Port 8085) - Planning
- **Déroulement Enseignement Service** (Port 8086) - Cahier de texte

Tous les appels passent par l'**API Gateway** sur le port **8080**.

### 3. Composant Générique CRUD

Le composant `CrudPage.jsx` fournit :
- Affichage en tableau
- Recherche/Filtrage
- Création
- Modification
- Suppression
- Gestion des erreurs
- État de chargement
- Formulaires modaux

### 4. Pages Spécialisées

Chaque entité dispose de sa propre page avec :
- Interface utilisateur cohérente
- Connexion au microservice approprié
- Validation des formulaires
- Messages d'erreur explicites

## Connexion aux Microservices

### Format des Requêtes API

```javascript
import { API_ENDPOINTS, api } from '../config/api';

// GET - Lister
const formations = await api.get(API_ENDPOINTS.MAQUETTES.FORMATIONS);

// POST - Créer
const newFormation = await api.post(API_ENDPOINTS.MAQUETTES.FORMATIONS, data);

// PUT - Modifier
await api.put(API_ENDPOINTS.MAQUETTES.FORMATION_BY_ID(id), data);

// DELETE - Supprimer
await api.delete(API_ENDPOINTS.MAQUETTES.FORMATION_BY_ID(id));
```

### Authentification

Les requêtes incluent automatiquement :
- Token Bearer dans le header `Authorization`
- Gestion de l'expiration de session
- Redirection automatique vers login si non authentifié

## Routes

Les routes sont configurées dans `App.jsx` :

```
/master/dashboard         → Dashboard Responsable Master
/master/formations        → Gestion des formations
/master/filieres         → Gestion des filières
/master/classes          → Gestion des classes
/master/structures       → Gestion des structures
/master/maquettes        → Gestion des maquettes
/master/modules          → Gestion des modules
/master/ues              → Gestion des UE
/master/ecs              → Gestion des EC
/master/enseignants      → Gestion des enseignants
/master/emploi-temps     → Gestion emploi du temps
/master/cahier-texte     → Cahier de texte

/coordinator/*           → Même structure pour coordinateur
```

## Configuration

### Variables d'Environnement

Créer un fichier `.env` basé sur `.env.example` :

```bash
VITE_API_BASE_URL=http://localhost:8080
```

## Utilisation

### Démarrage

```bash
cd front-end-General
npm install
npm run dev
```

L'application sera accessible sur `http://localhost:5173`

### Test de Connexion

Pour tester sans backend :
- Email contenant "master" → Rôle RESPONSABLE_MASTER
- Email contenant "coord" → Rôle COORDONATEUR_DES_LICENCES
- Email contenant "admin" → Rôle ADMIN
- Email contenant "enseignant" ou "teacher" → Rôle ENSEIGNANT
- Autres → Rôle ETUDIANT

### Connexion au Backend

Pour connecter aux vrais microservices :

1. Décommenter le code API dans `Login.jsx` (lignes 30-42)
2. S'assurer que tous les microservices sont démarrés
3. Vérifier que l'API Gateway est accessible

## Structure des Données

### Formations
```json
{
  "id": 1,
  "code": "MASTER_GL",
  "libelle": "Master Génie Logiciel",
  "description": "..."
}
```

### Filières
```json
{
  "id": 1,
  "code": "INFO",
  "libelle": "Informatique",
  "description": "..."
}
```

### Classes
```json
{
  "id": 1,
  "code": "M1_GL_2024",
  "libelle": "Master 1 Génie Logiciel",
  "effectif": 45,
  "anneeAcademique": "2024-2025"
}
```

### Enseignants
```json
{
  "id": 1,
  "matricule": "ENS001",
  "nom": "Diop",
  "prenom": "Moussa",
  "email": "m.diop@uasz.sn",
  "grade": "PROFESSEUR",
  "specialite": "Génie Logiciel"
}
```

### Emploi du Temps
```json
{
  "id": 1,
  "jour": "LUNDI",
  "heureDebut": "08:00",
  "heureFin": "10:00",
  "matiere": "Algorithmique Avancée",
  "salle": "A101",
  "typeSeance": "CM"
}
```

### Cahier de Texte
```json
{
  "id": 1,
  "date": "2024-01-15",
  "matiere": "Programmation Web",
  "classe": "L3 Info",
  "sujet": "Introduction à React",
  "duree": 2,
  "contenu": "...",
  "observations": "..."
}
```

## Prochaines Étapes

1. **Backend :** Implémenter les endpoints manquants dans les microservices
2. **Validation :** Ajouter des règles de validation métier
3. **Relations :** Gérer les relations entre entités (ex: Formation → Filières)
4. **Permissions :** Affiner les permissions selon le rôle
5. **Tests :** Ajouter des tests unitaires et d'intégration
6. **Performance :** Implémenter la pagination pour les grandes listes
7. **UX :** Ajouter des confirmations et feedback utilisateur

## Notes Techniques

- **React 19.2.0** avec hooks modernes
- **React Router 7.11.0** pour la navigation
- **Vite 7.2.4** comme bundler
- **Lucide React** pour les icônes
- Architecture basée sur les composants réutilisables
- Gestion d'état locale avec hooks
- Communication API avec fetch natif

## Support

Pour toute question ou problème :
1. Vérifier que tous les microservices sont démarrés
2. Consulter les logs du serveur Vite
3. Vérifier la console du navigateur pour les erreurs
4. Vérifier la configuration de l'API Gateway
