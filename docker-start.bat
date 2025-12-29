@echo off
REM Script de démarrage rapide pour Docker Compose DAOS (Windows)
REM Usage: docker-start.bat [option]

setlocal enabledelayedexpansion

set "GREEN=[92m"
set "YELLOW=[93m"
set "RED=[91m"
set "NC=[0m"

REM Vérifier que Docker est installé
docker --version >nul 2>&1
if errorlevel 1 (
    echo %RED%[ERROR]%NC% Docker n'est pas installé.
    exit /b 1
)

docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo %RED%[ERROR]%NC% Docker Compose n'est pas installé.
    exit /b 1
)

REM Vérifier que le fichier .env existe
if not exist .env (
    echo %YELLOW%[WARNING]%NC% Fichier .env non trouvé. Copie de .env.example...
    copy .env.example .env
    echo %GREEN%[DAOS]%NC% Fichier .env créé. Veuillez le modifier avec vos valeurs.
    pause
)

REM Fonction pour démarrer les services
if "%1"=="start" goto start_services
if "%1"=="stop" goto stop_services
if "%1"=="restart" goto restart_services
if "%1"=="logs" goto view_logs
if "%1"=="status" goto view_status
if "%1"=="build" goto build_images
if "%1"=="clean" goto clean_all
if "%1"=="" goto menu

echo %RED%[ERROR]%NC% Option invalide: %1
echo Usage: %0 [start^|stop^|restart^|logs^|status^|build^|clean]
exit /b 1

:start_services
echo %GREEN%[DAOS]%NC% Démarrage de tous les services...
docker-compose up -d
echo.
echo %GREEN%[DAOS]%NC% Services démarrés!
echo.
echo =========================================
echo Services disponibles:
echo =========================================
echo Front-End:        http://localhost:3000
echo API Gateway:      http://localhost:8080
echo Eureka Dashboard: http://localhost:8761
echo Config Server:    http://localhost:8888
echo Swagger UI:       http://localhost:8080/swagger-ui.html
echo =========================================
goto end

:stop_services
echo %GREEN%[DAOS]%NC% Arrêt de tous les services...
docker-compose down
echo %GREEN%[DAOS]%NC% Services arrêtés!
goto end

:restart_services
echo %GREEN%[DAOS]%NC% Redémarrage de tous les services...
docker-compose restart
echo %GREEN%[DAOS]%NC% Services redémarrés!
goto end

:view_logs
echo %GREEN%[DAOS]%NC% Affichage des logs (Ctrl+C pour quitter)...
docker-compose logs -f --tail=100
goto end

:view_status
echo %GREEN%[DAOS]%NC% Statut des services:
docker-compose ps
goto end

:build_images
echo %GREEN%[DAOS]%NC% Build de toutes les images Docker...
docker-compose build
echo %GREEN%[DAOS]%NC% Build terminé!
goto end

:clean_all
echo %YELLOW%[WARNING]%NC% Cette opération va supprimer tous les conteneurs, volumes et images.
set /p confirm="Êtes-vous sûr? (yes/no): "
if "%confirm%"=="yes" (
    echo %GREEN%[DAOS]%NC% Nettoyage en cours...
    docker-compose down -v --rmi all
    echo %GREEN%[DAOS]%NC% Nettoyage terminé!
) else (
    echo %GREEN%[DAOS]%NC% Opération annulée.
)
goto end

:menu
echo.
echo =========================================
echo   DAOS - Docker Compose Manager
echo =========================================
echo 1. Démarrer tous les services
echo 2. Arrêter tous les services
echo 3. Redémarrer tous les services
echo 4. Voir les logs
echo 5. Voir le statut
echo 6. Build les images
echo 7. Nettoyer tout
echo 0. Quitter
echo =========================================
set /p choice="Votre choix: "

if "%choice%"=="1" goto start_services
if "%choice%"=="2" goto stop_services
if "%choice%"=="3" goto restart_services
if "%choice%"=="4" goto view_logs
if "%choice%"=="5" goto view_status
if "%choice%"=="6" goto build_images
if "%choice%"=="7" goto clean_all
if "%choice%"=="0" exit /b 0

echo %RED%[ERROR]%NC% Choix invalide
goto menu

:end
if "%1"=="" (
    pause
    goto menu
)
exit /b 0
