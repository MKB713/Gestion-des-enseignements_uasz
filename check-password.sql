-- Vérifier les mots de passe dans la base de données
USE daos_auth_db;

-- Vérifier l'utilisateur admin
SELECT
    id,
    email,
    nom,
    prenom,
    role,
    etat,
    LENGTH(password) as longueur_hash,
    LEFT(password, 30) as debut_hash,
    compte_verrouille,
    tentatives_connexion
FROM utilisateur
WHERE email = 'admin@uasz.sn';

-- Vérifier tous les utilisateurs
SELECT
    id,
    email,
    role,
    LENGTH(password) as longueur_hash,
    compte_verrouille,
    tentatives_connexion,
    etat
FROM utilisateur
WHERE email LIKE '%@uasz.sn'
ORDER BY id;
