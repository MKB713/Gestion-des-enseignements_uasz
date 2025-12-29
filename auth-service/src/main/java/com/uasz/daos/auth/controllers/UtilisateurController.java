package com.uasz.daos.auth.controllers;

import com.uasz.daos.auth.dto.UserDTO;
import com.uasz.daos.auth.enums.Etat;
import com.uasz.daos.auth.enums.Role;
import com.uasz.daos.auth.model.Utilisateur;
import com.uasz.daos.auth.services.CustomUserDetails;
import com.uasz.daos.auth.services.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/management")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    // Liste des utilisateurs
    @GetMapping("/users")
    public ResponseEntity<?> listeUtilisateurs() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();

        List<Utilisateur> utilisateurs = utilisateurService.findAll(currentUser.getUtilisateur());

        List<UserDTO> userDTOs = utilisateurs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(Map.of(
                "users", userDTOs,
                "count", userDTOs.size()
        ));
    }

    // Page des paramètres utilisateur
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Non authentifié"
            ));
        }

        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return ResponseEntity.ok(convertToDTO(utilisateur));
    }

    // Ajout d'un utilisateur
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setMatricule(request.getMatricule());
            utilisateur.setNom(request.getNom());
            utilisateur.setPrenom(request.getPrenom());
            utilisateur.setEmail(request.getEmail());
            utilisateur.setDateNaissance(request.getDateNaissance());
            utilisateur.setTelephone(request.getTelephone());
            utilisateur.setAdresse(request.getAdresse());
            utilisateur.setRole(request.getRole());
            utilisateur.setEtat(Etat.ACTIF);

            Utilisateur savedUser = utilisateurService.createUser(utilisateur);

            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedUser));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // Modification d'un utilisateur
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @Valid @RequestBody UpdateUserRequest request) {

        try {
            Utilisateur utilisateur = utilisateurService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            utilisateur.setNom(request.getNom());
            utilisateur.setPrenom(request.getPrenom());
            utilisateur.setEmail(request.getEmail());
            utilisateur.setDateNaissance(request.getDateNaissance());
            utilisateur.setTelephone(request.getTelephone());
            utilisateur.setAdresse(request.getAdresse());

            // Seul admin peut changer rôle et état
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();

            if (currentUser.getRole().isAdmin()) {
                utilisateur.setRole(request.getRole());
                utilisateur.setEtat(request.getEtat());
            }

            Utilisateur updatedUser = utilisateurService.updateUser(utilisateur);

            return ResponseEntity.ok(convertToDTO(updatedUser));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // Gestion de l'état d'un utilisateur
    @PostMapping("/users/{id}/state")
    public ResponseEntity<?> manageUserState(@PathVariable Long id,
                                             @RequestParam String action) {
        try {
            switch (action.toLowerCase()) {
                case "archive":
                    utilisateurService.archiverUser(id);
                    return ResponseEntity.ok(Map.of("message", "Utilisateur archivé"));
                case "unarchive":
                    utilisateurService.desarchiverUser(id);
                    return ResponseEntity.ok(Map.of("message", "Utilisateur désarchivé"));
                case "activate":
                    utilisateurService.activerUser(id);
                    return ResponseEntity.ok(Map.of("message", "Utilisateur activé"));
                case "deactivate":
                    utilisateurService.desactiverUser(id);
                    return ResponseEntity.ok(Map.of("message", "Utilisateur désactivé"));
                default:
                    return ResponseEntity.badRequest().body(Map.of(
                            "error", "Action non supportée. Utilisez: archive, unarchive, activate, deactivate"
                    ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // Liste des utilisateurs archivés
    @GetMapping("/users/archived")
    public ResponseEntity<?> listeArchives() {
        List<Utilisateur> archived = utilisateurService.getAllUsersArchives();

        List<UserDTO> userDTOs = archived.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(Map.of(
                "users", userDTOs,
                "count", userDTOs.size()
        ));
    }

    // Changement de mot de passe
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request,
                                            Principal principal) {

        String email = principal.getName();

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Le nouveau mot de passe et la confirmation ne correspondent pas."
            ));
        }

        boolean success = utilisateurService.updatePassword(
                email, request.getCurrentPassword(),
                request.getNewPassword(), request.getConfirmPassword()
        );

        if (success) {
            return ResponseEntity.ok().body(Map.of(
                    "message", "Mot de passe changé avec succès !"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Erreur : Vérifiez votre mot de passe actuel ou la force du nouveau mot de passe."
            ));
        }
    }

    // Recherche d'utilisateurs
    @GetMapping("/users/search")
    public ResponseEntity<?> searchUtilisateurs(@RequestParam("q") String searchTerm) {
        List<Utilisateur> utilisateurs = utilisateurService.searchUtilisateurs(searchTerm);

        List<UserDTO> userDTOs = utilisateurs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(Map.of(
                "users", userDTOs,
                "searchTerm", searchTerm,
                "count", userDTOs.size()
        ));
    }

    // Helper methods
    private UserDTO convertToDTO(Utilisateur utilisateur) {
        return UserDTO.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .dateNaissance(utilisateur.getDateNaissance())
                .matricule(utilisateur.getMatricule())
                .etat(utilisateur.getEtat())
                .dateCreation(utilisateur.getDateCreation())
                .build();
    }

    // Request DTOs
    public static class CreateUserRequest {
        private String matricule;
        private String nom;
        private String prenom;
        private String email;
        private java.time.LocalDate dateNaissance;
        private String telephone;
        private String adresse;
        private Role role;

        public String getMatricule() {
            return matricule;
        }

        public void setMatricule(String matricule) {
            this.matricule = matricule;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public java.time.LocalDate getDateNaissance() {
            return dateNaissance;
        }

        public void setDateNaissance(java.time.LocalDate dateNaissance) {
            this.dateNaissance = dateNaissance;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getAdresse() {
            return adresse;
        }

        public void setAdresse(String adresse) {
            this.adresse = adresse;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }
    }

    public static class UpdateUserRequest {
        private String nom;
        private String prenom;
        private String email;
        private java.time.LocalDate dateNaissance;
        private String telephone;
        private String adresse;
        private Role role;
        private Etat etat;

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public java.time.LocalDate getDateNaissance() {
            return dateNaissance;
        }

        public void setDateNaissance(java.time.LocalDate dateNaissance) {
            this.dateNaissance = dateNaissance;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getAdresse() {
            return adresse;
        }

        public void setAdresse(String adresse) {
            this.adresse = adresse;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public Etat getEtat() {
            return etat;
        }

        public void setEtat(Etat etat) {
            this.etat = etat;
        }
    }

    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
        private String confirmPassword;

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}