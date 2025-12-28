# DAOS Front-End

Application React pour le système de gestion des enseignements DAOS (UASZ).

## Caractéristiques

- Application React 18
- Proxy Nginx pour redirection des appels API vers l'API Gateway
- Dockerfile multi-stage (Build avec Node.js, Service avec Nginx)
- Configuration CORS
- Health check endpoint

## Architecture

```
┌─────────────────┐
│   Navigateur    │
└────────┬────────┘
         │ HTTP (port 3000)
         ▼
┌─────────────────┐
│  Nginx (Alpine) │ ◄── Configuration Proxy
└────────┬────────┘
         │ Proxy /api/* vers API Gateway
         ▼
┌─────────────────┐
│  API Gateway    │ (port 8080)
└────────┬────────┘
         │
         ▼
   [Microservices]
```

## Développement Local

### Prérequis
- Node.js 18+
- npm ou yarn

### Installation
```bash
cd front-end-main
npm install
```

### Démarrage en mode développement
```bash
npm start
```

L'application sera accessible sur http://localhost:3000

### Build de production
```bash
npm run build
```

## Déploiement Docker

### Build de l'image Docker
```bash
docker build -t daos-frontend:latest .
```

### Exécution du conteneur
```bash
docker run -p 3000:80 \
  -e API_GATEWAY_HOST=api-gateway \
  -e API_GATEWAY_PORT=8080 \
  daos-frontend:latest
```

### Avec Docker Compose
```bash
docker-compose up front-end-main
```

## Configuration Nginx

Le fichier `nginx/nginx.conf` contient:

### Proxy vers API Gateway
Tous les appels vers `/api/*` sont automatiquement redirigés vers l'API Gateway:

```nginx
location /api/ {
    rewrite ^/api/(.*)$ /$1 break;
    proxy_pass http://api-gateway:8080;
    # Headers et configuration CORS...
}
```

### Health Check
Endpoint `/health` pour Docker health checks:
```nginx
location /health {
    return 200 "OK\n";
}
```

### Routing React
Support du routing côté client:
```nginx
location / {
    try_files $uri $uri/ /index.html;
}
```

## Variables d'Environnement

### Build Time
- `REACT_APP_API_GATEWAY_URL`: URL de l'API Gateway (défaut: http://localhost:8080)

### Runtime (Docker)
- `API_GATEWAY_HOST`: Hostname de l'API Gateway (défaut: api-gateway)
- `API_GATEWAY_PORT`: Port de l'API Gateway (défaut: 8080)

## Endpoints

| Endpoint | Description |
|----------|-------------|
| `/` | Application React principale |
| `/health` | Health check (retourne 200 OK) |
| `/api/*` | Proxy vers API Gateway (rewrite vers `/*`) |

## Exemples d'Utilisation

### Appel API depuis React
```javascript
import axios from 'axios';

// Appel via le proxy Nginx
const response = await axios.get('/api/auth/users');

// Équivalent à: http://api-gateway:8080/auth/users
```

### Test du Proxy
```bash
# Via le front-end (proxy Nginx)
curl http://localhost:3000/api/actuator/health

# Équivalent direct à l'API Gateway
curl http://localhost:8080/actuator/health
```

## Dockerfile Multi-Stage

Le Dockerfile utilise deux stages:

### Stage 1: Build (Node.js)
- Base: `node:18-alpine`
- Install des dépendances
- Build de l'application React (`npm run build`)

### Stage 2: Production (Nginx)
- Base: `nginx:1.25-alpine`
- Copie du build depuis Stage 1
- Configuration Nginx avec proxy
- Taille finale réduite (~25MB vs ~1GB avec Node)

## Troubleshooting

### Le proxy ne fonctionne pas
Vérifier que l'API Gateway est accessible:
```bash
docker exec front-end-main wget -O- http://api-gateway:8080/actuator/health
```

### Erreur CORS
Les headers CORS sont configurés dans `nginx/nginx.conf`. Vérifier:
```nginx
add_header Access-Control-Allow-Origin * always;
```

### Health check échoue
Tester manuellement:
```bash
curl http://localhost:3000/health
# Devrait retourner: OK
```

## Structure du Projet

```
front-end-main/
├── public/              # Fichiers statiques publics
│   └── index.html       # HTML template
├── src/                 # Code source React
│   ├── App.js           # Composant principal
│   ├── App.css          # Styles
│   └── index.js         # Point d'entrée
├── nginx/               # Configuration Nginx
│   ├── nginx.conf       # Configuration du serveur
│   └── docker-entrypoint.sh  # Script de démarrage
├── Dockerfile           # Multi-stage build
├── package.json         # Dépendances Node
└── README.md            # Documentation
```

## Liens Utiles

- React: https://react.dev
- Nginx: https://nginx.org/en/docs/
- Docker Multi-Stage: https://docs.docker.com/build/building/multi-stage/

## Licence

Propriété de l'Université Assane Seck de Ziguinchor (UASZ)
