package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.model.Deroulement;
import com.uasz.daos.emploitemps.service.DeroulementService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller pour la gestion du déroulement des séances
 * Permet de suivre l'exécution effective des séances planifiées
 */
@RestController
@RequestMapping("/api/deroulements")
public class DeroulementController {

    @Autowired
    private DeroulementService deroulementService;

    /**
     * GET /api/deroulements
     * Récupère tous les déroulements
     */
    @GetMapping
    public ResponseEntity<List<Deroulement>> listerDeroulements() {
        List<Deroulement> deroulements = deroulementService.listerTout();
        return ResponseEntity.ok(deroulements);
    }

    /**
     * GET /api/deroulements/{id}
     * Récupère un déroulement par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Deroulement> chercherDeroulement(@PathVariable Long id) {
        try {
            Deroulement deroulement = deroulementService.chercher(id);
            return ResponseEntity.ok(deroulement);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/deroulements
     * Crée un nouveau déroulement
     */
    @PostMapping
    public ResponseEntity<Deroulement> creerDeroulement(@RequestBody Deroulement deroulement) {
        try {
            Deroulement savedDeroulement = deroulementService.enregistrer(deroulement);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDeroulement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * PUT /api/deroulements/{id}
     * Met à jour un déroulement
     */
    @PutMapping("/{id}")
    public ResponseEntity<Deroulement> modifierDeroulement(@PathVariable Long id, @RequestBody Deroulement deroulement) {
        try {
            Deroulement updatedDeroulement = deroulementService.modifier(id, deroulement);
            return ResponseEntity.ok(updatedDeroulement);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/deroulements/{id}
     * Supprime un déroulement
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> supprimerDeroulement(@PathVariable Long id) {
        try {
            deroulementService.supprimer(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Déroulement supprimé avec succès",
                    "deroulementId", id.toString()
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/deroulements/seance/{id}
     * Récupère le déroulement d'une séance
     */
    @GetMapping("/seance/{id}")
    public ResponseEntity<Deroulement> getDeroulementBySeance(@PathVariable Long id) {
        try {
            Deroulement deroulement = deroulementService.getBySeance(id);
            return ResponseEntity.ok(deroulement);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/deroulements/enseignant/{id}
     * Récupère les déroulements d'un enseignant
     */
    @GetMapping("/enseignant/{id}")
    public ResponseEntity<List<Deroulement>> getDeroulementsByEnseignant(@PathVariable Long id) {
        List<Deroulement> deroulements = deroulementService.getByEnseignant(id);
        return ResponseEntity.ok(deroulements);
    }

    /**
     * POST /api/deroulements/{id}/valider
     * Valide un déroulement de séance
     *
     * Body JSON:
     * {
     *   "commentaire": "Séance effectuée correctement",
     *   "utilisateur": "enseignant_123"
     * }
     */
    @PostMapping("/{id}/valider")
    public ResponseEntity<Deroulement> validerDeroulement(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {

        try {
            String commentaire = body != null ? body.get("commentaire") : null;
            String utilisateur = body != null ? body.getOrDefault("utilisateur", "system") : "system";

            Deroulement deroulement = deroulementService.valider(id, commentaire, utilisateur);
            return ResponseEntity.ok(deroulement);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * POST /api/deroulements/{id}/invalider
     * Invalide un déroulement de séance
     *
     * Body JSON:
     * {
     *   "commentaire": "Séance non effectuée",
     *   "utilisateur": "chef_departement"
     * }
     */
    @PostMapping("/{id}/invalider")
    public ResponseEntity<Deroulement> invaliderDeroulement(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {

        try {
            String commentaire = body != null ? body.get("commentaire") : null;
            String utilisateur = body != null ? body.getOrDefault("utilisateur", "system") : "system";

            Deroulement deroulement = deroulementService.invalider(id, commentaire, utilisateur);
            return ResponseEntity.ok(deroulement);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * GET /api/deroulements/valides
     * Récupère tous les déroulements validés
     */
    @GetMapping("/valides")
    public ResponseEntity<List<Deroulement>> getDeroulementsValides() {
        List<Deroulement> deroulements = deroulementService.getValides();
        return ResponseEntity.ok(deroulements);
    }

    /**
     * GET /api/deroulements/en-attente
     * Récupère tous les déroulements en attente de validation
     */
    @GetMapping("/en-attente")
    public ResponseEntity<List<Deroulement>> getDeroulementsEnAttente() {
        List<Deroulement> deroulements = deroulementService.getEnAttente();
        return ResponseEntity.ok(deroulements);
    }
}