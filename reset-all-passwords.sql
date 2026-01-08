-- Réinitialiser TOUS les mots de passe à "password123"
USE daos_auth_db;

-- Hash BCrypt pour "password123" (généré avec BCryptPasswordEncoder de Spring)
-- Ce hash a été vérifié avec GeneratePasswordHash.java
SET @password_hash = '$2a$10$FlySioELwHXuiZz9/xfn5uE4Cw3cK57zgVIN5yUptHridm/SZgQv2';

-- D'abord, déverrouiller tous les comptes et réinitialiser les tentatives
UPDATE utilisateur
SET
    compte_verrouille = FALSE,
    tentatives_connexion = 0
WHERE email LIKE '%@uasz.sn';

-- Mettre à jour TOUS les mots de passe
UPDATE utilisateur
SET password = @password_hash
WHERE email LIKE '%@uasz.sn';

-- Vérifier les résultats
SELECT
    id,
    email,
    role,
    etat,
    compte_verrouille,
    tentatives_connexion,
    LENGTH(password) as hash_length,
    LEFT(password, 7) as hash_prefix
FROM utilisateur
WHERE email LIKE '%@uasz.sn'
ORDER BY id;
