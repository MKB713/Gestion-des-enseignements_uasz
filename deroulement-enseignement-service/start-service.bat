@echo off
echo ====================================
echo Demarrage du Deroulement Enseignement Service
echo ====================================
echo.

REM Verifier que MySQL est accessible
echo Verification de la connexion MySQL...
mysql -h localhost -P 3306 -uroot -e "SELECT 'MySQL OK' AS status;" 2>nul
if %errorlevel% neq 0 (
    echo [ERREUR] MySQL n'est pas accessible sur localhost:3306
    echo Veuillez demarrer MySQL avant de lancer ce service.
    pause
    exit /b 1
)
echo [OK] MySQL est accessible
echo.

REM Verifier que Eureka est accessible
echo Verification du serveur Eureka...
curl -s http://localhost:8761/actuator/health >nul 2>&1
if %errorlevel% neq 0 (
    echo [AVERTISSEMENT] Eureka Server ne semble pas accessible sur localhost:8761
    echo Le service pourra demarrer mais ne sera pas enregistre dans Eureka.
    echo.
)

echo Demarrage du service sur le port 8086...
echo.
mvn spring-boot:run

if %errorlevel% neq 0 (
    echo.
    echo [ERREUR] Le service n'a pas pu demarrer
    echo Verifiez les logs ci-dessus pour plus de details
    pause
    exit /b 1
)
