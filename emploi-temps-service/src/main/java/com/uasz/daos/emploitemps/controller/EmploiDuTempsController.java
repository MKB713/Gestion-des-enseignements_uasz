package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.dto.EmploiDuTempsDTO;
import com.uasz.daos.emploitemps.model.Seance;
import com.uasz.daos.emploitemps.service.EmploiDuTempsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/emploi-du-temps")
public class EmploiDuTempsController {

    @Autowired
    private EmploiDuTempsService emploiDuTempsService;

    /**
     * GET /api/emploi-du-temps/semaine
     * Récupère l'emploi du temps hebdomadaire (lundi-samedi)
     *
     * @param date Date de référence (optionnelle, par défaut = aujourd'hui)
     * @param filtrePar Type de filtre : ENSEIGNANT, SALLE, EC (optionnel)
     * @param filtreId ID de l'entité à filtrer (optionnel)
     * @return EmploiDuTempsDTO avec séances organisées par jour
     *
     * Exemples :
     * - /api/emploi-du-temps/semaine
     * - /api/emploi-du-temps/semaine?date=2024-01-15
     * - /api/emploi-du-temps/semaine?filtrePar=ENSEIGNANT&filtreId=123
     * - /api/emploi-du-temps/semaine?date=2024-01-15&filtrePar=SALLE&filtreId=5
     */
    @GetMapping("/semaine")
    public ResponseEntity<EmploiDuTempsDTO> getPlanningHebdomadaire(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String filtrePar,
            @RequestParam(required = false) Long filtreId) {

        // Par défaut, utiliser la date du jour
        LocalDate dateReference = (date != null) ? date : LocalDate.now();

        EmploiDuTempsDTO planning = emploiDuTempsService.getPlanningHebdomadaire(
                dateReference, filtrePar, filtreId);

        return ResponseEntity.ok(planning);
    }

    /**
     * GET /api/emploi-du-temps/semestre
     * Récupère l'emploi du temps semestriel
     *
     * @param dateDebut Date de début du semestre (obligatoire)
     * @param dateFin Date de fin du semestre (obligatoire)
     * @param filtrePar Type de filtre : ENSEIGNANT, SALLE, EC (optionnel)
     * @param filtreId ID de l'entité à filtrer (optionnel)
     * @return EmploiDuTempsDTO avec toutes les séances du semestre
     *
     * Exemples :
     * - /api/emploi-du-temps/semestre?dateDebut=2024-01-01&dateFin=2024-06-30
     * - /api/emploi-du-temps/semestre?dateDebut=2024-01-01&dateFin=2024-06-30&filtrePar=ENSEIGNANT&filtreId=123
     */
    @GetMapping("/semestre")
    public ResponseEntity<EmploiDuTempsDTO> getPlanningSemestriel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String filtrePar,
            @RequestParam(required = false) Long filtreId) {

        // Validation
        if (dateDebut.isAfter(dateFin)) {
            return ResponseEntity.badRequest().build();
        }

        EmploiDuTempsDTO planning = emploiDuTempsService.getPlanningSemestriel(
                dateDebut, dateFin, filtrePar, filtreId);

        return ResponseEntity.ok(planning);
    }

    /**
     * GET /api/emploi-du-temps/enseignant/{enseignantId}/semaine
     * Raccourci : planning hebdomadaire d'un enseignant
     *
     * @param enseignantId ID de l'enseignant
     * @param date Date de référence (optionnelle)
     */
    @GetMapping("/enseignant/{enseignantId}/semaine")
    public ResponseEntity<EmploiDuTempsDTO> getPlanningEnseignantSemaine(
            @PathVariable Long enseignantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate dateReference = (date != null) ? date : LocalDate.now();

        EmploiDuTempsDTO planning = emploiDuTempsService.getPlanningHebdomadaire(
                dateReference, "ENSEIGNANT", enseignantId);

        return ResponseEntity.ok(planning);
    }

    /**
     * GET /api/emploi-du-temps/salle/{salleId}/semaine
     * Raccourci : planning hebdomadaire d'une salle
     *
     * @param salleId ID de la salle
     * @param date Date de référence (optionnelle)
     */
    @GetMapping("/salle/{salleId}/semaine")
    public ResponseEntity<EmploiDuTempsDTO> getPlanningSalleSemaine(
            @PathVariable Long salleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate dateReference = (date != null) ? date : LocalDate.now();

        EmploiDuTempsDTO planning = emploiDuTempsService.getPlanningHebdomadaire(
                dateReference, "SALLE", salleId);

        return ResponseEntity.ok(planning);
    }

    /**
     * GET /api/seances/search
     * Recherche multicritère de séances
     *
     * @param enseignantId Filtrer par enseignant (optionnel)
     * @param salleId Filtrer par salle (optionnel)
     * @param ecId Filtrer par EC (optionnel)
     * @param dateDebut Filtrer à partir de cette date (optionnel)
     * @param dateFin Filtrer jusqu'à cette date (optionnel)
     * @return Liste des séances correspondantes
     *
     * Exemples :
     * - /api/seances/search?enseignantId=123
     * - /api/seances/search?salleId=5&dateDebut=2024-01-01
     * - /api/seances/search?ecId=42&dateDebut=2024-01-01&dateFin=2024-06-30
     */
    @GetMapping("/search")
    public ResponseEntity<List<Seance>> rechercherSeances(
            @RequestParam(required = false) Long enseignantId,
            @RequestParam(required = false) Long salleId,
            @RequestParam(required = false) Long ecId,
            @RequestParam(required = false) Long classeId, // Added classeId parameter
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        List<Seance> seances = emploiDuTempsService.rechercherSeances(
                enseignantId, salleId, ecId, classeId, dateDebut, dateFin); // Pass classeId

        return ResponseEntity.ok(seances);
    }
}