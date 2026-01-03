-- Ajouter les champs manquants à la table utilisateur
USE daos_auth_db;

-- Vérifier les colonnes existantes
SHOW COLUMNS FROM utilisateur;

-- Ajouter les champs manquants si nécessaire
ALTER TABLE utilisateur
ADD COLUMN IF NOT EXISTS matricule VARCHAR(50) UNIQUE,
ADD COLUMN IF NOT EXISTS adresse VARCHAR(255),
ADD COLUMN IF NOT EXISTS date_naissance DATE,
ADD COLUMN IF NOT EXISTS date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS derniere_connexion TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS tentatives_connexion INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS compte_verrouille BOOLEAN DEFAULT FALSE;

-- Mettre à jour les matricules si vides
UPDATE utilisateur SET matricule = CONCAT('USR', LPAD(id, 4, '0')) WHERE matricule IS NULL OR matricule = '';

-- Vérifier
SELECT id, email, matricule, compte_verrouille, tentatives_connexion, etat
FROM utilisateur
WHERE email LIKE '%@uasz.sn'
LIMIT 10;
