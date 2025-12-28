package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.dto.SeanceDTO;
import com.uasz.daos.emploitemps.model.HistoriqueSeance;
import com.uasz.daos.emploitemps.model.Seance;
import com.uasz.daos.emploitemps.service.SeanceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SeanceController {

    @Autowired
    private SeanceService seanceService;

    @GetMapping("/seances")
    public List<Seance> getAllSeances() {
        return seanceService.getAllSeances();
    }

    @GetMapping("/seances/{id}")
    public Seance getSeanceById(@PathVariable Long id) {
        return seanceService.getSeanceById(id);
    }

    /**
     * Créer une séance avec utilisateur (optionnel)
     */
    @PostMapping("/seances")
    public ResponseEntity<Seance> createSeance(
            @RequestBody SeanceDTO seanceDTO,
            @RequestParam(required = false, defaultValue = "system") String utilisateur) {

        Seance seance = seanceService.createSeance(seanceDTO, utilisateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(seance);
    }

    /**
     * Modifier une séance avec utilisateur (optionnel)
     */
    @PutMapping("/seances/{id}")
    public ResponseEntity<Seance> updateSeance(
            @PathVariable Long id,
            @RequestBody SeanceDTO seanceDTO,
            @RequestParam(required = false, defaultValue = "system") String utilisateur) {

        try {
            Seance seance = seanceService.updateSeance(id, seanceDTO, utilisateur);
            return ResponseEntity.ok(seance);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Supprimer (annuler) une séance avec confirmation et raison
     *
     * Body JSON attendu :
     * {
     *   "raisonAnnulation": "Enseignant malade",
     *   "utilisateur": "admin"
     * }
     */
    @DeleteMapping("/seances/{id}")
    public ResponseEntity<Map<String, String>> deleteSeance(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {

        try {
            String raisonAnnulation = body != null ? body.get("raisonAnnulation") : null;
            String utilisateur = body != null ? body.getOrDefault("utilisateur", "system") : "system";

            seanceService.deleteSeance(id, raisonAnnulation, utilisateur);

            return ResponseEntity.ok(Map.of(
                    "message", "Séance annulée avec succès",
                    "seanceId", id.toString()
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/seances/{id}/historique
     * Récupère l'historique complet d'une séance
     */
    @GetMapping("/seances/{id}/historique")
    public ResponseEntity<List<HistoriqueSeance>> getHistoriqueSeance(@PathVariable Long id) {
        List<HistoriqueSeance> historique = seanceService.getHistoriqueSeance(id);

        if (historique.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(historique);
    }

    /**
     * GET /api/seances/annulees
     * Récupère toutes les séances annulées
     */
    @GetMapping("/seances/annulees")
    public ResponseEntity<List<Seance>> getSeancesAnnulees() {
        List<Seance> seances = seanceService.getSeancesAnnulees();
        return ResponseEntity.ok(seances);
    }

    @GetMapping("/planning/salle/{id}")
    public List<Seance> getPlanningBySalle(@PathVariable("id") Long salleId) {
        return seanceService.getBySalle(salleId);
    }

    @GetMapping("/seances/enseignant/{id}")
    public List<Seance> getSeancesByEnseignant(@PathVariable("id") Long enseignantId) {
        return seanceService.getByEnseignant(enseignantId);
    }
}