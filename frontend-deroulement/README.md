# Frontend - Déroulement des Enseignements

Frontend React pour la gestion du déroulement des enseignements avec progression et statistiques.

## Fonctionnalités

- **Gestion des Séances**: Ajouter, modifier, supprimer et lister les séances
- **Progression**: Visualiser la progression des enseignements avec barres de progression
- **Statistiques**: Rapports détaillés par module et enseignant
- **Interface Responsive**: Design moderne et adaptatif

## Structure du Projet

```
frontend-deroulement/
├── src/
│   ├── components/
│   │   ├── layout/         # Composants de layout
│   │   ├── seance/         # Gestion des séances
│   │   ├── progression/    # Progression et statistiques
│   │   ├── Login.jsx       # Authentification
│   │   └── App.jsx         # Composant principal
│   ├── services/           # Services API
│   ├── assets/css/         # Styles
│   └── main.jsx            # Point d'entrée
└── package.json
```

## Installation

```bash
npm install
```

## Développement

```bash
npm run dev
```

## Build

```bash
npm run build
```

## Port

L'application s'exécute par défaut sur `http://localhost:5173`

## Configuration API

Modifier les URLs des services dans `src/services/`:
- DeroulementService: `http://localhost:8090/api/deroulement-enseignements`
- MaquetteService: `http://localhost:8095/api`
- AuthService: `http://localhost:8089/api/auth`
