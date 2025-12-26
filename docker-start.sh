#!/bin/bash

# Script de démarrage rapide pour Docker Compose DAOS
# Usage: ./docker-start.sh [option]
# Options:
#   start     - Démarrer tous les services
#   stop      - Arrêter tous les services
#   restart   - Redémarrer tous les services
#   logs      - Voir les logs
#   status    - Voir le statut des services
#   clean     - Nettoyer tout (conteneurs, volumes, images)

set -e

# Couleurs pour le terminal
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Fonction pour afficher un message coloré
print_message() {
    echo -e "${GREEN}[DAOS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Vérifier que Docker est installé
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker n'est pas installé. Veuillez l'installer d'abord."
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose n'est pas installé. Veuillez l'installer d'abord."
        exit 1
    fi
}

# Vérifier que le fichier .env existe
check_env() {
    if [ ! -f .env ]; then
        print_warning "Fichier .env non trouvé. Copie de .env.example..."
        cp .env.example .env
        print_message "Fichier .env créé. Veuillez le modifier avec vos valeurs."
        print_message "Notamment: MYSQL_ROOT_PASSWORD, JWT_SECRET, etc."
        read -p "Appuyez sur Entrée pour continuer..."
    fi
}

# Démarrer tous les services
start_services() {
    print_message "Démarrage de tous les services DAOS..."
    print_message "Cela peut prendre quelques minutes..."

    docker-compose up -d

    print_message "Services démarrés!"
    print_message "Attente que tous les services soient healthy..."

    # Attendre que les services soient prêts
    sleep 10

    print_message "Vérification du statut des services..."
    docker-compose ps

    print_message ""
    print_message "========================================="
    print_message "Services disponibles:"
    print_message "========================================="
    print_message "Front-End:        http://localhost:3000"
    print_message "API Gateway:      http://localhost:8080"
    print_message "Eureka Dashboard: http://localhost:8761"
    print_message "Config Server:    http://localhost:8888"
    print_message "Swagger UI:       http://localhost:8080/swagger-ui.html"
    print_message "========================================="
}

# Arrêter tous les services
stop_services() {
    print_message "Arrêt de tous les services..."
    docker-compose down
    print_message "Services arrêtés!"
}

# Redémarrer tous les services
restart_services() {
    print_message "Redémarrage de tous les services..."
    docker-compose restart
    print_message "Services redémarrés!"
}

# Voir les logs
view_logs() {
    print_message "Affichage des logs (Ctrl+C pour quitter)..."
    docker-compose logs -f --tail=100
}

# Voir le statut
view_status() {
    print_message "Statut des services:"
    docker-compose ps

    print_message ""
    print_message "Health checks:"

    # Test des endpoints
    services=(
        "http://localhost:8761/actuator/health:Eureka Server"
        "http://localhost:8888/actuator/health:Config Server"
        "http://localhost:8080/actuator/health:API Gateway"
        "http://localhost:8081/actuator/health:Auth Service"
        "http://localhost:3000/health:Front-End"
    )

    for service in "${services[@]}"; do
        IFS=: read -r url name <<< "$service"
        if curl -sf "$url" > /dev/null 2>&1; then
            print_message "✓ $name: UP"
        else
            print_error "✗ $name: DOWN"
        fi
    done
}

# Nettoyer tout
clean_all() {
    print_warning "ATTENTION: Cette opération va supprimer:"
    print_warning "- Tous les conteneurs"
    print_warning "- Tous les volumes (données MySQL)"
    print_warning "- Toutes les images construites"
    read -p "Êtes-vous sûr? (yes/no): " confirm

    if [ "$confirm" = "yes" ]; then
        print_message "Nettoyage en cours..."
        docker-compose down -v --rmi all
        print_message "Nettoyage terminé!"
    else
        print_message "Opération annulée."
    fi
}

# Build des images
build_images() {
    print_message "Build de toutes les images Docker..."
    docker-compose build
    print_message "Build terminé!"
}

# Menu principal
show_menu() {
    echo ""
    echo "========================================="
    echo "  DAOS - Docker Compose Manager"
    echo "========================================="
    echo "1. Démarrer tous les services"
    echo "2. Arrêter tous les services"
    echo "3. Redémarrer tous les services"
    echo "4. Voir les logs"
    echo "5. Voir le statut"
    echo "6. Build les images"
    echo "7. Nettoyer tout"
    echo "0. Quitter"
    echo "========================================="
    read -p "Votre choix: " choice

    case $choice in
        1) start_services ;;
        2) stop_services ;;
        3) restart_services ;;
        4) view_logs ;;
        5) view_status ;;
        6) build_images ;;
        7) clean_all ;;
        0) exit 0 ;;
        *) print_error "Choix invalide" ;;
    esac
}

# Point d'entrée
main() {
    check_docker
    check_env

    # Si un argument est fourni
    if [ $# -eq 0 ]; then
        # Mode interactif
        while true; do
            show_menu
        done
    else
        # Mode commande
        case $1 in
            start) start_services ;;
            stop) stop_services ;;
            restart) restart_services ;;
            logs) view_logs ;;
            status) view_status ;;
            build) build_images ;;
            clean) clean_all ;;
            *)
                print_error "Option invalide: $1"
                echo "Usage: $0 [start|stop|restart|logs|status|build|clean]"
                exit 1
                ;;
        esac
    fi
}

# Exécuter le script
main "$@"
