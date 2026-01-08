package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.model.Salle;
import com.uasz.daos.emploitemps.service.SalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/salles")
public class SalleController {

    @Autowired
    private SalleService salleService;

    @GetMapping
    public ResponseEntity<List<Salle>> listerSalles() {
        return ResponseEntity.ok(salleService.getAllSalles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Salle> chercherSalle(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(salleService.getSalleById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Salle> creerSalle(@RequestBody Salle salle) {
        try {
            Salle savedSalle = salleService.createSalle(salle);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSalle);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Salle> modifierSalle(@PathVariable Long id, @RequestBody Salle salle) {
        try {
            return ResponseEntity.ok(salleService.updateSalle(id, salle));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> supprimerSalle(@PathVariable Long id) {
        try {
            salleService.deleteSalle(id);
            return ResponseEntity.ok(Map.of("message", "Salle supprimée avec succès"));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
