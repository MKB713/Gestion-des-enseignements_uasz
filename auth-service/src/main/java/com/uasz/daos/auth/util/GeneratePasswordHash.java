package com.uasz.daos.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePasswordHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password123";
        String hash = encoder.encode(password);

        System.out.println("==============================================");
        System.out.println("Password: " + password);
        System.out.println("Hash BCrypt: " + hash);
        System.out.println("==============================================");

        // Test de vérification
        boolean matches = encoder.matches(password, hash);
        System.out.println("Test de vérification: " + (matches ? "OK ✓" : "ECHEC ✗"));

        // Test avec le hash existant dans la base
        String existingHash = "$2a$10$FlySioELwHXuiZz9/xfn5uE4Cw3cK57zgVIN5yUptHridm/SZgQv2";
        boolean matchesExisting = encoder.matches(password, existingHash);
        System.out.println("Test avec hash existant: " + (matchesExisting ? "OK ✓" : "ECHEC ✗"));
        System.out.println("==============================================");
    }
}
