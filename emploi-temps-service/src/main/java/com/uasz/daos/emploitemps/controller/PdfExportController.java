package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.service.PdfExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

/**
 * Controller pour l'export PDF des emplois du temps
 * User Story: "Éditer et imprimer l'emploi du temps en PDF"
 */
@RestController
@RequestMapping("/api/emploi-du-temps/export/pdf")
public class PdfExportController {

    @Autowired
    private PdfExportService pdfExportService;

    /**
     * GET /api/emploi-du-temps/export/pdf/hebdomadaire
     * Génère un PDF hebdomadaire
     *
     * Params:
     * - dateDebut: Date de début de la semaine (format: yyyy-MM-dd)
     * - typeFiltre: Type de filtre (classe, enseignant, salle) - optionnel
     * - filtreId: ID de l'entité filtrée - optionnel
     *
     * Exemple: GET /api/emploi-du-temps/export/pdf/hebdomadaire?dateDebut=2024-03-15&typeFiltre=classe&filtreId=12
     */
    @GetMapping("/hebdomadaire")
    public CompletableFuture<ResponseEntity<byte[]>> exporterHebdomadaire(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) String typeFiltre,
            @RequestParam(required = false) Long filtreId) {

        return pdfExportService.genererPdfHebdomadaire(dateDebut, typeFiltre, filtreId)
                .thenApply(pdfBytes -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment",
                            "emploi_temps_hebdo_" + dateDebut + ".pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
                })
                .exceptionally(ex -> {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    /**
     * GET /api/emploi-du-temps/export/pdf/semestriel
     * Génère un PDF semestriel
     *
     * Params:
     * - dateDebut: Date de début du semestre (format: yyyy-MM-dd)
     * - dateFin: Date de fin du semestre (format: yyyy-MM-dd)
     * - typeFiltre: Type de filtre (classe, enseignant, salle) - optionnel
     * - filtreId: ID de l'entité filtrée - optionnel
     *
     * Exemple: GET /api/emploi-du-temps/export/pdf/semestriel?dateDebut=2024-01-01&dateFin=2024-06-30&typeFiltre=classe&filtreId=12
     */
    @GetMapping("/semestriel")
    public CompletableFuture<ResponseEntity<byte[]>> exporterSemestriel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String typeFiltre,
            @RequestParam(required = false) Long filtreId) {

        return pdfExportService.genererPdfSemestriel(dateDebut, dateFin, typeFiltre, filtreId)
                .thenApply(pdfBytes -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment",
                            "emploi_temps_semestre_" + dateDebut + "_" + dateFin + ".pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
                })
                .exceptionally(ex -> {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    /**
     * GET /api/emploi-du-temps/export/pdf/salle/{id}
     * Génère un PDF pour le planning d'une salle
     *
     * Params:
     * - dateDebut: Date de début (format: yyyy-MM-dd)
     * - dateFin: Date de fin (format: yyyy-MM-dd)
     *
     * Exemple: GET /api/emploi-du-temps/export/pdf/salle/5?dateDebut=2024-03-01&dateFin=2024-03-31
     */
    @GetMapping("/salle/{id}")
    public CompletableFuture<ResponseEntity<byte[]>> exporterSalle(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {

        return pdfExportService.genererPdfSalle(id, dateDebut, dateFin)
                .thenApply(pdfBytes -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData("attachment",
                            "planning_salle_" + id + "_" + dateDebut + ".pdf");
                    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                    return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
                })
                .exceptionally(ex -> {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    /**
     * GET /api/emploi-du-temps/export/pdf/classe/{id}
     * Génère un PDF pour l'emploi du temps d'une classe (raccourci)
     *
     * Params:
     * - dateDebut: Date de début (format: yyyy-MM-dd)
     * - periode: "semaine" ou "semestre"
     *
     * Exemple: GET /api/emploi-du-temps/export/pdf/classe/12?dateDebut=2024-03-15&periode=semaine
     */
    @GetMapping("/classe/{id}")
    public CompletableFuture<ResponseEntity<byte[]>> exporterClasse(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(defaultValue = "semaine") String periode) {

        if ("semaine".equalsIgnoreCase(periode)) {
            return exporterHebdomadaire(dateDebut, "classe", id);
        } else {
            LocalDate dateFin = dateDebut.plusMonths(4); // Semestre = ~4 mois
            return exporterSemestriel(dateDebut, dateFin, "classe", id);
        }
    }

    /**
     * GET /api/emploi-du-temps/export/pdf/enseignant/{id}
     * Génère un PDF pour l'emploi du temps d'un enseignant (raccourci)
     *
     * Params:
     * - dateDebut: Date de début (format: yyyy-MM-dd)
     * - periode: "semaine" ou "semestre"
     *
     * Exemple: GET /api/emploi-du-temps/export/pdf/enseignant/45?dateDebut=2024-03-15&periode=semaine
     */
    @GetMapping("/enseignant/{id}")
    public CompletableFuture<ResponseEntity<byte[]>> exporterEnseignant(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(defaultValue = "semaine") String periode) {

        if ("semaine".equalsIgnoreCase(periode)) {
            return exporterHebdomadaire(dateDebut, "enseignant", id);
        } else {
            LocalDate dateFin = dateDebut.plusMonths(4);
            return exporterSemestriel(dateDebut, dateFin, "enseignant", id);
        }
    }
}