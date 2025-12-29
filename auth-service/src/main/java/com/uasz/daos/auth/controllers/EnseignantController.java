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
@RequestMapping("/api/enseignant")
@PreAuthorize("hasAnyAuthority('ENSEIGNANT', 'ADMIN', 'CHEF_DE_DEPARTEMENT')")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class EnseignantController {

    private final UtilisateurService utilisateurService;

    public EnseignantController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Utilisateur>> getAllEnseignants() {
        List<Utilisateur> enseignants = utilisateurService.findByRole(Role.ENSEIGNANT);
        return ResponseEntity.ok(enseignants);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, String>> getEnseignantProfile() {
        return ResponseEntity.ok(Map.of(
                "message", "Profil enseignant",
                "status", "success"
        ));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getEnseignantsCount() {
        long count = utilisateurService.countByRole(Role.ENSEIGNANT);
        return ResponseEntity.ok(count);
    }
}