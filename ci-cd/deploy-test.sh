#!/bin/bash

# Script de déploiement automatique en environnement de TEST
# Utilisé par le pipeline CI/CD GitLab
# Usage: ./deploy-test.sh

set -e

# Couleurs pour les logs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Variables
DEPLOY_DIR="/opt/daos"
BACKUP_DIR="/opt/daos/backups"
LOG_FILE="/var/log/daos-deploy.log"
TIMESTAMP=$(date +%Y%m%d-%H%M%S)

# Fonctions de logging
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1" | tee -a "$LOG_FILE"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1" | tee -a "$LOG_FILE"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a "$LOG_FILE"
}

log_step() {
    echo -e "${BLUE}[STEP]${NC} $1" | tee -a "$LOG_FILE"
}

# Vérifier les prérequis
check_prerequisites() {
    log_step "Vérification des prérequis..."

    if ! command -v docker &> /dev/null; then
        log_error "Docker n'est pas installé"
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose n'est pas installé"
        exit 1
    fi

    log_info "Tous les prérequis sont satisfaits"
}

# Créer une sauvegarde
create_backup() {
    log_step "Création d'une sauvegarde..."

    mkdir -p "$BACKUP_DIR"

    # Sauvegarder l'état actuel
    if [ -f "$DEPLOY_DIR/docker-compose.yml" ]; then
        docker-compose -f "$DEPLOY_DIR/docker-compose.yml" ps > "$BACKUP_DIR/containers-state-$TIMESTAMP.txt"
        log_info "État des conteneurs sauvegardé: $BACKUP_DIR/containers-state-$TIMESTAMP.txt"
    fi

    # Sauvegarder les variables d'environnement
    if [ -f "$DEPLOY_DIR/.env" ]; then
        cp "$DEPLOY_DIR/.env" "$BACKUP_DIR/.env-$TIMESTAMP"
        log_info "Variables d'environnement sauvegardées"
    fi
}

# Arrêter les services actuels
stop_services() {
    log_step "Arrêt des services actuels..."

    cd "$DEPLOY_DIR"

    if docker-compose ps | grep -q "Up"; then
        docker-compose down
        log_info "Services arrêtés avec succès"
    else
        log_warn "Aucun service en cours d'exécution"
    fi
}

# Récupérer les nouvelles images
pull_images() {
    log_step "Récupération des nouvelles images Docker..."

    cd "$DEPLOY_DIR"

    # Login au registry
    if [ -n "$CI_REGISTRY_PASSWORD" ] && [ -n "$CI_REGISTRY_USER" ]; then
        echo "$CI_REGISTRY_PASSWORD" | docker login -u "$CI_REGISTRY_USER" --password-stdin "$CI_REGISTRY"
        log_info "Authentification au registry réussie"
    fi

    # Pull des images
    docker-compose pull
    log_info "Images récupérées avec succès"
}

# Nettoyer les anciennes images
cleanup_old_images() {
    log_step "Nettoyage des anciennes images..."

    # Supprimer les images non utilisées
    docker image prune -f

    log_info "Nettoyage terminé"
}

# Démarrer les nouveaux services
start_services() {
    log_step "Démarrage des nouveaux services..."

    cd "$DEPLOY_DIR"

    # Démarrer les services dans l'ordre
    docker-compose up -d

    log_info "Services démarrés"
}

# Attendre que les services soient prêts
wait_for_services() {
    log_step "Attente de la disponibilité des services..."

    local MAX_ATTEMPTS=30
    local ATTEMPT=0

    # Attendre Config Server
    log_info "Attente de Config Server..."
    until curl -sf http://localhost:8888/actuator/health > /dev/null || [ $ATTEMPT -eq $MAX_ATTEMPTS ]; do
        ATTEMPT=$((ATTEMPT+1))
        log_warn "Tentative $ATTEMPT/$MAX_ATTEMPTS..."
        sleep 5
    done

    if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
        log_error "Config Server non disponible après $MAX_ATTEMPTS tentatives"
        return 1
    fi

    log_info "Config Server prêt"

    # Attendre Eureka Server
    ATTEMPT=0
    log_info "Attente de Eureka Server..."
    until curl -sf http://localhost:8761/actuator/health > /dev/null || [ $ATTEMPT -eq $MAX_ATTEMPTS ]; do
        ATTEMPT=$((ATTEMPT+1))
        log_warn "Tentative $ATTEMPT/$MAX_ATTEMPTS..."
        sleep 5
    done

    if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
        log_error "Eureka Server non disponible après $MAX_ATTEMPTS tentatives"
        return 1
    fi

    log_info "Eureka Server prêt"

    # Attendre API Gateway
    ATTEMPT=0
    log_info "Attente de API Gateway..."
    until curl -sf http://localhost:8080/actuator/health > /dev/null || [ $ATTEMPT -eq $MAX_ATTEMPTS ]; do
        ATTEMPT=$((ATTEMPT+1))
        log_warn "Tentative $ATTEMPT/$MAX_ATTEMPTS..."
        sleep 5
    done

    if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
        log_error "API Gateway non disponible après $MAX_ATTEMPTS tentatives"
        return 1
    fi

    log_info "API Gateway prêt"
}

# Vérifier la santé des services
health_check() {
    log_step "Vérification de la santé des services..."

    local FAILED=0

    # Vérifier Config Server
    if curl -sf http://localhost:8888/actuator/health > /dev/null; then
        log_info "✓ Config Server: Healthy"
    else
        log_error "✗ Config Server: Unhealthy"
        FAILED=1
    fi

    # Vérifier Eureka Server
    if curl -sf http://localhost:8761/actuator/health > /dev/null; then
        log_info "✓ Eureka Server: Healthy"
    else
        log_error "✗ Eureka Server: Unhealthy"
        FAILED=1
    fi

    # Vérifier API Gateway
    if curl -sf http://localhost:8080/actuator/health > /dev/null; then
        log_info "✓ API Gateway: Healthy"
    else
        log_error "✗ API Gateway: Unhealthy"
        FAILED=1
    fi

    # Vérifier MySQL
    if docker exec daos-mysql mysqladmin ping -h localhost -u root -proot > /dev/null 2>&1; then
        log_info "✓ MySQL: Healthy"
    else
        log_error "✗ MySQL: Unhealthy"
        FAILED=1
    fi

    # Vérifier Redis
    if docker exec daos-redis redis-cli ping > /dev/null 2>&1; then
        log_info "✓ Redis: Healthy"
    else
        log_error "✗ Redis: Unhealthy"
        FAILED=1
    fi

    if [ $FAILED -eq 1 ]; then
        log_error "Certains services ne sont pas en bonne santé"
        return 1
    fi

    log_info "Tous les services sont en bonne santé"
}

# Rollback en cas d'échec
rollback() {
    log_error "Échec du déploiement. Rollback en cours..."

    cd "$DEPLOY_DIR"

    # Arrêter les nouveaux services
    docker-compose down

    # Restaurer l'ancienne configuration si disponible
    if [ -f "$BACKUP_DIR/.env-$TIMESTAMP" ]; then
        cp "$BACKUP_DIR/.env-$TIMESTAMP" "$DEPLOY_DIR/.env"
    fi

    # Redémarrer avec les anciennes images
    docker-compose up -d

    log_warn "Rollback terminé. L'ancienne version est restaurée."
}

# Afficher le résumé
display_summary() {
    echo ""
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}   DÉPLOIEMENT TERMINÉ AVEC SUCCÈS${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo -e "Timestamp: ${BLUE}$TIMESTAMP${NC}"
    echo -e "Environnement: ${BLUE}TEST${NC}"
    echo ""
    echo -e "${YELLOW}Services disponibles:${NC}"
    echo "  - Eureka Server: http://localhost:8761"
    echo "  - Config Server: http://localhost:8888"
    echo "  - API Gateway: http://localhost:8080"
    echo "  - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo ""
    echo -e "${YELLOW}Logs:${NC}"
    echo "  - Fichier de log: $LOG_FILE"
    echo "  - Voir les logs: docker-compose logs -f"
    echo ""
}

# Main
main() {
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}   DÉPLOIEMENT ENVIRONNEMENT TEST${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""

    log_info "Début du déploiement à $TIMESTAMP"

    # Exécution des étapes
    check_prerequisites || exit 1
    create_backup || exit 1
    stop_services || exit 1
    pull_images || exit 1
    cleanup_old_images || true
    start_services || { rollback; exit 1; }
    wait_for_services || { rollback; exit 1; }
    health_check || { rollback; exit 1; }

    display_summary

    log_info "Déploiement terminé avec succès à $(date +%Y%m%d-%H%M%S)"

    exit 0
}

# Exécuter le script
main
