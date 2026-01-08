package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.model.Batiment;
import com.uasz.daos.emploitemps.service.BatimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/batiments")
public class BatimentController {

    @Autowired
    private BatimentService batimentService;

    @GetMapping
    public ResponseEntity<List<Batiment>> listerBatiments() {
        return ResponseEntity.ok(batimentService.getAllBatiments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Batiment> chercherBatiment(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(batimentService.getBatimentById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Batiment> creerBatiment(@RequestBody Batiment batiment) {
        try {
            Batiment savedBatiment = batimentService.createBatiment(batiment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBatiment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Batiment> modifierBatiment(@PathVariable Long id, @RequestBody Batiment batiment) {
        try {
            return ResponseEntity.ok(batimentService.updateBatiment(id, batiment));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> supprimerBatiment(@PathVariable Long id) {
        try {
            batimentService.deleteBatiment(id);
            return ResponseEntity.ok(Map.of("message", "Bâtiment supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
