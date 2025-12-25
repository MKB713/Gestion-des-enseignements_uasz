package com.uasz.daos.maquette.controller;

import com.uasz.daos.maquette.dto.ApiResponse;
import com.uasz.daos.maquette.model.Formation;
import com.uasz.daos.maquette.repository.FormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maquette/debug")
@RequiredArgsConstructor
public class DebugController {

    private final FormationRepository formationRepository;

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<String>> check() {
        return ResponseEntity.ok(ApiResponse.success("Serveur en ligne - Diagnostic OK", "OK"));
    }

    @GetMapping("/seed")
    public ResponseEntity<ApiResponse<Formation>> seed() {
        Formation f = new Formation();
        f.setCode("INFO-L");
        f.setLibelle("Licence Informatique");
        f.setDescription("Formation de base");
        Formation saved = formationRepository.save(f);
        return ResponseEntity
                .ok(ApiResponse.success("Données de test créées (Formation ID: " + saved.getId() + ")", saved));
    }
}
