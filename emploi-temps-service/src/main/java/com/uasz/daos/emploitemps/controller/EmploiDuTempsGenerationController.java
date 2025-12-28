package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.dto.generation.ConflitsResponse;
import com.uasz.daos.emploitemps.dto.generation.GenerationResponse;
import com.uasz.daos.emploitemps.service.EmploiDuTempsGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller pour la génération automatique d'emplois du temps
 * Utilise le service Python pour l'optimisation
 */
@RestController
@RequestMapping("/api/generation")
public class EmploiDuTempsGenerationController {

    @Autowired
    private EmploiDuTempsGenerationService generationService;

    /**
     * POST /api/generation/generer
     * Génère automatiquement un emploi du temps optimal
     *
     * Body JSON:
     * {
     *   "dateDebut": "2024-03-01",
     *   "dateFin": "2024-06-30",
     *   "classeIds": [12, 15, 18],
     *   "creePar": "chef_departement"
     * }
     */
    @PostMapping("/generer")
    public ResponseEntity<GenerationResponse> genererEmploiDuTemps(
            @RequestBody Map<String, Object> requestBody) {

        try {
            // Parser les paramètres
            LocalDate dateDebut = LocalDate.parse(requestBody.get("dateDebut").toString());
            LocalDate dateFin = LocalDate.parse(requestBody.get("dateFin").toString());

            @SuppressWarnings("unchecked")
            List<Long> classeIds = (List<Long>) requestBody.get("classeIds");

            String creePar = requestBody.getOrDefault("creePar", "system").toString();

            // Générer
            GenerationResponse response = generationService.genererEmploiDuTempsAutomatique(
                    dateDebut, dateFin, classeIds, creePar);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * GET /api/generation/verifier-conflits
     * Vérifie les conflits dans les séances existantes
     *
     * Params:
     * - dateDebut: Date de début (format: yyyy-MM-dd)
     * - dateFin: Date de fin (format: yyyy-MM-dd)
     */
    @GetMapping("/verifier-conflits")
    public ResponseEntity<ConflitsResponse> verifierConflits(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        try {
            ConflitsResponse response = generationService.verifierConflitsSeances(dateDebut, dateFin);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/generation/test-python
     * Teste la connexion avec le service Python
     */
    @GetMapping("/test-python")
    public ResponseEntity<Map<String, Object>> testerConnexionPython() {
        boolean available = generationService.testerConnexionPython();

        return ResponseEntity.ok(Map.of(
                "pythonServiceAvailable", available,
                "status", available ? "OK" : "UNAVAILABLE",
                "message", available ?
                        "Service Python accessible" :
                        "Service Python non disponible"
        ));
    }

    /**
     * GET /api/generation/status
     * Statut du service de génération
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        boolean pythonAvailable = generationService.testerConnexionPython();

        return ResponseEntity.ok(Map.of(
                "serviceStatus", "running",
                "pythonGeneratorStatus", pythonAvailable ? "available" : "unavailable",
                "version", "1.0.0"
        ));
    }

    // ========== MÉTHODES UTILITAIRES ==========

    private GenerationResponse createErrorResponse(String errorMessage) {
        GenerationResponse response = new GenerationResponse();
        response.setSuccess(false);
        response.setMessage(errorMessage);
        response.setSeances(List.of());
        response.setConflits(List.of(errorMessage));
        response.setStatistiques(Map.of());
        return response;
    }
}