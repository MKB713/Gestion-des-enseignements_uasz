package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.model.Remplacement;
import com.uasz.daos.emploitemps.model.StatutRemplacement;
import com.uasz.daos.emploitemps.service.RemplacementService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller pour la gestion des remplacements d'enseignants
 * User Story: "Gérer les remplacements"
 */
@RestController
@RequestMapping("/api")
public class RemplacementController {

    @Autowired
    private RemplacementService remplacementService;

    /**
     * POST /api/seances/{id}/remplacer
     * Créer une demande de remplacement pour une séance
     *
     * Body JSON attendu:
     * {
     *   "enseignantRemplacantId": 123,
     *   "raison": "Enseignant malade",
     *   "temporaire": true,
     *   "creePar": "chef_departement"
     * }
     */
    @PostMapping("/seances/{id}/remplacer")
    public ResponseEntity<Remplacement> remplacerEnseignant(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {

        try {
            Long enseignantRemplacantId = Long.valueOf(body.get("enseignantRemplacantId").toString());
            String raison = body.getOrDefault("raison", "Non spécifiée").toString();
            Boolean temporaire = Boolean.valueOf(body.getOrDefault("temporaire", true).toString());
            String creePar = body.getOrDefault("creePar", "system").toString();

            Remplacement remplacement = remplacementService.creerRemplacement(
                    id, enseignantRemplacantId, raison, temporaire, creePar);

            return ResponseEntity.status(HttpStatus.CREATED).body(remplacement);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * GET /api/remplacements
     * Récupère tous les remplacements
     */
    @GetMapping("/remplacements")
    public ResponseEntity<List<Remplacement>> getAllRemplacements() {
        List<Remplacement> remplacements = remplacementService.getAllRemplacements();
        return ResponseEntity.ok(remplacements);
    }

    /**
     * GET /api/remplacements/{id}
     * Récupère un remplacement par ID
     */
    @GetMapping("/remplacements/{id}")
    public ResponseEntity<Remplacement> getRemplacementById(@PathVariable Long id) {
        try {
            Remplacement remplacement = remplacementService.getRemplacementById(id);
            return ResponseEntity.ok(remplacement);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PUT /api/remplacements/{id}/accepter
     * Accepter une demande de remplacement
     *
     * Body JSON attendu:
     * {
     *   "commentaire": "J'accepte de remplacer",
     *   "utilisateur": "enseignant_remplacant"
     * }
     */
    @PutMapping("/remplacements/{id}/accepter")
    public ResponseEntity<Remplacement> accepterRemplacement(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {

        try {
            String commentaire = body != null ? body.get("commentaire") : null;
            String utilisateur = body != null ? body.getOrDefault("utilisateur", "system") : "system";

            Remplacement remplacement = remplacementService.accepterRemplacement(id, commentaire, utilisateur);
            return ResponseEntity.ok(remplacement);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * PUT /api/remplacements/{id}/refuser
     * Refuser une demande de remplacement
     *
     * Body JSON attendu:
     * {
     *   "commentaire": "Non disponible ce jour-là",
     *   "utilisateur": "enseignant_remplacant"
     * }
     */
    @PutMapping("/remplacements/{id}/refuser")
    public ResponseEntity<Remplacement> refuserRemplacement(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {

        try {
            String commentaire = body != null ? body.get("commentaire") : "Non spécifié";
            String utilisateur = body != null ? body.getOrDefault("utilisateur", "system") : "system";

            Remplacement remplacement = remplacementService.refuserRemplacement(id, commentaire, utilisateur);
            return ResponseEntity.ok(remplacement);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * DELETE /api/remplacements/{id}
     * Annuler un remplacement
     */
    @DeleteMapping("/remplacements/{id}")
    public ResponseEntity<Map<String, String>> annulerRemplacement(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "system") String utilisateur) {

        try {
            remplacementService.annulerRemplacement(id, utilisateur);
            return ResponseEntity.ok(Map.of(
                    "message", "Remplacement annulé avec succès",
                    "remplacementId", id.toString()
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/seances/{id}/remplacements
     * Récupère l'historique des remplacements d'une séance
     */
    @GetMapping("/seances/{id}/remplacements")
    public ResponseEntity<List<Remplacement>> getRemplacementsBySeance(@PathVariable Long id) {
        List<Remplacement> remplacements = remplacementService.getRemplacementsBySeance(id);
        return ResponseEntity.ok(remplacements);
    }

    /**
     * GET /api/enseignants/{id}/remplacements/remplace
     * Récupère les remplacements où l'enseignant a été remplacé
     */
    @GetMapping("/enseignants/{id}/remplacements/remplace")
    public ResponseEntity<List<Remplacement>> getRemplacementsEnseignantRemplace(@PathVariable Long id) {
        List<Remplacement> remplacements = remplacementService.getRemplacementsByEnseignantRemplace(id);
        return ResponseEntity.ok(remplacements);
    }

    /**
     * GET /api/enseignants/{id}/remplacements/remplacant
     * Récupère les remplacements où l'enseignant est remplaçant
     */
    @GetMapping("/enseignants/{id}/remplacements/remplacant")
    public ResponseEntity<List<Remplacement>> getRemplacementsEnseignantRemplacant(@PathVariable Long id) {
        List<Remplacement> remplacements = remplacementService.getRemplacementsByEnseignantRemplacant(id);
        return ResponseEntity.ok(remplacements);
    }

    /**
     * GET /api/enseignants/{id}/remplacements/en-attente
     * Récupère les demandes de remplacement en attente pour un enseignant
     */
    @GetMapping("/enseignants/{id}/remplacements/en-attente")
    public ResponseEntity<List<Remplacement>> getRemplacementsEnAttente(@PathVariable Long id) {
        List<Remplacement> remplacements = remplacementService.getRemplacementsEnAttente(id);
        return ResponseEntity.ok(remplacements);
    }

    /**
     * GET /api/remplacements/statut/{statut}
     * Récupère les remplacements par statut
     */
    @GetMapping("/remplacements/statut/{statut}")
    public ResponseEntity<List<Remplacement>> getRemplacementsByStatut(@PathVariable String statut) {
        try {
            StatutRemplacement statutEnum = StatutRemplacement.valueOf(statut.toUpperCase());
            List<Remplacement> remplacements = remplacementService.getRemplacementsByStatut(statutEnum);
            return ResponseEntity.ok(remplacements);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/seances/{id}/remplacements/count
     * Compte le nombre de remplacements pour une séance
     */
    @GetMapping("/seances/{id}/remplacements/count")
    public ResponseEntity<Map<String, Long>> countRemplacementsBySeance(@PathVariable Long id) {
        long count = remplacementService.countRemplacementsBySeance(id);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * GET /api/seances/{id}/remplacements/actif
     * Vérifie si une séance a un remplacement actif
     */
    @GetMapping("/seances/{id}/remplacements/actif")
    public ResponseEntity<Map<String, Boolean>> hasRemplacementActif(@PathVariable Long id) {
        boolean hasActif = remplacementService.hasRemplacementActif(id);
        return ResponseEntity.ok(Map.of("hasRemplacementActif", hasActif));
    }
}