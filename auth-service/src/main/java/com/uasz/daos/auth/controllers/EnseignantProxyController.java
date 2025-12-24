package com.uasz.daos.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur proxy pour les pages de gestion des enseignants
 * Redirige les requêtes vers le _enseignantservice
 */
@Controller
public class EnseignantProxyController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String ENSEIGNANT_SERVICE_URL = "http://enseignant-service";

    /**
     * Liste des enseignants
     */
    @GetMapping("/lst-enseignants")
    public String listEnseignants(Model model) {
        try {
            List<Map<String, Object>> enseignants = restTemplate.getForObject(
                    ENSEIGNANT_SERVICE_URL + "/api/enseignants",
                    List.class
            );
            model.addAttribute("enseignants", enseignants);
        } catch (Exception e) {
            model.addAttribute("error", "Impossible de charger la liste des enseignants");
            model.addAttribute("enseignants", List.of());
        }
        return "enseignant-list";
    }

    /**
     * Liste des enseignants archivés
     */
    @GetMapping("/lst-enseignants-archives")
    public String listEnseignantsArchives(Model model) {
        try {
            List<Map<String, Object>> enseignants = restTemplate.getForObject(
                    ENSEIGNANT_SERVICE_URL + "/api/enseignants/archives",
                    List.class
            );
            model.addAttribute("enseignants", enseignants);
            model.addAttribute("isArchiveView", Optional.of(true));
        } catch (Exception e) {
            model.addAttribute("error", "Impossible de charger les archives");
            model.addAttribute("enseignants", List.of());
        }
        return "enseignant-archive-list";
    }

    /**
     * Voir les détails d'un enseignant
     */
    @GetMapping("/view-enseignant/{id}")
    public String viewEnseignantDetails(@PathVariable Long id, Model model) {
        try {
            Map<String, Object> enseignant = restTemplate.getForObject(
                    ENSEIGNANT_SERVICE_URL + "/api/enseignants/" + id,
                    Map.class
            );
            model.addAttribute("enseignant", enseignant);
        } catch (Exception e) {
            model.addAttribute("enseignant", null);
            model.addAttribute("error", "Impossible de charger les détails de l'enseignant");
        }
        return "enseignant-details";
    }

    /**
     * Formulaire pour ajouter un enseignant
     */
    @GetMapping("/add-enseignant")
    public String addEnseignant(Model model) {
        model.addAttribute("enseignant", Map.of());
        model.addAttribute("grades", List.of("Assistant", "Maître-Assistant", "Maître de Conférences", "Professeur Titulaire", "Professeur Assimilé"));
        model.addAttribute("statuts", List.of("ACTIF", "INACTIF", "ARCHIVE"));
        return "enseignant-add";
    }

    /**
     * Sauvegarder un nouvel enseignant
     */
    @PostMapping("/save-enseignant")
    public String saveEnseignant(@ModelAttribute Map<String, Object> enseignant,
                                 RedirectAttributes redirectAttributes) {
        try {
            Map<String, Object> response = restTemplate.postForObject(
                    ENSEIGNANT_SERVICE_URL + "/api/enseignants",
                    enseignant,
                    Map.class
            );
            redirectAttributes.addFlashAttribute("success", "Enseignant ajouté avec succès!");
            return "redirect:/lst-enseignants";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout: " + e.getMessage());
            return "redirect:/add-enseignant";
        }
    }

    /**
     * Formulaire pour éditer un enseignant
     */
    @GetMapping("/edit-enseignant/{id}")
    public String editEnseignant(@PathVariable Long id, Model model) {
        try {
            Map<String, Object> enseignant = restTemplate.getForObject(
                    ENSEIGNANT_SERVICE_URL + "/api/enseignants/" + id,
                    Map.class
            );
            model.addAttribute("enseignant", enseignant);
            model.addAttribute("grades", List.of("Assistant", "Maître-Assistant", "Maître de Conférences", "Professeur Titulaire", "Professeur Assimilé"));
            model.addAttribute("statuts", List.of("ACTIF", "INACTIF", "ARCHIVE"));
        } catch (Exception e) {
            model.addAttribute("error", "Impossible de charger l'enseignant");
        }
        return "enseignant-edit";
    }

    /**
     * Mettre à jour un enseignant
     */
    @PostMapping("/update-enseignant/{id}")
    public String updateEnseignant(@PathVariable Long id,
                                   @ModelAttribute Map<String, Object> enseignant,
                                   RedirectAttributes redirectAttributes) {
        try {
            restTemplate.put(
                    ENSEIGNANT_SERVICE_URL + "/api/enseignants/" + id,
                    enseignant
            );
            redirectAttributes.addFlashAttribute("success", "Enseignant mis à jour avec succès.");
            return "redirect:/lst-enseignants";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification: " + e.getMessage());
            return "redirect:/edit-enseignant/" + id;
        }
    }

    /**
     * Archiver un enseignant
     */
    @PostMapping("/archive-enseignant/{id}")
    public String archiverEnseignant(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            restTemplate.postForObject(
                    ENSEIGNANT_SERVICE_URL + "/api/enseignants/" + id + "/archive",
                    null,
                    Map.class
            );
            redirectAttributes.addFlashAttribute("success", "Enseignant archivé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'archivage: " + e.getMessage());
        }
        return "redirect:/lst-enseignants";
    }

    /**
     * Désarchiver un enseignant
     */
    @PostMapping("/unarchive-enseignant/{id}")
    public String desarchiverEnseignant(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            restTemplate.postForObject(
                    ENSEIGNANT_SERVICE_URL + "/api/enseignants/" + id + "/unarchive",
                    null,
                    Map.class
            );
            redirectAttributes.addFlashAttribute("success", "Enseignant désarchivé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du désarchivage: " + e.getMessage());
        }
        return "redirect:/lst-enseignants-archives";
    }

    /**
     * Activer un enseignant
     */
    @PostMapping("/activer-enseignant/{id}")
    public String activerEnseignant(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            restTemplate.postForObject(
                    ENSEIGNANT_SERVICE_URL + "/api/enseignants/" + id + "/activate",
                    null,
                    Map.class
            );
            redirectAttributes.addFlashAttribute("success", "Enseignant activé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'activation: " + e.getMessage());
        }
        return "redirect:/lst-enseignants";
    }

    /**
     * Désactiver un enseignant
     */
    @PostMapping("/desactiver-enseignant/{id}")
    public String desactiverEnseignant(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            restTemplate.postForObject(
                    ENSEIGNANT_SERVICE_URL + "/api/enseignants/" + id + "/deactivate",
                    null,
                    Map.class
            );
            redirectAttributes.addFlashAttribute("success", "Enseignant désactivé avec succès.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la désactivation: " + e.getMessage());
        }
        return "redirect:/lst-enseignants";
    }
}