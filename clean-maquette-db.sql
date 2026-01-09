-- =======================================================================================
-- NETTOYAGE COMPLET DE LA BASE MAQUETTE
-- =======================================================================================
-- Ce script vide toutes les tables dans le bon ordre (respect des FK)

USE daos_maquette_db;

-- Désactiver temporairement les vérifications de clés étrangères
SET FOREIGN_KEY_CHECKS = 0;

-- Vider les tables dans l'ordre inverse des dépendances
TRUNCATE TABLE module;
TRUNCATE TABLE ec;
TRUNCATE TABLE ue;
TRUNCATE TABLE semestres;
TRUNCATE TABLE maquettes;
TRUNCATE TABLE classes;
TRUNCATE TABLE filieres;
TRUNCATE TABLE formations;
TRUNCATE TABLE niveaux;
TRUNCATE TABLE departements;

-- Réactiver les vérifications de clés étrangères
SET FOREIGN_KEY_CHECKS = 1;

-- Message de confirmation
SELECT 'Toutes les tables ont été vidées avec succès!' AS Status;
