@echo off
REM Script pour pousser vers GitHub ET GitLab en même temps
REM Usage: git-push-all.bat [nom-de-branche]

setlocal enabledelayedexpansion

set "GREEN=[92m"
set "YELLOW=[93m"
set "RED=[91m"
set "NC=[0m"

REM Vérifier qu'une branche est spécifiée
if "%1"=="" (
    echo %RED%[ERREUR]%NC% Veuillez spécifier une branche
    echo Usage: %0 nom-de-branche
    echo Exemple: %0 feature/GDEP2-1-Infrastructure-Technique
    exit /b 1
)

set BRANCH=%1

echo.
echo %GREEN%[GIT]%NC% Push vers GitHub et GitLab...
echo %GREEN%[GIT]%NC% Branche: %BRANCH%
echo.

REM Push vers GitHub
echo %YELLOW%[GITHUB]%NC% Push vers GitHub...
git push origin %BRANCH%
if errorlevel 1 (
    echo %RED%[ERREUR]%NC% Échec du push vers GitHub
    exit /b 1
)
echo %GREEN%[GITHUB]%NC% ✅ Push réussi vers GitHub!
echo.

REM Push vers GitLab
echo %YELLOW%[GITLAB]%NC% Push vers GitLab...
git push gitlab %BRANCH%
if errorlevel 1 (
    echo %YELLOW%[WARNING]%NC% Échec du push vers GitLab
    echo %YELLOW%[INFO]%NC% GitLab remote peut ne pas être configuré
    echo %YELLOW%[INFO]%NC% Pour ajouter GitLab:
    echo   git remote add gitlab https://gitlab.com/mkb713/gestion-enseignements-uasz.git
    echo.
    echo %GREEN%[RÉSUMÉ]%NC% Code poussé sur GitHub ✅
    exit /b 0
)
echo %GREEN%[GITLAB]%NC% ✅ Push réussi vers GitLab!
echo.

echo %GREEN%[SUCCÈS]%NC% ✅✅ Code poussé sur GitHub ET GitLab!
echo.
echo %GREEN%[INFO]%NC% Vérifiez les pipelines:
echo   GitHub: https://github.com/MKB713/Gestion-des-enseignements_uasz/actions
echo   GitLab: https://gitlab.com/mkb713/gestion-enseignements-uasz/-/pipelines
echo.

exit /b 0
