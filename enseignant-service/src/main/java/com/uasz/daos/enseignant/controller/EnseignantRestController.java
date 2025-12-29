package com.uasz.daos.enseignant.controller;

import com.uasz.daos.enseignant.dto.EnseignantUpdateDTO;
import com.uasz.daos.enseignant.model.Enseignant;
import com.uasz.daos.enseignant.enums.Statut;
import com.uasz.daos.enseignant.service.EnseignantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * API REST pour la gestion des enseignants
 * Utilisée par le proxy du auth-service
 */
@RestController
@RequestMapping("/api/enseignants")
public class EnseignantRestController {

    @Autowired
    private EnseignantService enseignantService;

    /**
     * Récupérer tous les enseignants actifs
     */
    @GetMapping
    public ResponseEntity<List<Enseignant>> getAllEnseignants() {
        List<Enseignant> enseignants = enseignantService.getAllEnseignants();
        return ResponseEntity.ok(enseignants);
    }

    /**
     * Récupérer tous les enseignants archivés
     */
    @GetMapping("/archives")
    public ResponseEntity<List<Enseignant>> getAllEnseignantsArchives() {
        List<Enseignant> enseignants = enseignantService.getAllEnseignantsArchives();
        return ResponseEntity.ok(enseignants);
    }

    /**
     * Récupérer un enseignant par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Enseignant> getEnseignantById(@PathVariable Long id) {
        try {
            Enseignant enseignant = enseignantService.getEnseignantById(id);
            return ResponseEntity.ok(enseignant);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Créer un nouvel enseignant
     */
    @PostMapping
//    public ResponseEntity<Enseignant> createEnseignant(@RequestBody Enseignant enseignant) {
//        try {
//            Enseignant saved = enseignantService.saveEnseignant(enseignant);
//            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    /**
     * Mettre à jour un enseignant
     */
    @PutMapping("/{id}")
    public ResponseEntity<Enseignant> updateEnseignant(@PathVariable Long id, @RequestBody Enseignant enseignant) {
        try {
            Enseignant updated = enseignantService.updateEnseignant(id, enseignant);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Archiver un enseignant
     */
    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveEnseignant(@PathVariable Long id) {
        try {
            enseignantService.archiverEnseignant(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Désarchiver un enseignant
     */
    @PostMapping("/{id}/unarchive")
    public ResponseEntity<Void> unarchiveEnseignant(@PathVariable Long id) {
        try {
            enseignantService.desarchiverEnseignant(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Activer un enseignant
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateEnseignant(@PathVariable Long id) {
        try {
            enseignantService.activerEnseignant(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Désactiver un enseignant
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateEnseignant(@PathVariable Long id) {
        try {
            enseignantService.desactiverEnseignant(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
