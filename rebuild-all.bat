@echo off
echo ============================================================
echo Reconstruction de TOUS les microservices
echo ============================================================
echo.

set SERVICES=config-server eureka-server api-gateway auth-service maquette-service enseignant-service emploi-temps-service deroulement-enseignement-service choix-enseignement-service

set FAILED=0

for %%s in (%SERVICES%) do (
    echo.
    echo ============================================================
    echo Reconstruction de %%s
    echo ============================================================
    cd %%s
    call mvn clean compile -q
    if %errorlevel% neq 0 (
        echo [ERREUR] %%s a echoue
        set FAILED=1
    ) else (
        echo [OK] %%s compile avec succes
    )
    cd ..
)

echo.
echo ============================================================
if %FAILED%==1 (
    echo [ERREUR] Certains services ont echoue
    echo Verifiez les messages ci-dessus
) else (
    echo [SUCCES] TOUS les services ont ete compiles avec succes !
)
echo ============================================================
pause
