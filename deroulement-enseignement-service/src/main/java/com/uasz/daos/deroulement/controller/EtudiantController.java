package com.uasz.daos.deroulement.controller;

import com.uasz.daos.deroulement.dto.EtudiantDTO;
import com.uasz.daos.deroulement.model.Etudiant;

import com.uasz.daos.deroulement.service.EtudiantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EtudiantController {

    @Autowired
    private EtudiantService etudiantService;

    /**
     * Affiche la liste de tous les étudiants
     */
    @GetMapping("/lst-etudiants")
    public String listEtudiants(Model model) {
        model.addAttribute("etudiants", etudiantService.getAllEtudiants());
        model.addAttribute("totalEtudiants", etudiantService.compterTousLesEtudiants());
        model.addAttribute("etudiantsActifs", etudiantService.compterEtudiantsActifs());
        return "etudiant-list";
    }

    /**
     * Affiche le formulaire pour ajouter un étudiant
     */
    @GetMapping("/add-etudiant")
    public String addEtudiant(Model model) {
        EtudiantDTO etudiantDTO = new EtudiantDTO();
        model.addAttribute("etudiant", etudiantDTO);
        return "etudiant-add";
    }

    /**
     * Enregistre un nouvel étudiant
     */
    @PostMapping("/save-etudiant")
    public String saveEtudiant(@Valid @ModelAttribute("etudiant") EtudiantDTO etudiantDTO,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "etudiant-add";
        }

        try {
            etudiantService.creerEtudiant(etudiantDTO);
            return "redirect:/lst-etudiants";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "etudiant-add";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'enregistrement : " + e.getMessage());
            return "etudiant-add";
        }
    }

    /**
     * Affiche le formulaire pour modifier un étudiant
     */
    @GetMapping("/edit-etudiant/{id}")
    public String editEtudiant(@PathVariable Long id, Model model) {
        try {
            Etudiant etudiant = etudiantService.getEtudiantById(id);
            model.addAttribute("etudiant", etudiant);
            return "etudiant-edit";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/lst-etudiants";
        }
    }

    /**
     * Met à jour un étudiant existant
     */
    @PostMapping("/update-etudiant/{id}")
    public String updateEtudiant(@PathVariable Long id,
            @Valid @ModelAttribute("etudiant") EtudiantDTO etudiantDTO,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("etudiant", etudiantDTO);
            return "etudiant-edit";
        }

        try {
            etudiantService.modifierEtudiant(id, etudiantDTO);
            return "redirect:/lst-etudiants";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("etudiant", etudiantDTO);
            return "etudiant-edit";
        }
    }

    /**
     * Affiche les détails d'un étudiant
     */
    @GetMapping("/view-etudiant/{id}")
    public String viewEtudiant(@PathVariable Long id, Model model) {
        try {
            Etudiant etudiant = etudiantService.getEtudiantById(id);
            model.addAttribute("etudiant", etudiant);
            return "etudiant-detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/lst-etudiants";
        }
    }

    // ==================== REST API ENDPOINTS ====================

    /**
     * API - Récupère tous les étudiants
     */
    @GetMapping("/api/etudiants")
    @ResponseBody
    public ResponseEntity<List<Etudiant>> getAllEtudiants() {
        return ResponseEntity.ok(etudiantService.getAllEtudiants());
    }

    /**
     * API - Récupère un étudiant par son ID
     */
    @GetMapping("/api/etudiants/{id}")
    @ResponseBody
    public ResponseEntity<?> getEtudiantById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(etudiantService.getEtudiantById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * API - Crée un nouvel étudiant
     */
    @PostMapping("/api/etudiants")
    @ResponseBody
    public ResponseEntity<?> createEtudiant(@Valid @RequestBody EtudiantDTO etudiantDTO) {
        try {
            Etudiant etudiant = etudiantService.creerEtudiant(etudiantDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(etudiant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * API - Met à jour un étudiant
     */
    @PutMapping("/api/etudiants/{id}")
    @ResponseBody
    public ResponseEntity<?> updateEtudiant(@PathVariable Long id,
            @Valid @RequestBody EtudiantDTO etudiantDTO) {
        try {
            Etudiant etudiant = etudiantService.modifierEtudiant(id, etudiantDTO);
            return ResponseEntity.ok(etudiant);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * API - Suspend un étudiant
     */
    @PatchMapping("/api/etudiants/{id}/suspendre")
    @ResponseBody
    public ResponseEntity<String> suspendreEtudiant(@PathVariable Long id) {
        try {
            etudiantService.suspendreEtudiant(id);
            return ResponseEntity.ok("Étudiant suspendu avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * API - Réactive un étudiant
     */
    @PatchMapping("/api/etudiants/{id}/reactiver")
    @ResponseBody
    public ResponseEntity<String> reactiverEtudiant(@PathVariable Long id) {
        try {
            etudiantService.reactiverEtudiant(id);
            return ResponseEntity.ok("Étudiant réactivé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * API - Recherche des étudiants
     */
    @GetMapping("/api/etudiants/search")
    @ResponseBody
    public ResponseEntity<List<Etudiant>> rechercherEtudiants(@RequestParam String term) {
        return ResponseEntity.ok(etudiantService.rechercherEtudiants(term));
    }

    /**
     * API - Récupère les étudiants actifs
     */
    @GetMapping("/api/etudiants/actifs")
    @ResponseBody
    public ResponseEntity<List<Etudiant>> getEtudiantsActifs() {
        return ResponseEntity.ok(etudiantService.getEtudiantsActifs());
    }

    /**
     * API - Supprime un étudiant
     */
    @DeleteMapping("/api/etudiants/{id}")
    @ResponseBody
    public ResponseEntity<String> supprimerEtudiant(@PathVariable Long id) {
        try {
            etudiantService.supprimerEtudiant(id);
            return ResponseEntity.ok("Étudiant supprimé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
