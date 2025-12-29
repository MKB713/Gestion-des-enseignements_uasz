-- ========================================
-- Fichier SQL - Utilisateurs de test DAOS
-- Base de données: daos_auth_db
-- Mot de passe pour tous les comptes: password123
-- ========================================

USE daos_auth_db;

-- Suppression des utilisateurs de test existants (garde seulement l'admin principal)
DELETE FROM utilisateur WHERE id > 1;

-- ========================================
-- 1. CHEF DE DÉPARTEMENT - Accès administratif
-- ========================================
INSERT INTO utilisateur (nom, prenom, telephone, email, password, role, etat)
VALUES (
    'Diallo',
    'Mamadou',
    '221771234568',
        'chef.departement@uasz.sn',
    '$2a$10$FlySioELwHXuiZz9/xfn5uE4Cw3cK57zgVIN5yUptHridm/SZgQv2',  -- password123
    'CHEF_DE_DEPARTEMENT',
    'ACTIF'
);

-- ========================================
-- 2. COORDINATEUR DES LICENCES - Gestion complète des licences
-- ========================================
INSERT INTO utilisateur (nom, prenom, telephone, email, password, role, etat)
VALUES (
    'Sarr',
    'Fatou',
    '221771234569',
    'coordinateur@uasz.sn',
    '$2a$10$FlySioELwHXuiZz9/xfn5uE4Cw3cK57zgVIN5yUptHridm/SZgQv2',  -- password123
    'COORDONATEUR_DES_LICENCES',
    'ACTIF'
);

-- ========================================
-- 3. RESPONSABLE MASTER - Gestion complète des masters
-- ========================================
INSERT INTO utilisateur (nom, prenom, telephone, email, password, role, etat)
VALUES (
    'Ndiaye',
    'Ousmane',
    '221771234570',
    'responsable.master@uasz.sn',
    '$2a$10$FlySioELwHXuiZz9/xfn5uE4Cw3cK57zgVIN5yUptHridm/SZgQv2',  -- password123
    'RESPONSABLE_MASTER',
    'ACTIF'
);

-- ========================================
-- 4. ENSEIGNANT - Consultation uniquement (cahier de texte, EDT)
-- Note: Pour que les enseignants puissent se connecter, ils doivent être
-- dans la table 'utilisateur' avec le rôle ENSEIGNANT
-- ========================================
INSERT INTO utilisateur (nom, prenom, telephone, email, password, role, etat)
VALUES (
    'Sall',
    'Cheikh',
    '221771234574',
    'enseignant@uasz.sn',
    '$2a$10$FlySioELwHXuiZz9/xfn5uE4Cw3cK57zgVIN5yUptHridm/SZgQv2',  -- password123
    'ENSEIGNANT',
    'ACTIF'
);

-- ========================================
-- 5. ÉTUDIANT - Consultation EDT uniquement
-- ========================================
INSERT INTO utilisateur (nom, prenom, telephone, email, password, role, etat)
VALUES (
    'Sow',
    'Aminata',
    '221771234571',
    'etudiant@uasz.sn',
    '$2a$10$FlySioELwHXuiZz9/xfn5uE4Cw3cK57zgVIN5yUptHridm/SZgQv2',  -- password123
    'ETUDIANT',
    'ACTIF'
);

-- ========================================
-- Utilisateurs supplémentaires pour tests
-- ========================================

-- Autre étudiant
INSERT INTO utilisateur (nom, prenom, telephone, email, password, role, etat)
VALUES (
    'Ba',
    'Ibrahima',
    '221771234572',
    'etudiant2@uasz.sn',
    '$2a$10$FlySioELwHXuiZz9/xfn5uE4Cw3cK57zgVIN5yUptHridm/SZgQv2',  -- password123
    'ETUDIANT',
    'ACTIF'
);

-- ========================================
-- Affichage des utilisateurs créés
-- ========================================
SELECT
    id,
    CONCAT(prenom, ' ', nom) AS nom_complet,
    email,
    role,
    etat
FROM utilisateur
ORDER BY
    FIELD(role, 'ADMIN', 'CHEF_DE_DEPARTEMENT', 'COORDONATEUR_DES_LICENCES', 'RESPONSABLE_MASTER', 'ENSEIGNANT', 'ETUDIANT');

-- ========================================
-- RÉSUMÉ
-- ========================================
SELECT
    '========================================' AS '';
SELECT
    'COMPTES CRÉÉS' AS '';
SELECT
    '========================================' AS '';
SELECT
    '' AS '';
SELECT
    'Email: admin@uasz.sn' AS 'ADMIN (déjà existant)',
    'Mot de passe: voir base de données' AS '';
SELECT
    'Email: chef.departement@uasz.sn' AS 'CHEF DE DÉPARTEMENT',
    'Mot de passe: password123' AS '';
SELECT
    'Email: coordinateur@uasz.sn' AS 'COORDINATEUR LICENCES',
    'Mot de passe: password123' AS '';
SELECT
    'Email: responsable.master@uasz.sn' AS 'RESPONSABLE MASTER',
    'Mot de passe: password123' AS '';
SELECT
    'Email: enseignant@uasz.sn' AS 'ENSEIGNANT',
    'Mot de passe: password123' AS '';
SELECT
    'Email: etudiant@uasz.sn' AS 'ÉTUDIANT',
    'Mot de passe: password123' AS '';
SELECT
    'Email: etudiant2@uasz.sn' AS 'ÉTUDIANT 2',
    'Mot de passe: password123' AS '';
