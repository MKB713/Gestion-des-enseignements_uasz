package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.api.EnseignantApi;
import com.uasz.daos.emploitemps.api.MaquetteApi;
import com.uasz.daos.emploitemps.dto.SeanceDTO;
import com.uasz.daos.emploitemps.dto.generation.ECDTO;
import com.uasz.daos.emploitemps.dto.generation.EnseignantDTO;
import com.uasz.daos.emploitemps.model.Salle;
import com.uasz.daos.emploitemps.model.Seance;
import com.uasz.daos.emploitemps.model.TypeSeance;
import com.uasz.daos.emploitemps.service.SalleService;
import com.uasz.daos.emploitemps.service.SeanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/seances")
public class SeanceWebController {

    @Autowired
    private SeanceService seanceService;

    @Autowired
    private EnseignantApi enseignantApi; // Inject EnseignantApi

    @Autowired
    private SalleService salleService; // Inject SalleService

    @Autowired
    private MaquetteApi maquetteApi; // Inject MaquetteApi

    /**
     * Affiche la liste de toutes les séances
     * Mappe vers /seances (GET)
     */
    @GetMapping
    public String listSeances(Model model) {
        List<Seance> seances = seanceService.getAllSeances();
        // TODO: Enrichir les séances avec les noms d'enseignant, salle, EC, classe
        // For now, we'll just pass the IDs. Frontend will need to handle display names.
        model.addAttribute("seances", seances);
        return "seance-list";
    }

    /**
     * Affiche le formulaire d'ajout d'une séance
     * Mappe vers /seances/add (GET)
     */
    @GetMapping("/add")
    public String addSeanceForm(Model model) {
        model.addAttribute("seanceDTO", new SeanceDTO());
        model.addAttribute("typeSeances", TypeSeance.values()); // Pour le dropdown du type de séance

        // Ajouter les listes d'enseignants, salles, ECs pour les dropdowns
        try {
            model.addAttribute("enseignants", enseignantApi.getAllEnseignants());
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des enseignants: " + e.getMessage());
            model.addAttribute("enseignants", List.of());
        }
        try {
            model.addAttribute("salles", salleService.getAllSalles());
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des salles: " + e.getMessage());
            model.addAttribute("salles", List.of());
        }
        try {
            model.addAttribute("ecs", maquetteApi.getAllEcs());
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des ECs: " + e.getMessage());
            model.addAttribute("ecs", List.of());
        }

        return "seance-add";
    }

    /**
     * Traite la soumission du formulaire d'ajout/modification de séance
     * Mappe vers /seances (POST)
     */
    @PostMapping
    public String saveSeance(@ModelAttribute SeanceDTO seanceDTO, RedirectAttributes redirectAttributes) {
        try {
            if (seanceDTO.getId() == null) {
                seanceService.createSeance(seanceDTO, "admin"); // Utilisateur par défaut
                redirectAttributes.addFlashAttribute("message", "Séance ajoutée avec succès !");
            } else {
                seanceService.updateSeance(seanceDTO.getId(), seanceDTO, "admin"); // Utilisateur par défaut
                redirectAttributes.addFlashAttribute("message", "Séance modifiée avec succès !");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement de la séance : " + e.getMessage());
        }
        return "redirect:/seances";
    }

    /**
     * Affiche le formulaire de modification d'une séance
     * Mappe vers /seances/edit/{id} (GET)
     */
    @GetMapping("/edit/{id}")
    public String editSeanceForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Seance seance = seanceService.getSeanceById(id);
            if (seance == null) {
                redirectAttributes.addFlashAttribute("error", "Séance non trouvée.");
                return "redirect:/seances";
            }
            // Convertir Seance en SeanceDTO pour le formulaire
            SeanceDTO seanceDTO = new SeanceDTO();
            seanceDTO.setId(seance.getId());
            seanceDTO.setDateSeance(seance.getDateSeance());
            seanceDTO.setHeureDebut(seance.getHeureDebut());
            seanceDTO.setHeureFin(seance.getHeureFin());
            seanceDTO.setDuree(seance.getDuree());
            seanceDTO.setSalleId(seance.getSalle() != null ? seance.getSalle().getId() : null);
            seanceDTO.setEnseignantId(seance.getEnseignantId());
            seanceDTO.setEcId(seance.getEcId());
            seanceDTO.setClasseId(seance.getClasseId());
            seanceDTO.setTypeSeance(seance.getTypeSeance() != null ? seance.getTypeSeance().name() : null);

            model.addAttribute("seanceDTO", seanceDTO);
            model.addAttribute("typeSeances", TypeSeance.values());

            // Ajouter les listes d'enseignants, salles, ECs pour les dropdowns
            try {
                model.addAttribute("enseignants", enseignantApi.getAllEnseignants());
            } catch (Exception e) {
                model.addAttribute("error", "Erreur lors de la récupération des enseignants: " + e.getMessage());
                model.addAttribute("enseignants", List.of());
            }
            try {
                model.addAttribute("salles", salleService.getAllSalles());
            } catch (Exception e) {
                model.addAttribute("error", "Erreur lors de la récupération des salles: " + e.getMessage());
                model.addAttribute("salles", List.of());
            }
            try {
                model.addAttribute("ecs", maquetteApi.getAllEcs());
            } catch (Exception e) {
                model.addAttribute("error", "Erreur lors de la récupération des ECs: " + e.getMessage());
                model.addAttribute("ecs", List.of());
            }

            return "seance-add"; // Réutiliser le formulaire d'ajout pour la modification
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la récupération de la séance : " + e.getMessage());
            return "redirect:/seances";
        }
    }

    /**
     * Affiche la page de confirmation de suppression (annulation) d'une séance
     * Mappe vers /seances/delete/{id} (GET)
     */
    @GetMapping("/delete/{id}")
    public String deleteSeanceConfirm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Seance seance = seanceService.getSeanceById(id);
            if (seance == null) {
                redirectAttributes.addFlashAttribute("error", "Séance non trouvée.");
                return "redirect:/seances";
            }
            // TODO: Enrichir la séance avec les noms d'enseignant, salle, EC, classe pour l'affichage
            model.addAttribute("seance", seance);
            return "seance-delete-confirm";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la récupération de la séance : " + e.getMessage());
            return "redirect:/seances";
        }
    }

    /**
     * Traite la suppression (annulation) d'une séance
     * Mappe vers /seances/delete/{id} (POST)
     */
    @PostMapping("/delete/{id}")
    public String deleteSeance(@PathVariable Long id,
                               @RequestParam(required = false) String raisonAnnulation,
                               RedirectAttributes redirectAttributes) {
        try {
            seanceService.deleteSeance(id, raisonAnnulation, "admin"); // Utilisateur par défaut
            redirectAttributes.addFlashAttribute("message", "Séance annulée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'annulation de la séance : " + e.getMessage());
        }
        return "redirect:/seances";
    }
}