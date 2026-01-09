@echo off
echo ========================================
echo Demarrage du Maquette Service
echo ========================================
echo.

REM Verification du port 8083
echo [1/4] Verification que le port 8083 est libre...
netstat -ano | findstr :8083 >nul
if %errorlevel% equ 0 (
    echo ATTENTION: Le port 8083 est deja utilise!
    echo Voulez-vous tuer le processus qui utilise ce port? (O/N)
    choice /c ON /n
    if errorlevel 2 goto :end
    if errorlevel 1 (
        for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8083') do (
            echo Arret du processus %%a...
            taskkill /PID %%a /F
        )
    )
) else (
    echo ✓ Port 8083 disponible
)
echo.

REM Verification de MySQL
echo [2/4] Verification de MySQL...
netstat -ano | findstr :3306 >nul
if %errorlevel% neq 0 (
    echo ERREUR: MySQL ne semble pas demarrer sur le port 3306
    echo Veuillez demarrer MySQL et reessayer.
    pause
    goto :end
) else (
    echo ✓ MySQL detecte sur le port 3306
)
echo.

REM Compilation
echo [3/4] Recherche du fichier JAR...
if exist "target\maquette-service-1.0.0.jar" (
    echo ✓ JAR trouve
) else (
    echo JAR non trouve. Compilation necessaire.
    echo Cette etape peut prendre quelques minutes...
    if exist "mvnw.cmd" (
        call mvnw.cmd clean package -DskipTests
    ) else (
        mvn clean package -DskipTests
    )
    if errorlevel 1 (
        echo ERREUR lors de la compilation
        pause
        goto :end
    )
)
echo.

REM Demarrage
echo [4/4] Demarrage du service...
echo.
echo ========================================
echo Service demarre sur http://localhost:8083
echo ========================================
echo.
echo Appuyez sur Ctrl+C pour arreter le service
echo.

if exist "mvnw.cmd" (
    call mvnw.cmd spring-boot:run
) else if exist "target\maquette-service-1.0.0.jar" (
    java -jar target\maquette-service-1.0.0.jar
) else (
    echo ERREUR: Impossible de demarrer le service
    pause
)

:end
