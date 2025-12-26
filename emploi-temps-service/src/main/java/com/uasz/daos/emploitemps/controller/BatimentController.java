package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.model.Batiment;
import com.uasz.daos.emploitemps.service.BatimentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller pour la gestion des bâtiments
 */
@RestController
@RequestMapping("/api/batiments")
public class BatimentController {

    @Autowired
    private BatimentService batimentService;

    /**
     * GET /api/batiments
     * Récupère tous les bâtiments
     */
    @GetMapping
    public ResponseEntity<List<Batiment>> listerBatiments() {
        List<Batiment> batiments = batimentService.getAllBatiments();
        return ResponseEntity.ok(batiments);
    }

    /**
     * GET /api/batiments/{id}
     * Récupère un bâtiment par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Batiment> chercherBatiment(@PathVariable Long id) {
        try {
            Batiment batiment = batimentService.getBatimentById(id);
            return ResponseEntity.ok(batiment);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/batiments
     * Crée un nouveau bâtiment
     */
    @PostMapping
    public ResponseEntity<Batiment> creerBatiment(@RequestBody Batiment batiment) {
        try {
            Batiment savedBatiment = batimentService.createBatiment(batiment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBatiment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * PUT /api/batiments/{id}
     * Met à jour un bâtiment
     */
    @PutMapping("/{id}")
    public ResponseEntity<Batiment> modifierBatiment(@PathVariable Long id, @RequestBody Batiment batiment) {
        try {
            Batiment updatedBatiment = batimentService.updateBatiment(id, batiment);
            return ResponseEntity.ok(updatedBatiment);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/batiments/{id}
     * Supprime un bâtiment
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> supprimerBatiment(@PathVariable Long id) {
        try {
            batimentService.deleteBatiment(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Bâtiment supprimé avec succès",
                    "batimentId", id.toString()
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/batiments/search
     * Recherche des bâtiments par nom
     *
     * Param:
     * - q: Terme de recherche
     */
    @GetMapping("/search")
    public ResponseEntity<List<Batiment>> rechercherBatiments(@RequestParam String q) {
        List<Batiment> batiments = batimentService.searchBatiments(q);
        return ResponseEntity.ok(batiments);
    }

    /**
     * GET /api/batiments/{id}/salles/count
     * Compte le nombre de salles dans un bâtiment
     */
    @GetMapping("/{id}/salles/count")
    public ResponseEntity<Map<String, Long>> compterSalles(@PathVariable Long id) {
        try {
            long count = batimentService.countSallesByBatiment(id);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}