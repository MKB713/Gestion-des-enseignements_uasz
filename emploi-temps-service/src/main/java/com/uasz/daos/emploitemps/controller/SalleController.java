package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.model.Salle;
import com.uasz.daos.emploitemps.service.SalleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Controller pour la gestion des salles
 * User Story: "Récupérer les plannings des salles"
 */
@RestController
@RequestMapping("/api/salles")
public class SalleController {

    @Autowired
    private SalleService salleService;

    /**
     * GET /api/salles
     * Récupère toutes les salles
     */
    @GetMapping
    public ResponseEntity<List<Salle>> getAllSalles() {
        List<Salle> salles = salleService.getAllSalles();
        return ResponseEntity.ok(salles);
    }

    /**
     * GET /api/salles/{id}
     * Récupère une salle par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Salle> getSalleById(@PathVariable Long id) {
        try {
            Salle salle = salleService.getSalleById(id);
            return ResponseEntity.ok(salle);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/salles
     * Crée une nouvelle salle
     */
    @PostMapping
    public ResponseEntity<Salle> createSalle(@RequestBody Salle salle) {
        try {
            Salle savedSalle = salleService.createSalle(salle);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSalle);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/salles/{id}
     * Met à jour une salle
     */
    @PutMapping("/{id}")
    public ResponseEntity<Salle> updateSalle(@PathVariable Long id, @RequestBody Salle salleDetails) {
        try {
            Salle updatedSalle = salleService.updateSalle(id, salleDetails);
            return ResponseEntity.ok(updatedSalle);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/salles/{id}
     * Supprime une salle
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSalle(@PathVariable Long id) {
        try {
            salleService.deleteSalle(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Salle supprimée avec succès",
                    "salleId", id.toString()
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== ENDPOINTS POUR LES DISPONIBILITÉS (USER STORY) ==========

    /**
     * GET /api/salles/disponibilites
     * Récupère les salles disponibles pour un créneau donné
     * User Story: "Récupérer les plannings des salles"
     *
     * Params requis:
     * - date: Date de la séance (format: yyyy-MM-dd)
     * - heureDebut: Heure de début (format: HH:mm)
     * - heureFin: Heure de fin (format: HH:mm)
     *
     * Params optionnels:
     * - capaciteMin: Capacité minimale requise
     *
     * Exemple: GET /api/salles/disponibilites?date=2024-03-15&heureDebut=08:00&heureFin=10:00&capaciteMin=50
     */
    @GetMapping("/disponibilites")
    public ResponseEntity<List<Salle>> getSallesDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureFin,
            @RequestParam(required = false) Integer capaciteMin) {

        try {
            List<Salle> sallesDisponibles;

            if (capaciteMin != null && capaciteMin > 0) {
                // Récupérer avec capacité minimale
                sallesDisponibles = salleService.getSallesDisponiblesAvecCapacite(
                        date, heureDebut, heureFin, capaciteMin);
            } else {
                // Récupérer toutes les salles disponibles
                sallesDisponibles = salleService.getSallesDisponibles(date, heureDebut, heureFin);
            }

            return ResponseEntity.ok(sallesDisponibles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/salles/{id}/disponible
     * Vérifie si une salle spécifique est disponible pour un créneau donné
     *
     * Params:
     * - date: Date de la séance (format: yyyy-MM-dd)
     * - heureDebut: Heure de début (format: HH:mm)
     * - heureFin: Heure de fin (format: HH:mm)
     *
     * Exemple: GET /api/salles/5/disponible?date=2024-03-15&heureDebut=08:00&heureFin=10:00
     */
    @GetMapping("/{id}/disponible")
    public ResponseEntity<Map<String, Boolean>> isSalleDisponible(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureFin) {

        try {
            boolean disponible = salleService.isSalleDisponible(id, date, heureDebut, heureFin);
            return ResponseEntity.ok(Map.of("disponible", disponible));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== AUTRES ENDPOINTS ==========

    /**
     * GET /api/salles/batiment/{id}
     * Récupère les salles d'un bâtiment
     */
    @GetMapping("/batiment/{id}")
    public ResponseEntity<List<Salle>> getSallesByBatiment(@PathVariable Long id) {
        List<Salle> salles = salleService.getSallesByBatiment(id);
        return ResponseEntity.ok(salles);
    }

    /**
     * GET /api/salles/search
     * Recherche des salles par libellé
     *
     * Param:
     * - q: Terme de recherche
     *
     * Exemple: GET /api/salles/search?q=Amphi
     */
    @GetMapping("/search")
    public ResponseEntity<List<Salle>> searchSalles(@RequestParam String q) {
        List<Salle> salles = salleService.searchSalles(q);
        return ResponseEntity.ok(salles);
    }

    /**
     * GET /api/salles/capacite/{min}
     * Récupère les salles avec une capacité minimale
     */
    @GetMapping("/capacite/{min}")
    public ResponseEntity<List<Salle>> getSallesByCapaciteMin(@PathVariable int min) {
        List<Salle> salles = salleService.getSallesByCapaciteMin(min);
        return ResponseEntity.ok(salles);
    }
}