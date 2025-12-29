-- Script de création de données de test pour le microservice Maquette
-- Base de données: daos_maquette_db

USE daos_maquette_db;

-- 1. Insertion de Filières
INSERT INTO Filieres (libelle, description) VALUES
('Informatique', 'Filière des sciences informatiques et technologies'),
('Mathématiques', 'Filière des mathématiques appliquées'),
('Physique', 'Filière de physique et sciences physiques');

-- 2. Insertion de Niveaux
INSERT INTO niveaux (numero, cycle) VALUES
(1, 'LICENCE'),
(2, 'LICENCE'),
(3, 'LICENCE'),
(1, 'MASTER'),
(2, 'MASTER');

-- 3. Insertion de Formations
-- Formation 1: Licence 1 Informatique
INSERT INTO formations (code, libelle, description, date_creation, statut_formation, filiere_id, niveau_id)
VALUES (
    'FORM-INFO-L1',
    'Licence 1 Informatique',
    'Formation de première année en informatique',
    NOW(),
    'ACTIVE',
    (SELECT id FROM Filieres WHERE libelle = 'Informatique' LIMIT 1),
    (SELECT id FROM niveaux WHERE numero = 1 AND cycle = 'LICENCE' LIMIT 1)
);

-- Formation 2: Licence 2 Informatique
INSERT INTO formations (code, libelle, description, date_creation, statut_formation, filiere_id, niveau_id)
VALUES (
    'FORM-INFO-L2',
    'Licence 2 Informatique',
    'Formation de deuxième année en informatique',
    NOW(),
    'ACTIVE',
    (SELECT id FROM Filieres WHERE libelle = 'Informatique' LIMIT 1),
    (SELECT id FROM niveaux WHERE numero = 2 AND cycle = 'LICENCE' LIMIT 1)
);

-- Formation 3: Master 1 Informatique
INSERT INTO formations (code, libelle, description, date_creation, statut_formation, filiere_id, niveau_id)
VALUES (
    'FORM-INFO-M1',
    'Master 1 Informatique',
    'Formation de première année de master en informatique',
    NOW(),
    'ACTIVE',
    (SELECT id FROM Filieres WHERE libelle = 'Informatique' LIMIT 1),
    (SELECT id FROM niveaux WHERE numero = 1 AND cycle = 'MASTER' LIMIT 1)
);

-- 4. Insertion de Maquettes
-- Maquette pour Licence 1 Informatique
INSERT INTO maquettes (nom, code, formation_id)
VALUES (
    'Maquette L1 Informatique 2024',
    'MAQ-INFO-L1-V1',
    (SELECT id FROM formations WHERE code = 'FORM-INFO-L1' LIMIT 1)
);

-- Maquette pour Licence 2 Informatique
INSERT INTO maquettes (nom, code, formation_id)
VALUES (
    'Maquette L2 Informatique 2024',
    'MAQ-INFO-L2-V1',
    (SELECT id FROM formations WHERE code = 'FORM-INFO-L2' LIMIT 1)
);

-- Maquette pour Master 1 Informatique
INSERT INTO maquettes (nom, code, formation_id)
VALUES (
    'Maquette M1 Informatique 2024',
    'MAQ-INFO-M1-V1',
    (SELECT id FROM formations WHERE code = 'FORM-INFO-M1' LIMIT 1)
);

-- Vérification des données insérées
SELECT 'Filieres' as Table_Name, COUNT(*) as Count FROM Filieres
UNION ALL
SELECT 'Niveaux', COUNT(*) FROM niveaux
UNION ALL
SELECT 'Formations', COUNT(*) FROM formations
UNION ALL
SELECT 'Maquettes', COUNT(*) FROM maquettes;
