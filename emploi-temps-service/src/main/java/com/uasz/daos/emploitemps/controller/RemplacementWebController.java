package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.api.EnseignantApi;
import com.uasz.daos.emploitemps.dto.RemplacementDTO;
import com.uasz.daos.emploitemps.model.Remplacement;
import com.uasz.daos.emploitemps.model.Seance;
import com.uasz.daos.emploitemps.service.RemplacementService;
import com.uasz.daos.emploitemps.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/remplacements")
public class RemplacementWebController {

    @Autowired
    private RemplacementService remplacementService;

    @Autowired
    private SeanceService seanceService;

    @Autowired
    private EnseignantApi enseignantApi;

    /**
     * Affiche la liste de tous les remplacements
     * Mappe vers /remplacements (GET)
     */
    @GetMapping
    public String listRemplacements(Model model) {
        List<Remplacement> remplacements = remplacementService.getAllRemplacements();
        // TODO: Enrichir les remplacements avec les noms d'enseignant, séance, etc.
        model.addAttribute("remplacements", remplacements);
        return "remplacement-list";
    }

    /**
     * Affiche le formulaire de déclaration d'un remplacement
     * Mappe vers /remplacements/add (GET)
     */
    @GetMapping("/add")
    public String addRemplacementForm(Model model) {
        model.addAttribute("remplacementDTO", new RemplacementDTO());

        try {
            model.addAttribute("seances", seanceService.getAllSeances());
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des séances: " + e.getMessage());
            model.addAttribute("seances", List.of());
        }
        try {
            model.addAttribute("enseignants", enseignantApi.getAllEnseignants());
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des enseignants: " + e.getMessage());
            model.addAttribute("enseignants", List.of());
        }

        return "remplacement-add";
    }

    /**
     * Traite la soumission du formulaire de déclaration de remplacement
     * Mappe vers /remplacements (POST)
     */
    @PostMapping
    public String saveRemplacement(@ModelAttribute RemplacementDTO remplacementDTO, RedirectAttributes redirectAttributes) {
        try {
            remplacementService.creerRemplacement(remplacementDTO.getSeanceId(), remplacementDTO.getEnseignantRemplacantId(), remplacementDTO.getRaison(), remplacementDTO.getTemporaire(), "admin"); // Utilisateur par défaut
            redirectAttributes.addFlashAttribute("message", "Remplacement déclaré avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la déclaration du remplacement : " + e.getMessage());
        }
        return "redirect:/remplacements";
    }

    // TODO: Ajouter les méthodes pour modifier et supprimer un remplacement si nécessaire
    // (User Story mentionne "Gérer les remplacements", ce qui implique CRUD)
}
