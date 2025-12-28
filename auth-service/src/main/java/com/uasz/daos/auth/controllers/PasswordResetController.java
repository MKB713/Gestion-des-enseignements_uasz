package com.uasz.daos.auth.controllers;

import com.uasz.daos.auth.services.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Password Reset", description = "API pour la réinitialisation de mot de passe")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @Operation(summary = "Demander une réinitialisation de mot de passe")
    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestPasswordReset(
            @RequestParam String email,
            HttpServletRequest request) {

        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        passwordResetService.requestPasswordReset(email, ipAddress, userAgent);

        // Pour des raisons de sécurité, on retourne toujours le même message
        return ResponseEntity.ok().body(
                "Si votre email est enregistré, vous recevrez un lien de réinitialisation.");
    }

    @Operation(summary = "Vérifier la validité d'un token de réinitialisation")
    @GetMapping("/reset-password/validate")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        boolean isValid = passwordResetService.validateToken(token);

        if (isValid) {
            return ResponseEntity.ok().body(Map.of(
                    "valid", true,
                    "message", "Token valide"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "message", "Token invalide ou expiré"
            ));
        }
    }

    @Operation(summary = "Réinitialiser le mot de passe avec un token")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        boolean success = passwordResetService.resetPassword(
                request.getToken(),
                request.getNewPassword(),
                request.getConfirmPassword(),
                ipAddress,
                userAgent);

        if (success) {
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Mot de passe réinitialisé avec succès"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Échec de la réinitialisation"
            ));
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Data
    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;
        private String confirmPassword;
    }

    // Méthode utilitaire pour créer des maps
    private Map<String, Object> createResponse(String key, Object value) {
        Map<String, Object> response = new HashMap<>();
        response.put(key, value);
        return response;
    }
}