package com.uasz.daos.deroulement.controller;

import com.uasz.daos.deroulement.dto.NoteCahierTexteDTO;
import com.uasz.daos.deroulement.model.NoteCahierTexte;
import com.uasz.daos.deroulement.service.CahierTextePdfService;
import com.uasz.daos.deroulement.service.NoteCahierTexteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes-cahier")
public class NoteCahierTexteController {

    @Autowired
    private NoteCahierTexteService noteCahierTexteService;

    @Autowired
    private CahierTextePdfService cahierTextePdfService;

    /**
     * API - Exporter le cahier de texte en PDF
     */
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestParam(required = false) Long enseignantId) {
        byte[] pdf = cahierTextePdfService.genererPdfCahierTexte(enseignantId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "cahier-de-texte.pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    /**
     * API - Récupère toutes les notes
     */
    @GetMapping
    public ResponseEntity<List<NoteCahierTexte>> getAllNotes() {
        return ResponseEntity.ok(noteCahierTexteService.getAllNotes());
    }

    /**
     * API - Récupère une note par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable Long id) {
        try {
            NoteCahierTexte note = noteCahierTexteService.getNoteById(id);
            return ResponseEntity.ok(note);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Note non trouvée avec l'ID : " + id);
        }
    }

    /**
     * API - Crée une nouvelle note
     */
    @PostMapping
    public ResponseEntity<?> createNote(@Valid @RequestBody NoteCahierTexteDTO noteDTO) {
        try {
            NoteCahierTexte note = noteCahierTexteService.ajouterNote(noteDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(note);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur de validation : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création de la note : " + e.getMessage());
        }
    }

    /**
     * API - Met à jour une note
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id,
                                       @Valid @RequestBody NoteCahierTexteDTO noteDTO) {
        try {
            NoteCahierTexte note = noteCahierTexteService.modifierNote(id, noteDTO);
            return ResponseEntity.ok(note);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Note non trouvée avec l'ID : " + id);
        }
    }

    /**
     * API - Valide une note
     */
    @PatchMapping("/{id}/valider")
    public ResponseEntity<String> validerNote(@PathVariable Long id) {
        try {
            noteCahierTexteService.validerNote(id);
            return ResponseEntity.ok("La note a été validée avec succès.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Note non trouvée avec l'ID : " + id);
        }
    }

    /**
     * API - Supprime une note
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNote(@PathVariable Long id) {
        try {
            noteCahierTexteService.supprimerNote(id);
            return ResponseEntity.ok("Note supprimée avec succès.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Note non trouvée avec l'ID : " + id);
        }
    }

    /**
     * API - Récupère l'historique des modifications d'une note
     */
    @GetMapping("/{id}/historique")
    public ResponseEntity<?> getHistoriqueNote(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(noteCahierTexteService.getHistoriqueNote(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Note non trouvée avec l'ID : " + id);
        }
    }

    /**
     * API - Consultation avec filtres
     */
    @GetMapping("/search")
    public ResponseEntity<List<NoteCahierTexte>> searchNotes(@RequestParam(required = false) Long enseignantId,
                                                           @RequestParam(required = false) Long seanceId) {
        return ResponseEntity.ok(noteCahierTexteService.consulterCahierTexte(enseignantId, seanceId));
    }
}
