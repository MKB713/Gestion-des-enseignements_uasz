#!/bin/bash

# Script d'initialisation de SonarQube
# Configure SonarQube avec les projets DAOS
# Usage: ./init-sonarqube.sh

set -e

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Variables
SONAR_URL="${SONAR_HOST_URL:-http://localhost:9000}"
SONAR_ADMIN_USER="${SONAR_ADMIN_USER:-admin}"
SONAR_ADMIN_PASS="${SONAR_ADMIN_PASS:-admin}"
SONAR_TOKEN=""

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}   INITIALISATION DE SONARQUBE${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Attendre que SonarQube soit prêt
wait_for_sonarqube() {
    echo -e "${YELLOW}Attente du démarrage de SonarQube...${NC}"

    local MAX_ATTEMPTS=30
    local ATTEMPT=0

    until curl -sf "$SONAR_URL/api/system/status" | grep -q '"status":"UP"' || [ $ATTEMPT -eq $MAX_ATTEMPTS ]; do
        ATTEMPT=$((ATTEMPT+1))
        echo "Tentative $ATTEMPT/$MAX_ATTEMPTS..."
        sleep 10
    done

    if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
        echo -e "${RED}Erreur: SonarQube ne démarre pas${NC}"
        exit 1
    fi

    echo -e "${GREEN}✓ SonarQube est prêt${NC}"
}

# Changer le mot de passe admin par défaut
change_admin_password() {
    echo -e "${YELLOW}Changement du mot de passe administrateur...${NC}"

    # Vérifier si le mot de passe est toujours "admin"
    if curl -sf -u "$SONAR_ADMIN_USER:admin" "$SONAR_URL/api/authentication/validate" > /dev/null 2>&1; then
        echo "Mot de passe par défaut détecté, changement en cours..."

        curl -X POST -u "$SONAR_ADMIN_USER:admin" \
            "$SONAR_URL/api/users/change_password" \
            -d "login=$SONAR_ADMIN_USER" \
            -d "previousPassword=admin" \
            -d "password=$SONAR_ADMIN_PASS" > /dev/null 2>&1

        echo -e "${GREEN}✓ Mot de passe administrateur changé${NC}"
    else
        echo "Mot de passe déjà changé, passage à l'étape suivante..."
    fi
}

# Créer un token d'authentification
create_token() {
    echo -e "${YELLOW}Création d'un token d'authentification...${NC}"

    RESPONSE=$(curl -X POST -u "$SONAR_ADMIN_USER:$SONAR_ADMIN_PASS" \
        "$SONAR_URL/api/user_tokens/generate" \
        -d "name=daos-ci-token" 2>/dev/null)

    SONAR_TOKEN=$(echo "$RESPONSE" | grep -o '"token":"[^"]*' | sed 's/"token":"//')

    if [ -n "$SONAR_TOKEN" ]; then
        echo -e "${GREEN}✓ Token créé avec succès${NC}"
        echo ""
        echo -e "${BLUE}IMPORTANT: Sauvegardez ce token dans les variables GitLab CI${NC}"
        echo -e "${BLUE}Variable: SONAR_TOKEN${NC}"
        echo -e "${BLUE}Valeur: $SONAR_TOKEN${NC}"
        echo ""
    else
        echo -e "${YELLOW}⚠ Token existant ou erreur de création${NC}"
    fi
}

# Créer les projets SonarQube
create_projects() {
    echo -e "${YELLOW}Création des projets SonarQube...${NC}"

    PROJECTS=(
        "daos-config-server:Config Server"
        "daos-eureka-server:Eureka Server"
        "daos-api-gateway:API Gateway"
        "daos-auth-service:Auth Service"
        "daos-enseignant-service:Enseignant Service"
        "daos-maquette-service:Maquette Service"
        "daos-choix-enseignement-service:Choix Enseignement Service"
        "daos-deroulement-enseignement-service:Deroulement Enseignement Service"
        "daos-emploi-temps-service:Emploi Temps Service"
        "daos-frontend:Frontend"
    )

    for PROJECT in "${PROJECTS[@]}"; do
        PROJECT_KEY=$(echo "$PROJECT" | cut -d: -f1)
        PROJECT_NAME=$(echo "$PROJECT" | cut -d: -f2)

        echo "Création du projet: $PROJECT_NAME..."

        curl -X POST -u "$SONAR_ADMIN_USER:$SONAR_ADMIN_PASS" \
            "$SONAR_URL/api/projects/create" \
            -d "project=$PROJECT_KEY" \
            -d "name=$PROJECT_NAME" > /dev/null 2>&1

        echo -e "${GREEN}✓ Projet $PROJECT_NAME créé${NC}"
    done
}

# Configurer les Quality Gates
configure_quality_gates() {
    echo -e "${YELLOW}Configuration des Quality Gates...${NC}"

    # Créer un Quality Gate personnalisé pour DAOS
    GATE_RESPONSE=$(curl -X POST -u "$SONAR_ADMIN_USER:$SONAR_ADMIN_PASS" \
        "$SONAR_URL/api/qualitygates/create" \
        -d "name=DAOS Quality Gate" 2>/dev/null)

    GATE_ID=$(echo "$GATE_RESPONSE" | grep -o '"id":[0-9]*' | sed 's/"id"://')

    if [ -n "$GATE_ID" ]; then
        echo "Quality Gate créé avec ID: $GATE_ID"

        # Ajouter des conditions
        # Couverture de code > 80%
        curl -X POST -u "$SONAR_ADMIN_USER:$SONAR_ADMIN_PASS" \
            "$SONAR_URL/api/qualitygates/create_condition" \
            -d "gateId=$GATE_ID" \
            -d "metric=coverage" \
            -d "op=LT" \
            -d "error=80" > /dev/null 2>&1

        # Bugs > 0
        curl -X POST -u "$SONAR_ADMIN_USER:$SONAR_ADMIN_PASS" \
            "$SONAR_URL/api/qualitygates/create_condition" \
            -d "gateId=$GATE_ID" \
            -d "metric=bugs" \
            -d "op=GT" \
            -d "error=0" > /dev/null 2>&1

        # Vulnérabilités > 0
        curl -X POST -u "$SONAR_ADMIN_USER:$SONAR_ADMIN_PASS" \
            "$SONAR_URL/api/qualitygates/create_condition" \
            -d "gateId=$GATE_ID" \
            -d "metric=vulnerabilities" \
            -d "op=GT" \
            -d "error=0" > /dev/null 2>&1

        # Code Smells > 10
        curl -X POST -u "$SONAR_ADMIN_USER:$SONAR_ADMIN_PASS" \
            "$SONAR_URL/api/qualitygates/create_condition" \
            -d "gateId=$GATE_ID" \
            -d "metric=code_smells" \
            -d "op=GT" \
            -d "error=10" > /dev/null 2>&1

        # Définir comme Quality Gate par défaut
        curl -X POST -u "$SONAR_ADMIN_USER:$SONAR_ADMIN_PASS" \
            "$SONAR_URL/api/qualitygates/set_as_default" \
            -d "id=$GATE_ID" > /dev/null 2>&1

        echo -e "${GREEN}✓ Quality Gate configuré${NC}"
    else
        echo -e "${YELLOW}⚠ Quality Gate existant ou erreur de création${NC}"
    fi
}

# Installer les plugins recommandés
install_plugins() {
    echo -e "${YELLOW}Installation des plugins recommandés...${NC}"

    echo "Note: Les plugins doivent être installés manuellement via l'interface web"
    echo "Plugins recommandés:"
    echo "  - SonarJava"
    echo "  - SonarJS"
    echo "  - SonarSecurity"
    echo "  - Checkstyle"
    echo "  - PMD"
    echo "  - SpotBugs"
    echo ""
}

# Afficher les informations finales
display_summary() {
    echo ""
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}   SONARQUBE INITIALISÉ AVEC SUCCÈS${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo -e "${YELLOW}Informations de connexion:${NC}"
    echo "  URL: $SONAR_URL"
    echo "  Username: $SONAR_ADMIN_USER"
    echo "  Password: $SONAR_ADMIN_PASS"
    echo ""
    echo -e "${YELLOW}Token pour GitLab CI:${NC}"
    if [ -n "$SONAR_TOKEN" ]; then
        echo "  $SONAR_TOKEN"
    else
        echo "  (Créer manuellement dans SonarQube)"
    fi
    echo ""
    echo -e "${YELLOW}Prochaines étapes:${NC}"
    echo "  1. Ajouter le SONAR_TOKEN dans les variables GitLab CI"
    echo "  2. Ajouter SONAR_HOST_URL=$SONAR_URL dans GitLab CI"
    echo "  3. Lancer le pipeline GitLab CI"
    echo ""
    echo -e "${YELLOW}Accéder à SonarQube:${NC}"
    echo "  $SONAR_URL"
    echo ""
}

# Main
main() {
    wait_for_sonarqube
    change_admin_password
    create_token
    create_projects
    configure_quality_gates
    install_plugins
    display_summary
}

# Exécuter
main
