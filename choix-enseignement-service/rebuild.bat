@echo off
echo ==========================================
echo Reconstruction du Choix Enseignement Service
echo ==========================================
echo.

echo Nettoyage du projet...
call mvn clean

if %errorlevel% neq 0 (
    echo [ERREUR] Le nettoyage a echoue
    pause
    exit /b 1
)

echo.
echo Compilation du projet...
call mvn compile

if %errorlevel% neq 0 (
    echo.
    echo [ERREUR] La compilation a echoue
    echo Verifiez les messages d'erreur ci-dessus
    pause
    exit /b 1
)

echo.
echo ==========================================
echo [SUCCES] Build reussi !
echo ==========================================
echo.
echo Le projet est pret. Vous pouvez maintenant :
echo   1. Demarrer le service : mvn spring-boot:run
echo   2. Creer le package : mvn package
echo.
pause
