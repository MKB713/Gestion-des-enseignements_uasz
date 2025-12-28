# ==========================================
# STAGE 1: Build de l'application React
# ==========================================
FROM node:18-alpine AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier package.json et package-lock.json
COPY package*.json ./

# Installer les dépendances
RUN npm install --production

# Copier le code source
COPY public/ ./public/
COPY src/ ./src/

# Arguments de build pour les variables d'environnement
ARG REACT_APP_API_GATEWAY_URL
ENV REACT_APP_API_GATEWAY_URL=${REACT_APP_API_GATEWAY_URL}

# Build de l'application React
RUN npm run build

# ==========================================
# STAGE 2: Servir avec Nginx
# ==========================================
FROM nginx:1.25-alpine

# Copier les fichiers build depuis le stage précédent
COPY --from=build /app/build /usr/share/nginx/html

# Copier la configuration Nginx personnalisée
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

# Créer un endpoint de health check
RUN echo '<!DOCTYPE html><html><body><h1>OK</h1></body></html>' > /usr/share/nginx/html/health

# Exposer le port 80
EXPOSE 80

# Script de démarrage pour injecter les variables d'environnement au runtime
COPY nginx/docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Démarrer Nginx
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
