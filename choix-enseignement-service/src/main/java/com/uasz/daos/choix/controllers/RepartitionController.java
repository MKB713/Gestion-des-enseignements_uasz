package com.uasz.daos.choix.controllers;

import com.uasz.daos.choix.model.Repartition;
import com.uasz.daos.choix.services.RepartitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repartitions")
@RequiredArgsConstructor
public class RepartitionController {

    private final RepartitionService repartitionService;

    @PostMapping
    public ResponseEntity<Repartition> ajouterRepartition(@RequestBody Repartition repartition) {
        Repartition saved = repartitionService.ajouterRepartition(repartition);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/maquette/{maquetteId}")
    public ResponseEntity<List<Repartition>> listerParMaquette(@PathVariable Long maquetteId) {
        return ResponseEntity.ok(repartitionService.listerParMaquette(maquetteId));
    }

    @GetMapping("/enseignant/{enseignantId}")
    public ResponseEntity<List<Repartition>> listerParEnseignant(@PathVariable Long enseignantId) {
        return ResponseEntity.ok(repartitionService.listerParEnseignant(enseignantId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Repartition> modifierRepartition(@PathVariable Long id,
            @RequestBody Repartition repartition) {
        return ResponseEntity.ok(repartitionService.modifierRepartition(id, repartition));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerRepartition(@PathVariable Long id) {
        repartitionService.supprimerRepartition(id);
        return ResponseEntity.noContent().build();
    }
}
