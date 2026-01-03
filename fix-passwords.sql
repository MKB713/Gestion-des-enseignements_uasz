-- Script pour corriger les mots de passe
USE daos_auth_db;

-- Mot de passe BCrypt pour "password123"
SET @correct_hash = '$2a$10$FlySioELwHXuiZz9/xfn5uE4Cw3cK57zgVIN5yUptHridm/SZgQv2';

-- Mettre à jour tous les utilisateurs avec le bon hash
UPDATE utilisateur SET password = @correct_hash WHERE email = 'admin@uasz.sn';
UPDATE utilisateur SET password = @correct_hash WHERE email = 'coordinateur@uasz.sn';
UPDATE utilisateur SET password = @correct_hash WHERE email = 'responsable.master@uasz.sn';
UPDATE utilisateur SET password = @correct_hash WHERE email = 'enseignant@uasz.sn';
UPDATE utilisateur SET password = @correct_hash WHERE email = 'etudiant@uasz.sn';
UPDATE utilisateur SET password = @correct_hash WHERE email = 'etudiant2@uasz.sn';
UPDATE utilisateur SET password = @correct_hash WHERE email = 'chef.departement@uasz.sn';

-- Vérifier les résultats
SELECT id, email, role,
       CASE
           WHEN password = @correct_hash THEN 'OK'
           ELSE 'PROBLEME'
       END as statut_password
FROM utilisateur
WHERE email LIKE '%@uasz.sn'
ORDER BY id;
