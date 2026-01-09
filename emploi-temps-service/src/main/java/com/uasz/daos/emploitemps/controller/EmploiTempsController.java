package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.model.Seance;
import com.uasz.daos.emploitemps.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emploi-du-temps")
public class EmploiTempsController {

    @Autowired
    private SeanceService seanceService;

    @GetMapping("/semaine")
    public ResponseEntity<Map<String, Object>> getEmploiDuTempsSemaine(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String filtrePar,
            @RequestParam(required = false) Long filtreId) {

        // Si aucune date n'est fournie, on prend la date du jour, mais ici on retourne
        // tout le planning
        // car Seance est basé sur "Jour de la semaine" (récurrent).
        // Le filtrage par date se ferait si on avait des séances "datées".
        // Pour l'instant, on retourne l'emploi du temps TYPE (récurrent).

        List<Seance> seances = seanceService.getAllSeances();

        // Filtrage optionnel
        if (filtrePar != null && filtreId != null) {
            if ("enseignant".equalsIgnoreCase(filtrePar)) {
                seances = seanceService.getSeancesByEnseignant(filtreId);
            } else if ("salle".equalsIgnoreCase(filtrePar)) {
                seances = seanceService.getSeancesBySalle(filtreId);
            }
        }

        // Grouper par jour (DayOfWeek)
        // Le frontend attend peut-être "MONDAY", "TUESDAY" ou "LUNDI"...
        // Vérifions AdminPlannings.jsx handling.
        // Il fait: const allSeances = Object.values(data.seancesParJour).flat();
        // Donc il s'attend à Map<String, List<Seance>>.

        Map<String, List<Seance>> seancesParJour = seances.stream()
                .filter(s -> s.getJour() != null)
                .collect(Collectors.groupingBy(s -> s.getJour().name()));

        Map<String, Object> response = new HashMap<>();
        response.put("seancesParJour", seancesParJour);
        // On pourrait ajouter infos sur la semaine, etc.

        return ResponseEntity.ok(response);
    }
}
