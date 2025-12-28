package com.uasz.daos.emploitemps.controller;

import com.uasz.daos.emploitemps.model.Salle;
import com.uasz.daos.emploitemps.service.SalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/salles")
public class SalleWebController {

    @Autowired
    private SalleService salleService;

    /**
     * Affiche la liste de toutes les salles
     * Mappe vers /salles (GET)
     */
    @GetMapping
    public String listSalles(Model model) {
        List<Salle> salles = salleService.getAllSalles();
        model.addAttribute("salles", salles);
        return "salle-list";
    }

    /**
     * Affiche le formulaire d'ajout d'une salle
     * Mappe vers /salles/add (GET)
     */
    @GetMapping("/add")
    public String addSalleForm(Model model) {
        model.addAttribute("salle", new Salle());
        return "salle-add-edit";
    }

    /**
     * Traite la soumission du formulaire d'ajout/modification de salle
     * Mappe vers /salles (POST)
     */
    @PostMapping
    public String saveSalle(@ModelAttribute Salle salle, RedirectAttributes redirectAttributes) {
        try {
            if (salle.getId() == null) {
                salleService.createSalle(salle);
                redirectAttributes.addFlashAttribute("message", "Salle ajoutée avec succès !");
            } else {
                salleService.updateSalle(salle.getId(), salle);
                redirectAttributes.addFlashAttribute("message", "Salle modifiée avec succès !");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'enregistrement de la salle : " + e.getMessage());
        }
        return "redirect:/salles";
    }

    /**
     * Affiche le formulaire de modification d'une salle
     * Mappe vers /salles/edit/{id} (GET)
     */
    @GetMapping("/edit/{id}")
    public String editSalleForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Salle salle = salleService.getSalleById(id);
            model.addAttribute("salle", salle);
            return "salle-add-edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Salle non trouvée : " + e.getMessage());
            return "redirect:/salles";
        }
    }

    /**
     * Supprime une salle
     * Mappe vers /salles/delete/{id} (GET)
     */
    @GetMapping("/delete/{id}")
    public String deleteSalle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            salleService.deleteSalle(id);
            redirectAttributes.addFlashAttribute("message", "Salle supprimée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de la salle : " + e.getMessage());
        }
        return "redirect:/salles";
    }

    // ========== GESTION DES DISPONIBILITÉS ==========

    /**
     * Affiche le formulaire de recherche de salles disponibles
     * Mappe vers /salles/disponibilites/search (GET)
     */
    @GetMapping("/disponibilites/search")
    public String searchSallesDisponiblesForm() {
        return "planning-salle-search";
    }

    /**
     * Traite la recherche de salles disponibles et affiche les résultats
     * Mappe vers /salles/disponibilites (GET)
     */
    @GetMapping("/disponibilites")
    public String getSallesDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heureFin,
            @RequestParam(required = false) Integer capaciteMin,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            List<Salle> sallesDisponibles;

            if (capaciteMin != null && capaciteMin > 0) {
                sallesDisponibles = salleService.getSallesDisponiblesAvecCapacite(
                        date, heureDebut, heureFin, capaciteMin);
            } else {
                sallesDisponibles = salleService.getSallesDisponibles(date, heureDebut, heureFin);
            }

            model.addAttribute("sallesDisponibles", sallesDisponibles);
            model.addAttribute("date", date);
            model.addAttribute("heureDebut", heureDebut);
            model.addAttribute("heureFin", heureFin);
            model.addAttribute("capaciteMin", capaciteMin);

            return "salles-disponibles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la recherche des salles disponibles : " + e.getMessage());
            return "redirect:/salles/disponibilites/search";
        }
    }
}
