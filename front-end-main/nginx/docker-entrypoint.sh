#!/bin/sh
set -e

# Ce script permet d'injecter les variables d'environnement au runtime
# dans les fichiers JavaScript buildés

echo "Injecting runtime environment variables..."

# Remplacer les variables d'environnement dans les fichiers JS
if [ -n "$API_GATEWAY_HOST" ] && [ -n "$API_GATEWAY_PORT" ]; then
    API_GATEWAY_URL="http://${API_GATEWAY_HOST}:${API_GATEWAY_PORT}"
    echo "Setting API Gateway URL to: $API_GATEWAY_URL"

    # Trouver tous les fichiers JS dans le build et remplacer les variables
    find /usr/share/nginx/html -type f -name "*.js" -exec sed -i "s|REACT_APP_API_GATEWAY_URL_PLACEHOLDER|${API_GATEWAY_URL}|g" {} \;
fi

# Afficher la configuration Nginx pour debug
echo "Nginx configuration:"
cat /etc/nginx/conf.d/default.conf

echo "Starting Nginx..."

# Exécuter la commande passée en argument (nginx)
exec "$@"
