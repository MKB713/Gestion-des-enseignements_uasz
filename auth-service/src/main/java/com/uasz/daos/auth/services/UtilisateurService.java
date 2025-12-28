package com.uasz.daos.auth.services;

import com.uasz.daos.auth.enums.Etat;
import com.uasz.daos.auth.enums.Role;
import com.uasz.daos.auth.model.PasswordHistory;
import com.uasz.daos.auth.model.Utilisateur;
import com.uasz.daos.auth.repository.PasswordHistoryRepository;
import com.uasz.daos.auth.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final AuditLogService auditLogService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*";
    private static final SecureRandom RANDOM = new SecureRandom();

    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                              PasswordHistoryRepository passwordHistoryRepository,
                              PasswordEncoder passwordEncoder,
                              MailService mailService,
                              AuditLogService auditLogService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordHistoryRepository = passwordHistoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.auditLogService = auditLogService;
    }

    // MÉTHODES DE BASE
    @Transactional(readOnly = true)
    public Optional<Utilisateur> findById(Long id) {
        return utilisateurRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> findByRole(Role role) {
        return utilisateurRepository.findByRole(role);
    }

    @Transactional
    public Utilisateur save(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void deleteById(Long id) {
        utilisateurRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return utilisateurRepository.count();
    }

    @Transactional(readOnly = true)
    public long countByRole(Role role) {
        return utilisateurRepository.countByRole(role);
    }

    // CRÉATION D'UTILISATEUR AVEC GÉNÉRATION AUTOMATIQUE DE MOT DE PASSE
    @Transactional
    public Utilisateur createUser(Utilisateur utilisateur) {
        // Vérifier l'unicité de l'email
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Vérifier l'unicité du matricule
        if (utilisateurRepository.existsByMatricule(utilisateur.getMatricule())) {
            throw new RuntimeException("Un utilisateur avec ce matricule existe déjà");
        }

        // Générer un mot de passe aléatoire sécurisé
        String generatedPassword = generateSecurePassword(12);
        utilisateur.setMotDePasse(passwordEncoder.encode(generatedPassword));

        // Définir l'état par défaut
        if (utilisateur.getEtat() == null) {
            utilisateur.setEtat(Etat.ACTIF);
        }

        // Sauvegarder l'utilisateur
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);

        // Enregistrer dans l'historique des mots de passe
        savePasswordHistory(savedUser, savedUser.getMotDePasse());

        // Envoyer l'email de bienvenue avec le mot de passe
        mailService.sendWelcomeEmail(
                savedUser.getEmail(),
                savedUser.getNom(),
                savedUser.getPrenom(),
                savedUser.getMatricule(),
                generatedPassword
        );

        // Audit log
        auditLogService.logAction(savedUser, "CREATION_UTILISATEUR", "Utilisateur",
                com.uasz.daos.auth.enums.ResultatAction.SUCCES,
                "Utilisateur créé avec succès");

        return savedUser;
    }

    // MISE À JOUR D'UTILISATEUR
    @Transactional
    public Utilisateur updateUser(Utilisateur utilisateur) {
        Optional<Utilisateur> existingUserOpt = utilisateurRepository.findById(utilisateur.getId());

        if (existingUserOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        Utilisateur existingUser = existingUserOpt.get();

        // Vérifier l'unicité de l'email si modifié
        if (!existingUser.getEmail().equals(utilisateur.getEmail())
                && utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Vérifier l'unicité du matricule si modifié
        if (!existingUser.getMatricule().equals(utilisateur.getMatricule())
                && utilisateurRepository.existsByMatricule(utilisateur.getMatricule())) {
            throw new RuntimeException("Un utilisateur avec ce matricule existe déjà");
        }

        // Mettre à jour les champs
        existingUser.setNom(utilisateur.getNom());
        existingUser.setPrenom(utilisateur.getPrenom());
        existingUser.setEmail(utilisateur.getEmail());
        existingUser.setMatricule(utilisateur.getMatricule());
        existingUser.setDateNaissance(utilisateur.getDateNaissance());
        existingUser.setTelephone(utilisateur.getTelephone());
        existingUser.setAdresse(utilisateur.getAdresse());
        existingUser.setRole(utilisateur.getRole());
        existingUser.setEtat(utilisateur.getEtat());

        Utilisateur updatedUser = utilisateurRepository.save(existingUser);

        // Audit log
        auditLogService.logAction(updatedUser, "MODIFICATION_UTILISATEUR", "Utilisateur",
                com.uasz.daos.auth.enums.ResultatAction.SUCCES,
                "Utilisateur modifié avec succès");

        return updatedUser;
    }

    // CHANGEMENT DE MOT DE PASSE
    @Transactional
    public boolean updatePassword(String email, String currentPassword, String newPassword, String confirmPassword) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);

        if (utilisateurOpt.isEmpty()) {
            return false;
        }

        Utilisateur utilisateur = utilisateurOpt.get();

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(currentPassword, utilisateur.getMotDePasse())) {
            return false;
        }

        // Vérifier que les nouveaux mots de passe correspondent
        if (!newPassword.equals(confirmPassword)) {
            return false;
        }

        // Valider la force du mot de passe
        if (!isPasswordStrong(newPassword)) {
            return false;
        }

        // Vérifier que le nouveau mot de passe n'a pas été utilisé récemment
        String newPasswordHash = passwordEncoder.encode(newPassword);
        if (isPasswordInHistory(utilisateur, newPasswordHash, 5)) {
            return false; // Empêcher la réutilisation des 5 derniers mots de passe
        }

        // Mettre à jour le mot de passe
        utilisateur.setMotDePasse(newPasswordHash);
        utilisateurRepository.save(utilisateur);

        // Enregistrer dans l'historique
        savePasswordHistory(utilisateur, newPasswordHash);

        // Audit log
        auditLogService.logAction(utilisateur, "CHANGEMENT_MOT_DE_PASSE", "Utilisateur",
                com.uasz.daos.auth.enums.ResultatAction.SUCCES,
                "Mot de passe changé avec succès");

        return true;
    }

    // GESTION DE L'ÉTAT DES UTILISATEURS
    @Transactional
    public void archiverUser(Long id) {
        utilisateurRepository.findById(id).ifPresent(user -> {
            user.setEtat(Etat.ARCHIVE);
            utilisateurRepository.save(user);

            auditLogService.logAction(user, "ARCHIVAGE_UTILISATEUR", "Utilisateur",
                    com.uasz.daos.auth.enums.ResultatAction.SUCCES,
                    "Utilisateur archivé");
        });
    }

    @Transactional
    public void desarchiverUser(Long id) {
        utilisateurRepository.findById(id).ifPresent(user -> {
            user.setEtat(Etat.ACTIF);
            utilisateurRepository.save(user);

            auditLogService.logAction(user, "DESARCHIVAGE_UTILISATEUR", "Utilisateur",
                    com.uasz.daos.auth.enums.ResultatAction.SUCCES,
                    "Utilisateur désarchivé");
        });
    }

    @Transactional
    public void activerUser(Long id) {
        utilisateurRepository.findById(id).ifPresent(user -> {
            user.setEtat(Etat.ACTIF);
            utilisateurRepository.save(user);

            auditLogService.logAction(user, "ACTIVATION_UTILISATEUR", "Utilisateur",
                    com.uasz.daos.auth.enums.ResultatAction.SUCCES,
                    "Utilisateur activé");
        });
    }

    @Transactional
    public void desactiverUser(Long id) {
        utilisateurRepository.findById(id).ifPresent(user -> {
            user.setEtat(Etat.INACTIF);
            utilisateurRepository.save(user);

            auditLogService.logAction(user, "DESACTIVATION_UTILISATEUR", "Utilisateur",
                    com.uasz.daos.auth.enums.ResultatAction.SUCCES,
                    "Utilisateur désactivé");
        });
    }

    // MÉTHODES DE RECHERCHE
    @Transactional(readOnly = true)
    public List<Utilisateur> getAllUsersArchives() {
        return utilisateurRepository.findByEtat(Etat.ARCHIVE);
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> findAll(Utilisateur currentUser) {
        // Filtrer selon les permissions de l'utilisateur courant
        if (currentUser.getRole().isAdmin()) {
            return utilisateurRepository.findAll();
        } else {
            // Retourner seulement les utilisateurs de rôle inférieur ou égal
            return utilisateurRepository.findAll().stream()
                    .filter(u -> u.getRole().getNiveau() <= currentUser.getRole().getNiveau())
                    .toList();
        }
    }

    @Transactional(readOnly = true)
    public List<Utilisateur> searchUtilisateurs(String searchTerm) {
        return utilisateurRepository.searchUtilisateurs(searchTerm);
    }

    // MÉTHODES D'AIDE PRIVÉES
    private String generateSecurePassword(int length) {
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }

    private boolean isPasswordStrong(String password) {
        // Au moins 8 caractères
        if (password.length() < 8) return false;

        // Contient au moins une majuscule, une minuscule, un chiffre et un caractère spécial
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if ("@#$%&*".indexOf(c) >= 0) hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private void savePasswordHistory(Utilisateur utilisateur, String passwordHash) {
        PasswordHistory history = new PasswordHistory();
        history.setUtilisateur(utilisateur);
        history.setPasswordHash(passwordHash);
        passwordHistoryRepository.save(history);

        // Nettoyer l'historique ancien (garder seulement les 10 derniers)
        List<PasswordHistory> histories = passwordHistoryRepository.findByUtilisateurOrderByChangedAtDesc(utilisateur);
        if (histories.size() > 10) {
            for (int i = 10; i < histories.size(); i++) {
                passwordHistoryRepository.delete(histories.get(i));
            }
        }
    }

    private boolean isPasswordInHistory(Utilisateur utilisateur, String passwordHash, int lastN) {
        List<PasswordHistory> histories = passwordHistoryRepository.findByUtilisateurOrderByChangedAtDesc(utilisateur);

        // Vérifier les N derniers mots de passe
        int limit = Math.min(lastN, histories.size());
        for (int i = 0; i < limit; i++) {
            if (histories.get(i).getPasswordHash().equals(passwordHash)) {
                return true;
            }
        }

        return false;
    }

    // RÉINITIALISATION DE MOT DE PASSE (pour mot de passe oublié)
    @Transactional
    public boolean resetPassword(String token, String newPassword, String confirmPassword) {
        // Cette méthode sera complétée avec le service PasswordResetService
        return false;
    }

    // VÉRIFICATION DE L'ÉTAT DU COMPTE
    @Transactional(readOnly = true)
    public boolean isAccountActive(Long userId) {
        return utilisateurRepository.findById(userId)
                .map(user -> user.getEtat() == Etat.ACTIF && !user.getCompteVerrouille())
                .orElse(false);
    }

    // DÉVERROUILLAGE DE COMPTE
    @Transactional
    public void unlockAccount(Long userId) {
        utilisateurRepository.findById(userId).ifPresent(user -> {
            user.setCompteVerrouille(false);
            user.setTentativesConnexion(0);
            utilisateurRepository.save(user);

            auditLogService.logAction(user, "DEVERROUILLAGE_COMPTE", "Utilisateur",
                    com.uasz.daos.auth.enums.ResultatAction.SUCCES,
                    "Compte déverrouillé");
        });
    }
}