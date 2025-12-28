package com.uasz.daos.choix.controllers;

import com.uasz.daos.choix.dtos.*;
import com.uasz.daos.choix.services.ChoixService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/choix")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ChoixController {

    private final ChoixService choixService;

    @PostMapping
    public ResponseEntity<MessageResponseDTO> ajouterChoix(
            @Valid @RequestBody ChoixCreateDTO dto) {

        log.info("Requête POST /api/choix - Ajout d'un nouveau choix");

        ChoixResponseDTO choix = choixService.ajouterChoix(dto);

        MessageResponseDTO response = MessageResponseDTO.success(
                "Choix d'enseignement créé avec succès",
                choix
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> modifierChoix(
            @PathVariable Long id,
            @Valid @RequestBody ChoixUpdateDTO dto,
            @RequestHeader(value = "X-Enseignant-Id", required = false) Long idEnseignant) {

        log.info("Requête PUT /api/choix/{} - Modification du choix", id);

        if (idEnseignant == null) {
            throw new IllegalArgumentException("ID enseignant requis dans le header X-Enseignant-Id");
        }

        ChoixResponseDTO choix = choixService.modifierChoix(id, dto, idEnseignant);

        MessageResponseDTO response = MessageResponseDTO.success(
                "Choix d'enseignement modifié avec succès",
                choix
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerChoix(
            @PathVariable Long id,
            @RequestHeader(value = "X-Enseignant-Id", required = false) Long idEnseignant) {

        log.info("Requête DELETE /api/choix/{} - Suppression du choix", id);

        if (idEnseignant == null) {
            throw new IllegalArgumentException("ID enseignant requis dans le header X-Enseignant-Id");
        }

        choixService.supprimerChoix(id, idEnseignant);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/enseignant/{idEnseignant}")
    public ResponseEntity<List<ChoixResponseDTO>> rechercherChoixParEnseignant(
            @PathVariable Long idEnseignant) {

        log.info("Requête GET /api/choix/enseignant/{} - Recherche des choix", idEnseignant);

        List<ChoixResponseDTO> choixList = choixService.rechercherChoixParEnseignant(idEnseignant);

        return ResponseEntity.ok(choixList);
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<ChoixResponseDTO>> listerTousLesChoix(
            @RequestParam(required = false) Long idEnseignant,
            @RequestParam(required = false) Long idEnseignement,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        log.info("Requête GET /api/choix - Listing de tous les choix");

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PageResponseDTO<ChoixResponseDTO> response = choixService.listerTousLesChoix(
                idEnseignant, idEnseignement, dateDebut, dateFin, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChoixResponseDTO> getChoixById(@PathVariable Long id) {

        log.info("Requête GET /api/choix/{} - Récupération du choix", id);

        ChoixResponseDTO choix = choixService.getChoixById(id);

        return ResponseEntity.ok(choix);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Choix Service is running on port 8084");
    }
}