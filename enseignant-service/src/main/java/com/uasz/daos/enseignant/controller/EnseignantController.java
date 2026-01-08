package com.uasz.daos.enseignant.controller;

import com.uasz.daos.enseignant.model.Enseignant;
import com.uasz.daos.enseignant.enums.Statut; // Assurez-vous d'avoir cet Enum ou créez-le
import com.uasz.daos.enseignant.service.EnseignantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/enseignants") // L'URL sera /api/enseignants via le Gateway
public class EnseignantController {

    @Autowired
    private EnseignantService enseignantService;

    // ==========================================
    // 1. LECTURE (GET)
    // ==========================================

    /**
     * Récupérer tous les enseignants ACTIFS
     */
    @GetMapping
    public ResponseEntity<List<Enseignant>> getAllEnseignants() {
        return ResponseEntity.ok(enseignantService.getAllEnseignants());
    }

    /**
     * Récupérer tous les enseignants ARCHIVÉS (Corbeille)
     */
    @GetMapping("/archives")
    public ResponseEntity<List<Enseignant>> getEnseignantsArchives() {
        return ResponseEntity.ok(enseignantService.getAllEnseignantsArchives());
    }

    /**
     * Récupérer un enseignant par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Enseignant> getEnseignantById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(enseignantService.getEnseignantById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ==========================================
    // 2. ECRITURE (POST / PUT)
    // ==========================================

    /**
     * Créer un nouvel enseignant
     */
    @PostMapping
    public ResponseEntity<?> createEnseignant(@RequestBody Enseignant enseignant) {
        try {
            Enseignant saved = enseignantService.saveEnseignant(enseignant);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur : " + e.getMessage());
        }
    }

    /**
     * Modifier un enseignant existant
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEnseignant(@PathVariable Long id, @RequestBody Enseignant enseignant) {
        try {
            Enseignant updated = enseignantService.updateEnseignant(id, enseignant);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ==========================================
    // 3. ACTIONS (PATCH)
    // ==========================================

    @PatchMapping("/{id}/archiver")
    public ResponseEntity<?> archiver(@PathVariable Long id) {
        try {
            enseignantService.archiverEnseignant(id);
            return ResponseEntity.ok().body("Enseignant archivé.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/restaurer") // Remplace 'unarchive-enseignant'
    public ResponseEntity<?> restaurer(@PathVariable Long id) {
        try {
            enseignantService.desarchiverEnseignant(id);
            return ResponseEntity.ok().body("Enseignant restauré.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<?> activer(@PathVariable Long id) {
        try {
            enseignantService.activerEnseignant(id);
            return ResponseEntity.ok().body("Enseignant activé.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<?> desactiver(@PathVariable Long id) {
        try {
            enseignantService.desactiverEnseignant(id);
            return ResponseEntity.ok().body("Enseignant désactivé.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ==========================================
    // 4. REFERENCES (Pour les listes déroulantes React)
    // ==========================================

    @GetMapping("/ref/grades")
    public ResponseEntity<List<String>> getGrades() {
        return ResponseEntity.ok(List.of(
                "Assistant",
                "Maître-Assistant",
                "Maître de Conférences",
                "Professeur Titulaire",
                "Professeur Assimilé"));
    }

    @GetMapping("/ref/statuts")
    public ResponseEntity<List<Statut>> getStatuts() {
        return ResponseEntity.ok(Arrays.asList(Statut.values()));
    }
}