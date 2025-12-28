package com.uasz.daos.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitaire pour générer des hash BCrypt pour les mots de passe
 * Exécutez cette classe pour générer un hash
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Mot de passe à hasher
        String password = "password123";

        // Génération du hash
        String hash = encoder.encode(password);

        // Affichage
        System.out.println("===============================================");
        System.out.println("GÉNÉRATEUR DE HASH BCRYPT");
        System.out.println("===============================================");
        System.out.println("Mot de passe: " + password);
        System.out.println("Hash BCrypt: " + hash);
        System.out.println("===============================================");
        System.out.println("\nCopiez ce hash dans votre fichier SQL :");
        System.out.println(hash);
    }
}