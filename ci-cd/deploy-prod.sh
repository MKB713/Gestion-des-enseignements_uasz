#!/bin/bash

# Script de déploiement automatique en environnement de PRODUCTION
# Utilisé par le pipeline CI/CD GitLab
# Usage: ./deploy-prod.sh
# ATTENTION: Ce script nécessite une validation manuelle

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
LOG_FILE="/var/log/daos-deploy-prod.log"
TIMESTAMP=$(date +%Y%m%d-%H%M%S)
ROLLBACK_ENABLED=true

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

# Confirmation utilisateur
confirm_deployment() {
    echo -e "${YELLOW}========================================${NC}"
    echo -e "${YELLOW}   DÉPLOIEMENT EN PRODUCTION${NC}"
    echo -e "${YELLOW}========================================${NC}"
    echo ""
    echo -e "${RED}ATTENTION: Vous êtes sur le point de déployer en PRODUCTION${NC}"
    echo ""
    echo "Timestamp: $TIMESTAMP"
    echo "Environnement: PRODUCTION"
    echo ""
    read -p "Êtes-vous sûr de vouloir continuer? (oui/non): " -r
    echo

    if [[ ! $REPLY =~ ^[Oo][Uu][Ii]$ ]]; then
        log_warn "Déploiement annulé par l'utilisateur"
        exit 0
    fi

    log_info "Confirmation reçue. Début du déploiement..."
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

    # Vérifier l'espace disque
    AVAILABLE_SPACE=$(df -BG /opt | tail -1 | awk '{print $4}' | sed 's/G//')
    if [ "$AVAILABLE_SPACE" -lt 10 ]; then
        log_error "Espace disque insuffisant: ${AVAILABLE_SPACE}G (minimum 10G requis)"
        exit 1
    fi

    log_info "Tous les prérequis sont satisfaits"
}

# Créer une sauvegarde complète
create_backup() {
    log_step "Création d'une sauvegarde complète..."

    mkdir -p "$BACKUP_DIR"

    # Sauvegarder l'état des conteneurs
    if [ -f "$DEPLOY_DIR/docker-compose.yml" ]; then
        docker-compose -f "$DEPLOY_DIR/docker-compose.yml" ps > "$BACKUP_DIR/containers-state-$TIMESTAMP.txt"
        log_info "État des conteneurs sauvegardé"
    fi

    # Sauvegarder les variables d'environnement
    if [ -f "$DEPLOY_DIR/.env" ]; then
        cp "$DEPLOY_DIR/.env" "$BACKUP_DIR/.env-$TIMESTAMP"
        log_info "Variables d'environnement sauvegardées"
    fi

    # Sauvegarder la base de données
    log_info "Sauvegarde de la base de données MySQL..."
    docker exec daos-mysql mysqldump -u root -proot --all-databases > "$BACKUP_DIR/mysql-backup-$TIMESTAMP.sql"
    if [ $? -eq 0 ]; then
        log_info "Base de données sauvegardée: $BACKUP_DIR/mysql-backup-$TIMESTAMP.sql"
    else
        log_error "Échec de la sauvegarde de la base de données"
        exit 1
    fi

    # Compresser la sauvegarde
    tar -czf "$BACKUP_DIR/backup-$TIMESTAMP.tar.gz" -C "$BACKUP_DIR" \
        "containers-state-$TIMESTAMP.txt" \
        ".env-$TIMESTAMP" \
        "mysql-backup-$TIMESTAMP.sql"

    log_info "Sauvegarde complète créée: $BACKUP_DIR/backup-$TIMESTAMP.tar.gz"
}

# Rolling update - Mise à jour progressive
rolling_update() {
    log_step "Démarrage de la mise à jour progressive (Rolling Update)..."

    cd "$DEPLOY_DIR"

    # Login au registry
    if [ -n "$CI_REGISTRY_PASSWORD" ] && [ -n "$CI_REGISTRY_USER" ]; then
        echo "$CI_REGISTRY_PASSWORD" | docker login -u "$CI_REGISTRY_USER" --password-stdin "$CI_REGISTRY"
        log_info "Authentification au registry réussie"
    fi

    # Pull des nouvelles images
    log_info "Récupération des nouvelles images..."
    docker-compose pull

    # Mise à jour service par service
    SERVICES=(
        "config-server"
        "eureka-server"
        "api-gateway"
        "auth-service"
        "enseignant-service"
        "maquette-service"
        "choix-enseignement-service"
        "deroulement-enseignement-service"
        "emploi-temps-service"
        "frontend"
    )

    for SERVICE in "${SERVICES[@]}"; do
        log_info "Mise à jour de $SERVICE..."

        # Créer un nouveau conteneur sans arrêter l'ancien
        docker-compose up -d --no-deps --build "$SERVICE"

        # Attendre que le nouveau conteneur soit healthy
        sleep 10

        # Vérifier la santé du service
        if docker ps | grep -q "$SERVICE"; then
            log_info "✓ $SERVICE mis à jour avec succès"
        else
            log_error "✗ Échec de la mise à jour de $SERVICE"
            return 1
        fi

        # Petite pause entre les services
        sleep 5
    done

    log_info "Rolling update terminé"
}

# Smoke tests - Tests de fumée rapides
smoke_tests() {
    log_step "Exécution des smoke tests..."

    local FAILED=0

    # Test Config Server
    if curl -sf http://localhost:8888/actuator/health | grep -q '"status":"UP"'; then
        log_info "✓ Config Server: OK"
    else
        log_error "✗ Config Server: FAILED"
        FAILED=1
    fi

    # Test Eureka Server
    if curl -sf http://localhost:8761/actuator/health | grep -q '"status":"UP"'; then
        log_info "✓ Eureka Server: OK"
    else
        log_error "✗ Eureka Server: FAILED"
        FAILED=1
    fi

    # Test API Gateway
    if curl -sf http://localhost:8080/actuator/health | grep -q '"status":"UP"'; then
        log_info "✓ API Gateway: OK"
    else
        log_error "✗ API Gateway: FAILED"
        FAILED=1
    fi

    # Test d'authentification
    AUTH_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"admin"}')

    if echo "$AUTH_RESPONSE" | grep -q "token"; then
        log_info "✓ Authentification: OK"
    else
        log_warn "⚠ Authentification: Vérification manuelle requise"
    fi

    # Test de la base de données
    if docker exec daos-mysql mysqladmin ping -h localhost -u root -proot > /dev/null 2>&1; then
        log_info "✓ MySQL: OK"
    else
        log_error "✗ MySQL: FAILED"
        FAILED=1
    fi

    if [ $FAILED -eq 1 ]; then
        log_error "Certains smoke tests ont échoué"
        return 1
    fi

    log_info "Tous les smoke tests sont passés"
}

# Rollback complet
rollback() {
    log_error "ROLLBACK EN COURS..."

    cd "$DEPLOY_DIR"

    # Arrêter tous les services
    docker-compose down

    # Restaurer les variables d'environnement
    if [ -f "$BACKUP_DIR/.env-$TIMESTAMP" ]; then
        cp "$BACKUP_DIR/.env-$TIMESTAMP" "$DEPLOY_DIR/.env"
        log_info "Variables d'environnement restaurées"
    fi

    # Restaurer la base de données
    if [ -f "$BACKUP_DIR/mysql-backup-$TIMESTAMP.sql" ]; then
        log_info "Restauration de la base de données..."
        docker exec -i daos-mysql mysql -u root -proot < "$BACKUP_DIR/mysql-backup-$TIMESTAMP.sql"
        log_info "Base de données restaurée"
    fi

    # Redémarrer avec les anciennes images
    docker-compose up -d

    # Attendre que les services redémarrent
    sleep 30

    log_warn "ROLLBACK TERMINÉ. L'ancienne version est restaurée."
    log_warn "Veuillez vérifier l'état des services."
}

# Monitorer les services après déploiement
post_deployment_monitoring() {
    log_step "Surveillance post-déploiement (5 minutes)..."

    local DURATION=300  # 5 minutes
    local INTERVAL=30   # Vérifier toutes les 30 secondes
    local ELAPSED=0

    while [ $ELAPSED -lt $DURATION ]; do
        if ! smoke_tests > /dev/null 2>&1; then
            log_error "Problème détecté pendant la surveillance"
            return 1
        fi

        sleep $INTERVAL
        ELAPSED=$((ELAPSED + INTERVAL))
        log_info "Surveillance: $ELAPSED/$DURATION secondes..."
    done

    log_info "Surveillance terminée. Aucun problème détecté."
}

# Nettoyer les anciennes sauvegardes (garder les 10 dernières)
cleanup_old_backups() {
    log_step "Nettoyage des anciennes sauvegardes..."

    cd "$BACKUP_DIR"

    # Garder seulement les 10 dernières sauvegardes
    ls -t backup-*.tar.gz | tail -n +11 | xargs -r rm

    log_info "Anciennes sauvegardes nettoyées"
}

# Afficher le résumé
display_summary() {
    echo ""
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}   DÉPLOIEMENT PRODUCTION RÉUSSI${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo -e "Timestamp: ${BLUE}$TIMESTAMP${NC}"
    echo -e "Environnement: ${BLUE}PRODUCTION${NC}"
    echo -e "Sauvegarde: ${BLUE}$BACKUP_DIR/backup-$TIMESTAMP.tar.gz${NC}"
    echo ""
    echo -e "${YELLOW}Services déployés:${NC}"
    docker-compose ps
    echo ""
    echo -e "${YELLOW}Logs:${NC}"
    echo "  - Fichier de log: $LOG_FILE"
    echo "  - Voir les logs: docker-compose logs -f"
    echo ""
    echo -e "${YELLOW}Surveillance:${NC}"
    echo "  - Prometheus: http://localhost:9090"
    echo "  - Grafana: http://localhost:3001"
    echo "  - Jaeger: http://localhost:16686"
    echo ""
}

# Main
main() {
    log_info "=========================================="
    log_info "   DÉPLOIEMENT EN PRODUCTION"
    log_info "=========================================="

    # Confirmation obligatoire en production
    if [ -z "$CI" ]; then
        confirm_deployment
    fi

    log_info "Début du déploiement PRODUCTION à $TIMESTAMP"

    # Exécution des étapes avec rollback automatique en cas d'échec
    check_prerequisites || exit 1
    create_backup || exit 1
    rolling_update || { rollback; exit 1; }
    smoke_tests || { rollback; exit 1; }
    post_deployment_monitoring || { rollback; exit 1; }
    cleanup_old_backups || true

    display_summary

    log_info "Déploiement PRODUCTION terminé avec succès à $(date +%Y%m%d-%H%M%S)"

    exit 0
}

# Exécuter le script
main
