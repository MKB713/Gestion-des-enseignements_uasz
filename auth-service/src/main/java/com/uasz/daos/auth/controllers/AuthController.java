package com.uasz.daos.auth.controllers;

import com.uasz.daos.auth.dto.AuthenticationRequest;
import com.uasz.daos.auth.dto.AuthenticationResponse;
import com.uasz.daos.auth.dto.RefreshTokenRequest;
import com.uasz.daos.auth.dto.RegisterRequest;
import com.uasz.daos.auth.model.Utilisateur;
import com.uasz.daos.auth.repository.UtilisateurRepository;
import com.uasz.daos.auth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UtilisateurRepository utilisateurRepository;

    public AuthController(AuthService authService, UtilisateurRepository utilisateurRepository) {
        this.authService = authService;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * ENDPOINT DE TEST SIMPLE - Pour vérifier que tout fonctionne
     */
    @PostMapping("/test-login")
    public ResponseEntity<?> testLogin(@RequestBody AuthenticationRequest request) {
        try {
            System.out.println("====================================");
            System.out.println("=== TEST LOGIN SIMPLE ===");
            System.out.println("====================================");
            System.out.println("Email reçu: " + request.getEmail());

            Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            System.out.println("Utilisateur trouvé: " + utilisateur.getEmail());
            System.out.println("ID: " + utilisateur.getId());
            System.out.println("Nom: " + utilisateur.getNom());
            System.out.println("Prénom: " + utilisateur.getPrenom());
            System.out.println("Rôle: " + utilisateur.getRole());

            // Créer une réponse manuelle simple
            Map<String, Object> response = new HashMap<>();
            response.put("access_token", "test-token-123456789");
            response.put("refresh_token", "test-refresh-987654321");
            response.put("token_type", "Bearer");
            response.put("expires_in", 86400);

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", utilisateur.getId());
            userMap.put("email", utilisateur.getEmail());
            userMap.put("nom", utilisateur.getNom());
            userMap.put("prenom", utilisateur.getPrenom());
            userMap.put("role", utilisateur.getRole().toString());

            response.put("user", userMap);

            System.out.println("Réponse créée avec succès");
            System.out.println("====================================");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("====================================");
            System.err.println("=== ERREUR TEST LOGIN ===");
            System.err.println("Type d'erreur: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            System.err.println("====================================");

            Map<String, String> error = new HashMap<>();
            error.put("error", "Test login failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * REGISTER - Inscription d'un nouvel utilisateur
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            System.out.println("====================================");
            System.out.println("=== REGISTER REQUEST ===");
            System.out.println("====================================");
            System.out.println("Email: " + request.getEmail());
            System.out.println("Nom: " + request.getNom());
            System.out.println("Prénom: " + request.getPrenom());
            System.out.println("Matricule: " + request.getMatricule());
            System.out.println("Rôle: " + request.getRole());

            AuthenticationResponse response = authService.register(request);

            System.out.println("=== REGISTER SUCCESS ===");
            System.out.println("User ID: " + response.getUser().getId());
            System.out.println("Access Token: " + (response.getAccessToken() != null ? "GÉNÉRÉ" : "NULL"));
            System.out.println("Refresh Token: " + (response.getRefreshToken() != null ? "GÉNÉRÉ" : "NULL"));
            System.out.println("====================================");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.err.println("====================================");
            System.err.println("=== REGISTER ERROR ===");
            System.err.println("Type: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            System.err.println("====================================");

            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * LOGIN - Authentification d'un utilisateur
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequest request) {
        try {
            System.out.println("====================================");
            System.out.println("=== LOGIN REQUEST ===");
            System.out.println("====================================");
            System.out.println("Email: " + request.getEmail());
            System.out.println("Password length: " + (request.getPassword() != null ? request.getPassword().length() : 0));
            System.out.println("Timestamp: " + java.time.LocalDateTime.now());

            System.out.println("\n--- Appel du service d'authentification ---");
            AuthenticationResponse response = authService.authenticate(request);

            System.out.println("\n=== LOGIN SUCCESS ===");
            System.out.println("User ID: " + response.getUser().getId());
            System.out.println("User Email: " + response.getUser().getEmail());
            System.out.println("User Role: " + response.getUser().getRole());
            System.out.println("Access Token présent: " + (response.getAccessToken() != null ? "OUI" : "NON"));
            System.out.println("Access Token length: " + (response.getAccessToken() != null ? response.getAccessToken().length() : 0));
            System.out.println("Refresh Token présent: " + (response.getRefreshToken() != null ? "OUI" : "NON"));
            System.out.println("Expires In: " + response.getExpiresIn() + " secondes");

            System.out.println("\n--- Début sérialisation JSON ---");
            System.out.println("User connected");
            System.out.println("--- Envoi de la réponse au client ---");
            System.out.println("====================================\n");

            return ResponseEntity.ok(response);

        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            System.err.println("====================================");
            System.err.println("=== LOGIN FAILED - BAD CREDENTIALS ===");
            System.err.println("Email: " + request.getEmail());
            System.err.println("Raison: Identifiants incorrects");
            System.err.println("====================================\n");

            Map<String, String> error = new HashMap<>();
            error.put("error", "Authentication failed");
            error.put("message", "Email ou mot de passe incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

        } catch (Exception e) {
            System.err.println("====================================");
            System.err.println("=== LOGIN ERROR - EXCEPTION GÉNÉRALE ===");
            System.err.println("Type d'erreur: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            System.err.println("\n--- Stack Trace ---");
            e.printStackTrace();
            System.err.println("====================================\n");

            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", e.getMessage());
            error.put("type", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * REFRESH TOKEN - Rafraîchir le token d'accès
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            System.out.println("====================================");
            System.out.println("=== REFRESH TOKEN REQUEST ===");
            System.out.println("====================================");
            System.out.println("Refresh Token length: " + (request.getRefreshToken() != null ? request.getRefreshToken().length() : 0));

            AuthenticationResponse response = authService.refreshToken(request.getRefreshToken());

            System.out.println("=== REFRESH TOKEN SUCCESS ===");
            System.out.println("New Access Token length: " + (response.getAccessToken() != null ? response.getAccessToken().length() : 0));
            System.out.println("====================================\n");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("====================================");
            System.err.println("=== REFRESH TOKEN ERROR ===");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            System.err.println("====================================\n");

            Map<String, String> error = new HashMap<>();
            error.put("error", "Token refresh failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * VALIDATE TOKEN - Valider le token actuel
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        System.out.println("====================================");
        System.out.println("=== VALIDATE TOKEN ===");
        System.out.println("Authorization header present: " + (authHeader != null ? "OUI" : "NON"));
        System.out.println("====================================\n");

        Map<String, String> response = new HashMap<>();
        response.put("status", "valid");
        response.put("message", "Token valide");
        return ResponseEntity.ok(response);
    }

    /**
     * LOGOUT - Déconnexion de l'utilisateur
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        System.out.println("====================================");
        System.out.println("=== LOGOUT REQUEST ===");
        System.out.println("Token present: " + (token != null ? "OUI" : "NON"));
        System.out.println("====================================\n");

        // La révocation du token est gérée côté client
        // Pour une implémentation complète, on pourrait blacklister le token
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Déconnexion réussie");
        return ResponseEntity.ok(response);
    }

    /**
     * TEST ENDPOINT - Vérifier que le serveur fonctionne
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        System.out.println("=== TEST ENDPOINT CALLED ===");

        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Auth Service");
        response.put("message", "Le service fonctionne correctement");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}