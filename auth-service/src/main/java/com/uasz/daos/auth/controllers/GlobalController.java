package com.uasz.daos.auth.controllers;

import com.uasz.daos.auth.services.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class GlobalController {

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return ResponseEntity.ok().body(Map.of(
                    "user", Map.of(
                            "id", userDetails.getId(),
                            "nom", userDetails.getNom(),
                            "prenom", userDetails.getPrenom(),
                            "email", userDetails.getUsername(),
                            "role", userDetails.getRole().name(),
                            "roleDisplay", userDetails.getRole().getLibelle()
                    ),
                    "isAuthenticated", true,
                    "isAdmin", userDetails.getRole().isAdmin()
            ));
        }

        return ResponseEntity.ok().body(Map.of(
                "isAuthenticated", false,
                "message", "Non authentifié"
        ));
    }

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated()
                && !(auth.getPrincipal().equals("anonymousUser"));

        return ResponseEntity.ok().body(Map.of(
                "authenticated", isAuthenticated,
                "isAdmin", isAdmin()
        ));
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getRole().isAdmin();
        }
        return false;
    }
}