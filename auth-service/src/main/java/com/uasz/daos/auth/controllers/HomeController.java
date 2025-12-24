package com.uasz.daos.auth.controllers;

import com.uasz.daos.auth.dto.DashboardStatsDTO;
import com.uasz.daos.auth.enums.Role;
import com.uasz.daos.auth.services.CustomUserDetails;
import com.uasz.daos.auth.services.DashboardService;
import com.uasz.daos.auth.services.FormationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class HomeController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private FormationService formationService;

    /**
     * API d'accueil
     */
    @GetMapping("/")
    public ResponseEntity<?> index() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Si non authentifié
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.ok().body(Map.of(
                    "message", "Bienvenue sur DAOS API",
                    "authenticated", false,
                    "endpoints", Map.of(
                            "login", "POST /api/auth/login",
                            "register", "POST /api/auth/register",
                            "documentation", "/swagger-ui.html"
                    )
            ));
        }

        // Si authentifié
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Role userRole = userDetails.getRole();

        return ResponseEntity.ok().body(Map.of(
                "message", "Bienvenue " + userDetails.getPrenom() + " " + userDetails.getNom(),
                "authenticated", true,
                "user", Map.of(
                        "id", userDetails.getId(),
                        "nom", userDetails.getNom(),
                        "prenom", userDetails.getPrenom(),
                        "email", userDetails.getUsername(),
                        "role", userRole.name(),
                        "roleDisplay", userRole.getLibelle()
                ),
                "dashboardUrl", "/api/dashboard/my"
        ));
    }

    /**
     * Dashboard utilisateur
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Non authentifié"
            ));
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Role userRole = userDetails.getRole();

        DashboardStatsDTO stats = dashboardService.getStats();
        Object formations = formationService.getAllFormations();

        Map<String, Object> response = Map.of(
                "user", Map.of(
                        "id", userDetails.getId(),
                        "nom", userDetails.getNom(),
                        "prenom", userDetails.getPrenom(),
                        "email", userDetails.getUsername(),
                        "role", userRole.name(),
                        "roleDisplay", userRole.getLibelle()
                ),
                "stats", stats,
                "formations", formations,
                "dashboardType", getDashboardType(userRole)
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Dashboard par rôle
     */
    @GetMapping("/{role}")
    public ResponseEntity<?> getDashboardByRole(@PathVariable String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Non authentifié"
            ));
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Role userRole = userDetails.getRole();

        // Vérifier les permissions
        if (!hasAccessToDashboard(userRole, role)) {
            return ResponseEntity.status(403).body(Map.of(
                    "error", "Accès non autorisé"
            ));
        }

        DashboardStatsDTO stats = dashboardService.getStats();
        Object formations = formationService.getAllFormations();

        return ResponseEntity.ok(Map.of(
                "stats", stats,
                "formations", formations,
                "userRole", userRole.name(),
                "dashboardFor", role
        ));
    }

    private boolean hasAccessToDashboard(Role userRole, String requestedRole) {
        if (userRole.isAdmin()) return true;

        try {
            Role requested = Role.valueOf(requestedRole.toUpperCase());
            return userRole == requested;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String getDashboardType(Role role) {
        return switch (role) {
            case ETUDIANT -> "etudiant";
            case ENSEIGNANT -> "enseignant";
            case RESPONSABLE_MASTER -> "responsable";
            case COORDONATEUR_DES_LICENCES -> "coordinateur";
            case ADMIN, CHEF_DE_DEPARTEMENT -> "admin";
        };
    }
}