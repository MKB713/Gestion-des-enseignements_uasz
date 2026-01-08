package com.uasz.daos.auth.controllers;

import com.uasz.daos.auth.model.Utilisateur;
import com.uasz.daos.auth.enums.Role;
import com.uasz.daos.auth.services.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/etudiant")
@PreAuthorize("hasAnyAuthority('ETUDIANT', 'ADMIN')")
// @CrossOrigin removed - CORS is handled by API Gateway
public class EtudiantController {

    private final UtilisateurService utilisateurService;

    public EtudiantController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Utilisateur>> getAllEtudiants() {
        List<Utilisateur> etudiants = utilisateurService.findByRole(Role.ETUDIANT);
        return ResponseEntity.ok(etudiants);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, String>> getEtudiantProfile() {
        return ResponseEntity.ok(Map.of(
                "message", "Profil étudiant",
                "status", "success"
        ));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getEtudiantsCount() {
        long count = utilisateurService.countByRole(Role.ETUDIANT);
        return ResponseEntity.ok(count);
    }
}