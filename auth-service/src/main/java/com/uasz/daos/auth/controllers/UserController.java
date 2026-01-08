package com.uasz.daos.auth.controllers;

import com.uasz.daos.auth.dto.UserDTO;
import com.uasz.daos.auth.model.Utilisateur;
import com.uasz.daos.auth.services.CustomUserDetails;
import com.uasz.daos.auth.services.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:5173" })
public class UserController {

        private final UtilisateurService utilisateurService;

        public UserController(UtilisateurService utilisateurService) {
                this.utilisateurService = utilisateurService;
        }

        @GetMapping("/me")
        public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
                Utilisateur utilisateur = utilisateurService.findById(userDetails.getId())
                                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

                UserDTO userDTO = UserDTO.builder()
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

                return ResponseEntity.ok(userDTO);
        }

        @PostMapping
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
        public ResponseEntity<Utilisateur> createUser(@RequestBody com.uasz.daos.auth.dto.RegisterRequest request) {
                // 1. Générer le Matricule Automatique (Année + Séquence)
                String year = String.valueOf(java.time.Year.now().getValue());
                long count = utilisateurService.count() + 1;
                String matricule = year + String.format("%04d", count);

                // Vérifier unicité (simplifié)
                // Note: Idéalement déplacer cette logique dans un service, mais pour l'instant
                // duplication du AuthService par nécessité de rapidité
                // (En production, refactorisez dans un UserGeneratorService)

                // 2. Générer l'Email Institutionnel
                char firstP = request.getPrenom() != null && !request.getPrenom().isEmpty()
                                ? request.getPrenom().toLowerCase().charAt(0)
                                : 'x';
                char firstN = request.getNom() != null && !request.getNom().isEmpty()
                                ? request.getNom().toLowerCase().charAt(0)
                                : 'x';
                String randomDigits = String.format("%03d", (int) (Math.random() * 1000));
                String generatedEmail = firstP + "." + firstN + randomDigits + "@zig.univ.sn";

                // 3. Créer l'objet Utilisateur
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setMatricule(matricule);
                utilisateur.setNom(request.getNom());
                utilisateur.setPrenom(request.getPrenom());
                utilisateur.setEmail(generatedEmail);
                utilisateur.setRole(request.getRole() != null ? request.getRole()
                                : com.uasz.daos.auth.enums.Role.ENSEIGNANT);
                utilisateur.setEtat(com.uasz.daos.auth.enums.Etat.ACTIF);

                // 4. Appel au service pour sauvegarder (qui génère mdp et envoie mail)
                // Mais UtilisateurService.createUser génère aussi un MDP et envoie un mail !
                // Donc on passe l'utilisateur pré-rempli au service ET l'email personnel pour
                // l'envoi.
                return ResponseEntity.ok(utilisateurService.createUser(utilisateur, request.getEmailPersonnel()));
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CHEF_DE_DEPARTEMENT') or #id == principal.id")
        public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
                Utilisateur utilisateur = utilisateurService.findById(id)
                                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

                UserDTO userDTO = UserDTO.builder()
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

                return ResponseEntity.ok(userDTO);
        }

        @GetMapping
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CHEF_DE_DEPARTEMENT')")
        public ResponseEntity<List<UserDTO>> getAllUsers() {
                List<Utilisateur> utilisateurs = utilisateurService.findAll();

                List<UserDTO> userDTOs = utilisateurs.stream()
                                .map(u -> UserDTO.builder()
                                                .id(u.getId())
                                                .nom(u.getNom())
                                                .prenom(u.getPrenom())
                                                .email(u.getEmail())
                                                .role(u.getRole())
                                                .dateNaissance(u.getDateNaissance())
                                                .matricule(u.getMatricule())
                                                .etat(u.getEtat())
                                                .dateCreation(u.getDateCreation())
                                                .build())
                                .toList();

                return ResponseEntity.ok(userDTOs);
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('ROLE_ADMIN') or #id == principal.id")
        public ResponseEntity<UserDTO> updateUser(
                        @PathVariable Long id,
                        @RequestBody UserDTO userDTO,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                Utilisateur utilisateur = utilisateurService.findById(id)
                                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

                // Mettre à jour les champs de base
                utilisateur.setNom(userDTO.getNom());
                utilisateur.setPrenom(userDTO.getPrenom());
                utilisateur.setEmail(userDTO.getEmail());
                utilisateur.setDateNaissance(userDTO.getDateNaissance());
                utilisateur.setMatricule(userDTO.getMatricule());

                // Seul l'admin peut changer le rôle et l'état
                if (userDetails.getRole().isAdmin()) {
                        utilisateur.setRole(userDTO.getRole());
                        utilisateur.setEtat(userDTO.getEtat());
                }

                utilisateur = utilisateurService.save(utilisateur);

                UserDTO updatedDTO = UserDTO.builder()
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

                return ResponseEntity.ok(updatedDTO);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('ROLE_ADMIN')")
        public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
                utilisateurService.deleteById(id);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/search")
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CHEF_DE_DEPARTEMENT')")
        public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String query) {
                List<Utilisateur> utilisateurs = utilisateurService.searchUtilisateurs(query);

                List<UserDTO> userDTOs = utilisateurs.stream()
                                .map(u -> UserDTO.builder()
                                                .id(u.getId())
                                                .nom(u.getNom())
                                                .prenom(u.getPrenom())
                                                .email(u.getEmail())
                                                .role(u.getRole())
                                                .dateNaissance(u.getDateNaissance())
                                                .matricule(u.getMatricule())
                                                .etat(u.getEtat())
                                                .dateCreation(u.getDateCreation())
                                                .build())
                                .toList();

                return ResponseEntity.ok(userDTOs);
        }

        @GetMapping("/role/{role}")
        @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CHEF_DE_DEPARTEMENT')")
        public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
                List<Utilisateur> utilisateurs = utilisateurService.findByRole(
                                com.uasz.daos.auth.enums.Role.valueOf(role.toUpperCase()));

                List<UserDTO> userDTOs = utilisateurs.stream()
                                .map(u -> UserDTO.builder()
                                                .id(u.getId())
                                                .nom(u.getNom())
                                                .prenom(u.getPrenom())
                                                .email(u.getEmail())
                                                .role(u.getRole())
                                                .dateNaissance(u.getDateNaissance())
                                                .matricule(u.getMatricule())
                                                .etat(u.getEtat())
                                                .dateCreation(u.getDateCreation())
                                                .build())
                                .toList();

                return ResponseEntity.ok(userDTOs);
        }
}