#!/bin/bash

# Script d'initialisation de Vault avec les secrets DAOS
# Usage: ./init-vault.sh

set -e

VAULT_ADDR="http://localhost:8200"
VAULT_TOKEN="${VAULT_ROOT_TOKEN:-daos-root-token}"

export VAULT_ADDR
export VAULT_TOKEN

echo "==================================="
echo "Initialisation de Vault pour DAOS"
echo "==================================="

# Attendre que Vault soit prêt
echo "Attente de Vault..."
until vault status 2>/dev/null; do
    echo "Vault n'est pas encore prêt, attente..."
    sleep 2
done

echo "✓ Vault est prêt"

# Activer le moteur de secrets KV v2
echo ""
echo "Activation du moteur de secrets KV v2..."
vault secrets enable -path=secret kv-v2 2>/dev/null || echo "KV v2 déjà activé"

# Créer les secrets pour chaque microservice

echo ""
echo "Création des secrets pour auth-service..."
vault kv put secret/auth-service \
    jwt.secret="5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437" \
    jwt.expiration="86400000" \
    spring.datasource.password="root" \
    spring.mail.password="change-me"

echo ""
echo "Création des secrets pour les autres microservices..."
for SERVICE in enseignant-service maquette-service choix-enseignement-service emploi-temps-service deroulement-enseignement-service; do
    vault kv put secret/$SERVICE \
        spring.datasource.password="root"
    echo "✓ Secrets créés pour $SERVICE"
done

echo ""
echo "Création des secrets d'infrastructure..."
vault kv put secret/eureka-server \
    eureka.security.username="admin" \
    eureka.security.password="admin-change-in-prod"

vault kv put secret/config-server \
    spring.security.user.name="configadmin" \
    spring.security.user.password="configadmin-change-in-prod" \
    encrypt.key="daos-secret-encryption-key-change-in-production"

vault kv put secret/mysql \
    MYSQL_ROOT_PASSWORD="root-password-change-in-prod"

vault kv put secret/redis \
    REDIS_PASSWORD="redis-password-change-in-prod"

echo ""
echo "Création de la policy d'accès pour les microservices..."
vault policy write daos-microservices - <<EOF
# Politique d'accès pour les microservices DAOS
path "secret/data/auth-service" {
  capabilities = ["read"]
}

path "secret/data/enseignant-service" {
  capabilities = ["read"]
}

path "secret/data/maquette-service" {
  capabilities = ["read"]
}

path "secret/data/choix-enseignement-service" {
  capabilities = ["read"]
}

path "secret/data/emploi-temps-service" {
  capabilities = ["read"]
}

path "secret/data/deroulement-enseignement-service" {
  capabilities = ["read"]
}

path "secret/data/eureka-server" {
  capabilities = ["read"]
}

path "secret/data/config-server" {
  capabilities = ["read"]
}
EOF

echo ""
echo "Activation de l'authentification AppRole..."
vault auth enable approle 2>/dev/null || echo "AppRole déjà activé"

echo ""
echo "Création du role pour les microservices..."
vault write auth/approle/role/daos-microservices \
    token_policies="daos-microservices" \
    token_ttl=1h \
    token_max_ttl=4h

echo ""
echo "Récupération des credentials AppRole..."
ROLE_ID=$(vault read -field=role_id auth/approle/role/daos-microservices/role-id)
SECRET_ID=$(vault write -field=secret_id -f auth/approle/role/daos-microservices/secret-id)

echo ""
echo "==================================="
echo "Initialisation terminée!"
echo "==================================="
echo ""
echo "Vault UI: http://localhost:8200"
echo "Token d'accès: $VAULT_TOKEN"
echo ""
echo "Credentials AppRole pour les microservices:"
echo "VAULT_ROLE_ID=$ROLE_ID"
echo "VAULT_SECRET_ID=$SECRET_ID"
echo ""
echo "Ajouter ces variables dans .env:"
echo "VAULT_ADDR=http://vault:8200"
echo "VAULT_ROLE_ID=$ROLE_ID"
echo "VAULT_SECRET_ID=$SECRET_ID"
echo ""
echo "Pour lire un secret:"
echo "vault kv get secret/auth-service"
echo ""
