package com.uasz.daos.choix.controller;

import com.uasz.daos.choix.client.MaquetteClient;
import com.uasz.daos.choix.dto.MaquetteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de test pour vérifier la communication avec le microservice Maquette
 */
@RestController
@RequestMapping("/api/test/maquettes")
public class MaquetteTestController {

    @Autowired
    private MaquetteClient maquetteClient;

    /**
     * Test : Récupérer une maquette par ID
     * URL: GET http://localhost:8084/api/test/maquettes/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<MaquetteDTO> getMaquette(@PathVariable Long id) {
        try {
            MaquetteDTO maquette = maquetteClient.getMaquetteById(id);
            return ResponseEntity.ok(maquette);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Test : Récupérer toutes les maquettes
     * URL: GET http://localhost:8084/api/test/maquettes
     */
    @GetMapping
    public ResponseEntity<List<MaquetteDTO>> getAllMaquettes() {
        try {
            List<MaquetteDTO> maquettes = maquetteClient.getAllMaquettes();
            return ResponseEntity.ok(maquettes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Test : Vérifier si une maquette existe
     * URL: GET http://localhost:8084/api/test/maquettes/1/exists
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existsMaquette(@PathVariable Long id) {
        try {
            Boolean exists = maquetteClient.existsById(id);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}