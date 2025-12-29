@echo off
echo Insertion des donnees de test pour le microservice Maquette...
echo.

REM Insertion des Filieres
echo [1/4] Insertion des Filieres...
mysql -u root -proot -e "USE daos_maquette_db; INSERT IGNORE INTO Filieres (libelle, description) VALUES ('Informatique', 'Filiere des sciences informatiques'), ('Mathematiques', 'Filiere des mathematiques'), ('Physique', 'Filiere de physique');"

REM Insertion des Niveaux
echo [2/4] Insertion des Niveaux...
mysql -u root -proot -e "USE daos_maquette_db; INSERT IGNORE INTO niveaux (numero, cycle) VALUES (1, 'LICENCE'), (2, 'LICENCE'), (3, 'LICENCE'), (1, 'MASTER'), (2, 'MASTER');"

REM Insertion des Formations
echo [3/4] Insertion des Formations...
mysql -u root -proot -e "USE daos_maquette_db; INSERT IGNORE INTO formations (code, libelle, description, date_creation, statut_formation, filiere_id, niveau_id) VALUES ('FORM-INFO-L1', 'Licence 1 Informatique', 'Formation L1 Info', NOW(), 'ACTIVE', 1, 1), ('FORM-INFO-L2', 'Licence 2 Informatique', 'Formation L2 Info', NOW(), 'ACTIVE', 1, 2), ('FORM-INFO-L3', 'Licence 3 Informatique', 'Formation L3 Info', NOW(), 'ACTIVE', 1, 3);"

REM Insertion des Maquettes
echo [4/4] Insertion des Maquettes...
mysql -u root -proot -e "USE daos_maquette_db; INSERT IGNORE INTO maquettes (nom, code, formation_id) VALUES ('Maquette L1 Info 2024', 'MAQ-INFO-L1-V1', 1), ('Maquette L2 Info 2024', 'MAQ-INFO-L2-V1', 2), ('Maquette L3 Info 2024', 'MAQ-INFO-L3-V1', 3);"

echo.
echo ========================================
echo Verification des donnees inserees:
echo ========================================
mysql -u root -proot -e "USE daos_maquette_db; SELECT 'Filieres' as Table_Name, COUNT(*) as Count FROM Filieres UNION ALL SELECT 'Niveaux', COUNT(*) FROM niveaux UNION ALL SELECT 'Formations', COUNT(*) FROM formations UNION ALL SELECT 'Maquettes', COUNT(*) FROM maquettes;"

echo.
echo Donnees de test inserees avec succes!
pause
