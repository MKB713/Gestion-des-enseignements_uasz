# ==========================================
# STAGE 1: Build de l'application React/Vite
# ==========================================
FROM node:18-alpine AS build

WORKDIR /app

# Copier les fichiers de dépendances
COPY package.json package-lock.json ./

# Installer les dépendances
RUN npm ci

# Copier le code source
COPY . .

# Build de l'application
RUN npm run build

# ==========================================
# STAGE 2: Servir avec Nginx
# ==========================================
FROM nginx:1.25-alpine

# Copier les fichiers build depuis le stage précédent
COPY --from=build /app/dist /usr/share/nginx/html

# Copier une configuration Nginx personnalisée
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Créer un endpoint de health check
RUN echo '<!DOCTYPE html><html><body><h1>OK</h1></body></html>' > /usr/share/nginx/html/health

# Exposer le port 80
EXPOSE 80

# Démarrer Nginx
CMD ["nginx", "-g", "daemon off;"]
