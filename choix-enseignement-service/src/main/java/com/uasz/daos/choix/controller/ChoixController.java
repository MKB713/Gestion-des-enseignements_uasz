package com.uasz.daos.choix.controller;

import com.uasz.daos.choix.model.Choix;
import com.uasz.daos.choix.service.ChoixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/choix") // Gateway redirige /api/choix/** vers ici
@CrossOrigin(origins = "*")
public class ChoixController {

    @Autowired
    private ChoixService choixService;

    @PostMapping
    public ResponseEntity<?> creerChoix(@RequestBody ChoixRequest request) {
        try {
            // On suppose une classe interne ou DTO pour recevoir les IDs
            Choix choix = choixService.ajouterChoix(request.getEnseignantId(), request.getUeId());
            return ResponseEntity.ok(choix);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Classe interne simple pour la requête JSON { "enseignantId": 1, "ueId": 5 }
    @lombok.Data
    static class ChoixRequest {
        private Long enseignantId;
        private Long ueId;
    }
}