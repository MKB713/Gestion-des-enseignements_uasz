package com.uasz.daos.auth.config;

import com.uasz.daos.auth.model.Utilisateur;
import com.uasz.daos.auth.enums.Role;
import com.uasz.daos.auth.enums.Etat;
import com.uasz.daos.auth.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    // Injection via constructeur (recommandé)
    public AdminInitializer(UtilisateurRepository utilisateurRepository,
                            PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            if (!utilisateurRepository.existsByEmail("admin@daos.com")) {
                Utilisateur admin = new Utilisateur();

                // Définir tous les champs OBLIGATOIRES selon votre table
                admin.setEmail("admin@daos.com");
                admin.setMotDePasse(passwordEncoder.encode("admin1231")); // CORRECT
                admin.setNom("Admin");
                admin.setPrenom("System");
                admin.setMatricule("ADMIN001"); // OBLIGATOIRE (unique, not null)
                admin.setRole(Role.ADMIN);
                admin.setEtat(Etat.ACTIF);

                // Définir les champs avec valeurs par défaut
                admin.setDateCreation(LocalDateTime.now());
                admin.setCompteVerrouille(false);
                admin.setTentativesConnexion(0);

                // Définir les autres champs (optionnels mais recommandés)
                admin.setAdresse("Administration");
                admin.setTelephone("+221000000000");
                admin.setDateNaissance(LocalDate.of(1990, 1, 1));

                // Initialiser dateModification (sera mis à jour par @PrePersist)
                admin.setDateModification(LocalDateTime.now());

                utilisateurRepository.save(admin);
                System.out.println("✅ Admin créé: admin@daos.com / admin1231");

                // Vérification
                System.out.println("📋 Détails de l'admin créé:");
                System.out.println("   Email: " + admin.getEmail());
                System.out.println("   Matricule: " + admin.getMatricule());
                System.out.println("   Rôle: " + admin.getRole());
                System.out.println("   État: " + admin.getEtat());

            } else {
                System.out.println("ℹ️  Admin existe déjà: admin@daos.com");

                // Optionnel: Afficher les détails de l'admin existant
                utilisateurRepository.findByEmail("admin@daos.com").ifPresent(existingAdmin -> {
                    System.out.println("📋 Détails de l'admin existant:");
                    System.out.println("   ID: " + existingAdmin.getId());
                    System.out.println("   Matricule: " + existingAdmin.getMatricule());
                    System.out.println("   Rôle: " + existingAdmin.getRole());
                    System.out.println("   État: " + existingAdmin.getEtat());
                });
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création de l'admin: " + e.getMessage());
            e.printStackTrace();
        }
    }
}