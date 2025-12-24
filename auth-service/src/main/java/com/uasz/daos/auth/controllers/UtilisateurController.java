package com.uasz.daos.auth.controllers;

import com.uasz.daos.auth.model.Utilisateur;
import com.uasz.daos.auth.services.CustomUserDetails;
import com.uasz.daos.auth.services.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    private CustomUserDetails getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.getPrincipal() instanceof CustomUserDetails)
                ? (CustomUserDetails) auth.getPrincipal()
                : null;
    }

    // Liste des utilisateurs
    @GetMapping("/lst-utilisateurs")
    public String listeUtilisateurs(Model model) {
        CustomUserDetails cu = getLoggedUser();

        if (cu != null && cu.getEntity() instanceof Utilisateur utilisateur) {
            model.addAttribute("utilisateurs", utilisateurService.findAll(utilisateur));
        } else {
            model.addAttribute("utilisateurs", List.of());
        }

        return "utilisateur-list";
    }

    // Page des paramètres utilisateur
    @GetMapping("/parametres")
    public String settingsPage(Model model, Principal principal) {
        if (principal != null) {
            Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            model.addAttribute("utilisateur", utilisateur);
        }
        return "parametres";
    }

    // Formulaire d'ajout d'utilisateur
    @GetMapping("/add-utilisateur")
    public String showAddUserForm(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "utilisateur-add";
    }

    // Ajout d'un utilisateur
    @PostMapping("/ajout-user")
    public String createUser(@Valid @ModelAttribute Utilisateur utilisateur,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("utilisateur", utilisateur);
            return "utilisateur-add";
        }

        try {
            utilisateurService.createUser(utilisateur);
            redirectAttributes.addFlashAttribute("success", "Utilisateur ajouté avec succès");
            return "redirect:/lst-utilisateurs";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de l'ajout : " + e.getMessage());
            model.addAttribute("utilisateur", utilisateur);
            return "utilisateur-add";
        }
    }

    // Formulaire de modification d'utilisateur
    @GetMapping("/edit-utilisateur/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        Utilisateur utilisateur = utilisateurService.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        model.addAttribute("utilisateur", utilisateur);
        return "utilisateur-edit";
    }

    // Modification d'un utilisateur
    @PostMapping("/edit/utilisateur/{id}")
    public String editUser(@PathVariable Long id,
                           @Valid @ModelAttribute Utilisateur utilisateur,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("utilisateur", utilisateur);
            return "utilisateur-edit";
        }

        try {
            utilisateur.setId(id);
            utilisateurService.updateUser(utilisateur);
            redirectAttributes.addFlashAttribute("success", "Utilisateur modifié avec succès");
            return "redirect:/lst-utilisateurs";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de la modification : " + e.getMessage());
            model.addAttribute("utilisateur", utilisateur);
            return "utilisateur-edit";
        }
    }

    // Modification du profil (pour l'utilisateur courant)
    @PostMapping("/edit1/utilisateur/{id}")
    public String editUserProfile(@PathVariable Long id,
                                  @Valid @ModelAttribute Utilisateur utilisateur,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("utilisateur", utilisateur);
            return "parametres";
        }

        try {
            utilisateur.setId(id);
            utilisateurService.updateUser(utilisateur);
            redirectAttributes.addFlashAttribute("success", "Profil modifié avec succès");
            return "redirect:/parametres";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors de la modification : " + e.getMessage());
            model.addAttribute("utilisateur", utilisateur);
            return "parametres";
        }
    }

    // Archivage d'un utilisateur
    @PostMapping("/archiver/{id}")
    public String archiverUtilisateur(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.archiverUser(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur archivé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'archivage : " + e.getMessage());
        }
        return "redirect:/lst-utilisateurs";
    }

    // Désarchivage d'un utilisateur
    @PostMapping("/desarchiver/{id}")
    public String desarchiverUtilisateur(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.desarchiverUser(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur désarchivé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors du désarchivage : " + e.getMessage());
        }
        return "redirect:/lst-utilisateurs-archives";
    }

    // Activation d'un utilisateur
    @PostMapping("/activer/{id}")
    public String activerUtilisateur(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.activerUser(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur activé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'activation : " + e.getMessage());
        }
        return "redirect:/lst-utilisateurs";
    }

    // Désactivation d'un utilisateur
    @PostMapping("/desactiver/{id}")
    public String desactiverUtilisateur(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.desactiverUser(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur désactivé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la désactivation : " + e.getMessage());
        }
        return "redirect:/lst-utilisateurs";
    }

    // Liste des utilisateurs archivés
    @GetMapping("/lst-utilisateurs-archives")
    public String listeArchives(Model model) {
        model.addAttribute("utilisateurs", utilisateurService.getAllUsersArchives());
        return "utilisateur-archive-list";
    }

    // Changement de mot de passe
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Principal principal,
            Model model) {

        String email = principal.getName();
        Utilisateur utilisateur = utilisateurService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        model.addAttribute("utilisateur", utilisateur);

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Erreur : Le nouveau mot de passe et la confirmation ne correspondent pas.");
            return "parametres";
        }

        boolean success = utilisateurService.updatePassword(email, currentPassword, newPassword, confirmPassword);

        if (success) {
            model.addAttribute("message", "Mot de passe changé avec succès !");
        } else {
            model.addAttribute("error", "Erreur : Vérifiez votre mot de passe actuel ou la force du nouveau mot de passe.");
        }

        return "parametres";
    }

    // Recherche d'utilisateurs
    @GetMapping("/search-utilisateurs")
    public String searchUtilisateurs(@RequestParam("search") String searchTerm, Model model) {
        List<Utilisateur> utilisateurs = utilisateurService.searchUtilisateurs(searchTerm);
        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("searchTerm", searchTerm);
        return "utilisateur-list";
    }
}