package com.uasz.daos.maquette.controller;

import com.uasz.daos.maquette.dto.*;
import com.uasz.daos.maquette.model.MaquetteVersion;
import com.uasz.daos.maquette.service.MaquetteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maquette/maquettes")
@RequiredArgsConstructor
public class MaquetteController {

    private final MaquetteService maquetteService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MaquetteResponseDTO>>> listerMaquettes() {
        List<MaquetteResponseDTO> maquettes = maquetteService.listerTout();
        return ResponseEntity.ok(ApiResponse.success("Liste des maquettes récupérée avec succès", maquettes));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MaquetteResponseDTO>> ajouterMaquette(
            @Valid @RequestBody MaquetteRequestDTO requestDTO) {
        MaquetteResponseDTO created = maquetteService.ajouterMaquette(requestDTO);
        return new ResponseEntity<>(ApiResponse.success("Maquette créée avec succès", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MaquetteResponseDTO>> modifierMaquette(@PathVariable Long id,
            @Valid @RequestBody MaquetteRequestDTO requestDTO) {
        MaquetteResponseDTO updated = maquetteService.modifierMaquette(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Maquette modifiée avec succès", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> supprimerMaquette(@PathVariable Long id) {
        maquetteService.supprimerMaquette(id);
        return ResponseEntity.ok(ApiResponse.success("Maquette supprimée avec succès", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaquetteResponseDTO>> detailsMaquette(@PathVariable Long id) {
        MaquetteResponseDTO maquette = maquetteService.detailsMaquette(id);
        return ResponseEntity.ok(ApiResponse.success("Détails de la maquette récupérés", maquette));
    }

    @PostMapping("/{id}/publier")
    public ResponseEntity<ApiResponse<Void>> publierMaquette(@PathVariable Long id) {
        maquetteService.publierMaquette(id);
        return ResponseEntity.ok(ApiResponse.success("Maquette publiée avec succès", null));
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<ApiResponse<List<MaquetteVersion>>> listerVersions(@PathVariable Long id) {
        List<MaquetteVersion> versions = maquetteService.listerVersions(id);
        return ResponseEntity.ok(ApiResponse.success("Historique des versions récupéré", versions));
    }
}
