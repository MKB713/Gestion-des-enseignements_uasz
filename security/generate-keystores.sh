#!/bin/bash

# Script pour générer les keystores et certificats SSL/TLS pour DAOS
# Usage: ./generate-keystores.sh

set -e

CERT_DIR="./certificates"
KEYSTORE_PASSWORD="daos-keystore-password"
VALIDITY_DAYS=365

echo "==================================="
echo "Génération des certificats SSL/TLS"
echo "==================================="

# Créer le répertoire certificates s'il n'existe pas
mkdir -p "$CERT_DIR"

# Fonction pour générer un keystore pour un service
generate_keystore() {
    local SERVICE_NAME=$1
    local SERVICE_PORT=$2
    local KEYSTORE_FILE="$CERT_DIR/${SERVICE_NAME}-keystore.p12"

    echo "Génération du keystore pour $SERVICE_NAME..."

    keytool -genkeypair \
        -alias "$SERVICE_NAME" \
        -keyalg RSA \
        -keysize 2048 \
        -storetype PKCS12 \
        -keystore "$KEYSTORE_FILE" \
        -storepass "$KEYSTORE_PASSWORD" \
        -keypass "$KEYSTORE_PASSWORD" \
        -validity "$VALIDITY_DAYS" \
        -dname "CN=$SERVICE_NAME,OU=DAOS,O=UASZ,L=Ziguinchor,ST=Senegal,C=SN" \
        -ext "SAN=DNS:$SERVICE_NAME,DNS:localhost,IP:127.0.0.1"

    echo "✓ Keystore généré: $KEYSTORE_FILE"
}

# Générer les keystores pour chaque service
generate_keystore "eureka-server" "8761"
generate_keystore "config-server" "8888"
generate_keystore "api-gateway" "8080"
generate_keystore "auth-service" "8081"
generate_keystore "enseignant-service" "8082"
generate_keystore "maquette-service" "8083"
generate_keystore "choix-enseignement-service" "8084"
generate_keystore "emploi-temps-service" "8085"
generate_keystore "deroulement-enseignement-service" "8086"

# Générer un truststore commun
echo ""
echo "Génération du truststore commun..."
TRUSTSTORE_FILE="$CERT_DIR/truststore.p12"

# Exporter et importer les certificats dans le truststore
for SERVICE in eureka-server config-server api-gateway auth-service enseignant-service maquette-service choix-enseignement-service emploi-temps-service deroulement-enseignement-service; do
    CERT_FILE="$CERT_DIR/${SERVICE}.crt"
    KEYSTORE_FILE="$CERT_DIR/${SERVICE}-keystore.p12"

    # Exporter le certificat
    keytool -exportcert \
        -alias "$SERVICE" \
        -keystore "$KEYSTORE_FILE" \
        -storepass "$KEYSTORE_PASSWORD" \
        -file "$CERT_FILE" \
        -rfc

    # Importer dans le truststore
    keytool -importcert \
        -alias "$SERVICE" \
        -file "$CERT_FILE" \
        -keystore "$TRUSTSTORE_FILE" \
        -storepass "$KEYSTORE_PASSWORD" \
        -noprompt

    echo "✓ Certificat $SERVICE ajouté au truststore"
done

echo ""
echo "==================================="
echo "Génération terminée!"
echo "==================================="
echo ""
echo "Fichiers générés dans: $CERT_DIR"
echo ""
echo "Keystores:"
ls -lh "$CERT_DIR"/*.p12
echo ""
echo "IMPORTANT:"
echo "1. Copier le mot de passe du keystore dans .env:"
echo "   KEYSTORE_PASSWORD=$KEYSTORE_PASSWORD"
echo ""
echo "2. En production, changer le mot de passe:"
echo "   keytool -storepasswd -keystore <keystore-file>"
echo ""
echo "3. Ne JAMAIS committer les keystores dans Git!"
echo ""
