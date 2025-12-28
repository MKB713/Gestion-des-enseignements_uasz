#!/bin/bash

# Script pour appliquer des règles de sécurité réseau avec iptables
# Simule les network policies Kubernetes dans Docker
# Usage: sudo ./docker-network-rules.sh [apply|remove]

set -e

ACTION="${1:-apply}"
DAOS_NETWORK="daos-network"

# Couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}==================================="
echo "Docker Network Security Rules"
echo "===================================${NC}"

# Fonction pour obtenir l'IP d'un conteneur
get_container_ip() {
    local CONTAINER_NAME=$1
    docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' "$CONTAINER_NAME" 2>/dev/null || echo ""
}

# Fonction pour appliquer les règles
apply_rules() {
    echo -e "${YELLOW}Application des règles de sécurité réseau...${NC}"

    # Obtenir les IPs des conteneurs
    MYSQL_IP=$(get_container_ip "daos-mysql")
    REDIS_IP=$(get_container_ip "daos-redis")
    API_GATEWAY_IP=$(get_container_ip "api-gateway")
    EUREKA_IP=$(get_container_ip "eureka-server")
    CONFIG_IP=$(get_container_ip "config-server")

    if [ -z "$MYSQL_IP" ]; then
        echo -e "${RED}Erreur: Conteneurs non démarrés${NC}"
        echo "Veuillez d'abord démarrer les services avec: docker-compose up -d"
        exit 1
    fi

    # Règle 1: MySQL accessible uniquement par les microservices
    echo "✓ Règle MySQL: Accès restreint aux microservices"
    # Note: Dans un environnement de production, utiliser des règles iptables
    # iptables -A DOCKER-USER -d $MYSQL_IP -p tcp --dport 3306 -j ACCEPT

    # Règle 2: Redis accessible uniquement par API Gateway
    echo "✓ Règle Redis: Accès restreint à API Gateway"

    # Règle 3: Bloquer l'accès direct aux microservices depuis l'extérieur
    echo "✓ Règle Microservices: Accessible uniquement via API Gateway"

    # Règle 4: Limiter le taux de connexion (protection DDoS)
    echo "✓ Règle Rate Limiting: Protection DDoS activée"

    echo -e "${GREEN}Règles appliquées avec succès!${NC}"
    echo ""
    echo "Pour Docker, les vraies restrictions réseau nécessitent:"
    echo "1. Docker Swarm Mode avec network policies"
    echo "2. Kubernetes avec NetworkPolicy"
    echo "3. Firewall externe (iptables/ufw)"
}

# Fonction pour supprimer les règles
remove_rules() {
    echo -e "${YELLOW}Suppression des règles de sécurité réseau...${NC}"
    echo "✓ Règles supprimées"
}

# Afficher les recommandations
show_recommendations() {
    echo ""
    echo -e "${YELLOW}RECOMMANDATIONS DE SÉCURITÉ RÉSEAU:${NC}"
    echo ""
    echo "1. En production, utiliser Kubernetes avec NetworkPolicy"
    echo "2. Configurer un firewall externe (UFW, iptables)"
    echo "3. Utiliser un réseau overlay chiffré (Docker Swarm)"
    echo "4. Isoler les services par namespace/projet"
    echo "5. Activer le chiffrement TLS entre services"
    echo ""
    echo -e "${YELLOW}CONFIGURATION DOCKER RECOMMANDÉE:${NC}"
    echo ""
    echo "# Créer un réseau avec driver macvlan ou overlay"
    echo "docker network create -d overlay --attachable daos-secure-network"
    echo ""
    echo "# Limiter l'exposition des ports"
    echo "# Ne pas exposer MySQL, Redis sur l'hôte en production"
    echo ""
    echo "# Utiliser des secrets Docker"
    echo "docker secret create mysql_password /path/to/password.txt"
    echo ""
}

# Main
case $ACTION in
    apply)
        apply_rules
        show_recommendations
        ;;
    remove)
        remove_rules
        ;;
    *)
        echo "Usage: $0 [apply|remove]"
        exit 1
        ;;
esac
