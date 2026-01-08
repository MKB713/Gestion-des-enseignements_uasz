package com.uasz.daos.maquette.controller;

import com.uasz.daos.maquette.model.Niveau;
import com.uasz.daos.maquette.service.NiveauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maquette/niveaux")

public class NiveauController {

    @Autowired
    private NiveauService niveauService;

    @GetMapping
    public ResponseEntity<List<Niveau>> getAllNiveaux() {
        return ResponseEntity.ok(niveauService.getAllNiveaux());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Niveau> getNiveauById(@PathVariable Long id) {
        return niveauService.getNiveauById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Niveau> createNiveau(@RequestBody Niveau niveau) {
        Niveau saved = niveauService.saveNiveau(niveau);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Niveau> updateNiveau(@PathVariable Long id, @RequestBody Niveau niveau) {
        niveau.setId(id);
        Niveau updated = niveauService.saveNiveau(niveau);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNiveau(@PathVariable Long id) {
        niveauService.deleteNiveau(id);
        return ResponseEntity.noContent().build();
    }
}