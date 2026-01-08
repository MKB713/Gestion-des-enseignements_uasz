package com.uasz.daos.maquette.controller;

import com.uasz.daos.maquette.model.Filiere;
import com.uasz.daos.maquette.service.FiliereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maquette/filieres")

public class FiliereController {

    @Autowired
    private FiliereService filiereService;

    // GET /api/maquette/filieres
    @GetMapping
    public ResponseEntity<List<Filiere>> getAllFilieres() {
        return new ResponseEntity<>(filiereService.getAllFiliere(), HttpStatus.OK);
    }

    // GET /api/maquette/filieres/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Filiere> getFiliereById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(filiereService.getFiliereById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/maquette/filieres (Ajout)
    @PostMapping
    public ResponseEntity<Filiere> createFiliere(@RequestBody Filiere filiere) {
        Filiere saved = filiereService.save(filiere);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // PUT /api/maquette/filieres/{id} (Modif)
    @PutMapping("/{id}")
    public ResponseEntity<Filiere> updateFiliere(@PathVariable Long id, @RequestBody Filiere filiere) {
        filiere.setId(id); // Sécurité
        Filiere updated = filiereService.save(filiere);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // DELETE /api/maquette/filieres/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFiliere(@PathVariable Long id) {
        filiereService.delete(id);
        return ResponseEntity.noContent().build();
    }
}