package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.api.EnseignantApi;
import com.uasz.daos.emploitemps.dto.EmploiDuTempsDTO;
import com.uasz.daos.emploitemps.model.Seance;
import com.uasz.daos.emploitemps.service.EmploiDuTempsGenerationService;
import com.uasz.daos.emploitemps.dto.generation.GenerationResponse;
import com.uasz.daos.emploitemps.service.EmploiDuTempsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/planning")
public class PlanningWebController {

    @Autowired
    private EmploiDuTempsService emploiDuTempsService;

    @Autowired
    private EnseignantApi enseignantApi;

    @Autowired
    private EmploiDuTempsGenerationService emploiDuTempsGenerationService;

    /**
     * Affiche l'emploi du temps hebdomadaire
     * Mappe vers /planning/hebdomadaire (GET)
     */
    @GetMapping("/hebdomadaire")
    public String showWeeklyPlanning(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String filtrePar,
            @RequestParam(required = false) Long filtreId,
            Model model,
            RedirectAttributes redirectAttributes) {

        LocalDate dateReference = (date != null) ? date : LocalDate.now();

        try {
            EmploiDuTempsDTO planning = emploiDuTempsService.getPlanningHebdomadaire(
                    dateReference, filtrePar, filtreId);
            model.addAttribute("planning", planning);
            model.addAttribute("currentDate", dateReference);
            return "planning-view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la récupération du planning hebdomadaire : " + e.getMessage());
            return "redirect:/seances";
        }
    }

    /**
     * Affiche l'emploi du temps semestriel
     * Mappe vers /planning/semestriel (GET)
     */
    @GetMapping("/semestriel")
    public String showSemestrialPlanning(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String filtrePar,
            @RequestParam(required = false) Long filtreId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            EmploiDuTempsDTO planning = emploiDuTempsService.getPlanningSemestriel(
                    dateDebut, dateFin, filtrePar, filtreId);
            model.addAttribute("planning", planning);
            model.addAttribute("currentDateDebut", dateDebut);
            model.addAttribute("currentDateFin", dateFin);
            return "planning-view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la récupération du planning semestriel : " + e.getMessage());
            return "redirect:/seances";
        }
    }

    /**
     * Affiche le formulaire de recherche multicritère de séances
     * Mappe vers /planning/search (GET)
     */
    @GetMapping("/search")
    public String searchSeancesForm(Model model) {
        // TODO: Ajouter les listes d'enseignants, salles, ECs, classes pour les dropdowns de recherche
        return "seance-search";
    }

    /**
     * Traite la recherche multicritère de séances et affiche les résultats
     * Mappe vers /planning/search/results (GET)
     */
    @GetMapping("/search/results")
    public String searchSeances(
            @RequestParam(required = false) Long enseignantId,
            @RequestParam(required = false) Long salleId,
            @RequestParam(required = false) Long ecId,
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            List<Seance> seances = emploiDuTempsService.rechercherSeances(
                    enseignantId, salleId, ecId, classeId, dateDebut, dateFin);
            model.addAttribute("seances", seances);
            // TODO: Enrichir les séances avec les noms d'enseignant, salle, EC, classe pour l'affichage
            return "seance-search-results";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la recherche des séances : " + e.getMessage());
            return "redirect:/planning/search";
        }
    }

    /**
     * Affiche le formulaire de recherche de séances par enseignant et les résultats
     * Mappe vers /seances/enseignant/search (GET)
     */
    @GetMapping("/seances/enseignant/search")
    public String searchSeancesByEnseignant(
            @RequestParam(required = false) Long enseignantId,
            @RequestParam(required = false) String enseignantNom,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Long resolvedEnseignantId = enseignantId;
            if (enseignantNom != null && !enseignantNom.isEmpty()) {
                if (resolvedEnseignantId == null) {
                    // Placeholder for resolving enseignantNom to ID
                }
            }

            List<Seance> seances = List.of();
            if (resolvedEnseignantId != null) {
                seances = emploiDuTempsService.rechercherSeances(
                        resolvedEnseignantId, null, null, null, null, null);
            }
            model.addAttribute("seances", seances);
            model.addAttribute("enseignantId", enseignantId);
            model.addAttribute("enseignantNom", enseignantNom);
            return "seance-enseignant-search";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la recherche des séances par enseignant : " + e.getMessage());
            return "redirect:/seances/enseignant/search";
        }
    }

    /**
     * Affiche le formulaire de génération d'emploi du temps optimisé
     * Mappe vers /planning/generer (GET)
     */
    @GetMapping("/generer")
    public String showGenerationForm(Model model) {
        return "planning-generation";
    }

    /**
     * Déclenche la génération d'un emploi du temps optimisé
     * Mappe vers /planning/generer (POST)
     */
    @PostMapping("/generer")
    public String triggerGeneration(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) List<Long> classeIds, // Nouveau paramètre
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            // Utiliser la nouvelle méthode et lui passer les IDs de classes et un utilisateur par défaut
            GenerationResponse response = emploiDuTempsGenerationService.genererEmploiDuTempsAutomatique(
                dateDebut, dateFin, classeIds != null ? classeIds : List.of(), "admin");

            if (response.getSuccess()) {
                redirectAttributes.addFlashAttribute("message", "Génération de l'emploi du temps optimisé lancée avec succès !");
            } else {
                redirectAttributes.addFlashAttribute("error", "Génération de l'emploi du temps avec conflits : " + response.getConflits().size());
            }
            redirectAttributes.addFlashAttribute("generationResponse", response); // Passer la réponse pour affichage si besoin
            return "redirect:/planning/generer";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la génération de l'emploi du temps : " + e.getMessage());
            return "redirect:/planning/generer";
        }
    }

    /**
     * Affiche le formulaire de téléchargement PDF
     * Mappe vers /planning/pdf/download (GET)
     */
    @GetMapping("/pdf/download")
    public String showPdfDownloadForm() {
        return "planning-pdf-download";
    }

    /**
     * Génère et télécharge l'emploi du temps en PDF
     * Mappe vers /planning/pdf/download (POST)
     */
    @PostMapping("/pdf/download")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
            @RequestParam(required = false) String filtrePar,
            @RequestParam(required = false) Long filtreId,
            RedirectAttributes redirectAttributes) {
        try {
            // La méthode generatePdf a été supprimée ou déplacée.
            // Pour l'instant, nous retournons une erreur.
            // TODO: Implémenter la nouvelle logique de génération de PDF ou la supprimer si non nécessaire.
            String errorMessage = "La génération de PDF n'est pas encore implémentée ou a été refactorisée.";
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_IMPLEMENTED)
                                 .body(errorMessage.getBytes());
        } catch (Exception e) {
            // Gérer les erreurs inattendues
            String errorMessage = "Erreur inattendue lors de la génération du PDF : " + e.getMessage();
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(errorMessage.getBytes());
        }
    }
}
