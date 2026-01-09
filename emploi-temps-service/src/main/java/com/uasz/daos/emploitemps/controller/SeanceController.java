package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.model.Seance;
import com.uasz.daos.emploitemps.service.SeanceService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/seances")
public class SeanceController {

    @Autowired
    private SeanceService seanceService;

    @GetMapping
    public ResponseEntity<List<Seance>> getAllSeances() {
        return ResponseEntity.ok(seanceService.getAllSeances());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seance> getSeanceById(@PathVariable Long id) {
        return ResponseEntity.ok(seanceService.getSeanceById(id));
    }

    @PostMapping
    public ResponseEntity<?> createSeance(@RequestBody SeanceDTO seanceDTO) {
        try {
            Seance seance = new Seance();
            // Handle Date -> DayOfWeek logic
            if (seanceDTO.getDateSeance() != null) {
                LocalDate date = LocalDate.parse(seanceDTO.getDateSeance());
                seance.setJour(date.getDayOfWeek());
            } else if (seanceDTO.getJour() != null) {
                seance.setJour(seanceDTO.getJour());
            }

            // Time parsing (if needed, frontend sends string HH:mm:ss or HH:mm)
            // Assuming frontend sends "HH:mm:ss" or standard ISO time format suitable for
            // LocalTime.parse
            if (seanceDTO.getHeureDebut() != null) {
                seance.setHeureDebut(LocalTime.parse(seanceDTO.getHeureDebut()));
            }
            if (seanceDTO.getHeureFin() != null) {
                seance.setHeureFin(LocalTime.parse(seanceDTO.getHeureFin()));
            }

            seance.setSalleId(seanceDTO.getSalleId()); // Needs to set Salle object or ID depending on model
            // Model has 'Salle salle'. But Service might handle ID lookup or we just set ID
            // if it was just ID.
            // Wait, Seance model has 'Salle salle'. We need to fetch Salle.
            // But checking Seance.java:
            // @ManyToOne @JoinColumn(name = "salle_id") private Salle salle;
            // AND the repo/service saves it.
            // If we only have salleId, we should fetch the Salle proxy or object.
            // However, SeanceService.createSeance takes Seance.

            // Let's check if SeanceDTO can carry IDs and we construct Seance.
            // For Salle, we might need a workaround if we don't want to inject
            // SalleRepository here.
            // Actually, SeanceService should handle this or we can set a Salle object with
            // just ID (stub).
            if (seanceDTO.getSalleId() != null) {
                com.uasz.daos.emploitemps.model.Salle s = new com.uasz.daos.emploitemps.model.Salle();
                s.setId(seanceDTO.getSalleId());
                seance.setSalle(s);
            }

            seance.setEcId(seanceDTO.getEcId());
            seance.setEnseignantId(seanceDTO.getEnseignantId());
            seance.setClasseId(seanceDTO.getClasseId());
            seance.setSemestreId(seanceDTO.getSemestreId());

            return new ResponseEntity<>(seanceService.createSeance(seance), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la création: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Seance> updateSeance(@PathVariable Long id, @RequestBody Seance seance) {
        return ResponseEntity.ok(seanceService.updateSeance(id, seance));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeance(@PathVariable Long id) {
        seanceService.deleteSeance(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/enseignant/{id}")
    public ResponseEntity<List<Seance>> getSeancesByEnseignant(@PathVariable Long id) {
        return ResponseEntity.ok(seanceService.getSeancesByEnseignant(id));
    }

    @GetMapping("/salle/{id}")
    public ResponseEntity<List<Seance>> getSeancesBySalle(@PathVariable Long id) {
        return ResponseEntity.ok(seanceService.getSeancesBySalle(id));
    }

    @Data
    public static class SeanceDTO {
        private String dateSeance; // Custom field from frontend
        private DayOfWeek jour;
        private String heureDebut;
        private String heureFin;
        private Long salleId;
        private Long ecId;
        private Long enseignantId;
        private Long classeId;
        private Long semestreId;
    }
}
