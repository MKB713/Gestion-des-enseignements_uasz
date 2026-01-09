package com.uasz.daos.auth.services;

import com.uasz.daos.auth.enums.ResultatAction;
import com.uasz.daos.auth.model.PasswordResetToken;
import com.uasz.daos.auth.model.Utilisateur;
import com.uasz.daos.auth.repository.PasswordResetTokenRepository;
import com.uasz.daos.auth.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public PasswordResetService(UtilisateurRepository utilisateurRepository,
            PasswordResetTokenRepository tokenRepository,
            MailService mailService,
            PasswordEncoder passwordEncoder,
            AuditLogService auditLogService) {
        this.utilisateurRepository = utilisateurRepository;
        this.tokenRepository = tokenRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public boolean requestPasswordReset(String email, String ipAddress, String userAgent) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);

        if (utilisateurOpt.isEmpty()) {
            // Pour des raisons de sécurité, on ne révèle pas si l'email existe
            return true;
        }

        Utilisateur utilisateur = utilisateurOpt.get();

        // Invalider les tokens existants
        tokenRepository.invalidateUserTokens(utilisateur);

        // Créer un nouveau token
        PasswordResetToken token = new PasswordResetToken();
        token.setUtilisateur(utilisateur);
        token.setAdresseIp(ipAddress);
        token.setUserAgent(userAgent);
        tokenRepository.save(token);

        // Envoyer l'email
        mailService.sendPasswordResetEmail(email, token.getToken());

        // Audit log
        auditLogService.logAction(utilisateur, "DEMANDE_REINITIALISATION_MDP",
                "PasswordReset", ResultatAction.SUCCES,
                "Demande envoyée à " + email + " depuis IP: " + ipAddress);

        return true;
    }

    @Transactional
    public boolean validateToken(String tokenValue) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(tokenValue);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken token = tokenOpt.get();
        return token.isValid();
    }

    @Transactional
    public boolean resetPassword(String tokenValue, String newPassword, String confirmPassword,
            String ipAddress, String userAgent) {

        if (!newPassword.equals(confirmPassword)) {
            return false;
        }

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(tokenValue);

        if (tokenOpt.isEmpty() || !tokenOpt.get().isValid()) {
            return false;
        }

        PasswordResetToken token = tokenOpt.get();
        Utilisateur utilisateur = token.getUtilisateur();

        // Valider la force du mot de passe
        if (!isPasswordStrong(newPassword)) {
            return false;
        }

        // Mettre à jour le mot de passe
        String newPasswordHash = passwordEncoder.encode(newPassword);
        utilisateur.setMotDePasse(newPasswordHash);
        utilisateur.setCompteVerrouille(Boolean.valueOf(false));
        utilisateur.setTentativesConnexion(Integer.valueOf(0));
        utilisateurRepository.save(utilisateur);

        // Marquer le token comme utilisé
        token.setUsed(true);
        token.setAdresseIp(ipAddress);
        token.setUserAgent(userAgent);
        tokenRepository.save(token);

        // Invalider tous les autres tokens
        tokenRepository.invalidateUserTokens(utilisateur);

        // Envoyer email de confirmation
        mailService.sendPasswordChangedEmail(utilisateur.getEmail(),
                utilisateur.getNom(), utilisateur.getPrenom());

        // Audit log
        auditLogService.logAction(utilisateur, "REINITIALISATION_MDP",
                "PasswordReset", ResultatAction.SUCCES,
                "Mot de passe réinitialisé depuis IP: " + ipAddress);

        return true;
    }

    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.cleanupExpiredTokens();
    }

    private boolean isPasswordStrong(String password) {
        // Au moins 8 caractères
        if (password.length() < 8)
            return false;

        // Contient au moins une majuscule, une minuscule, un chiffre
        boolean hasUpper = false, hasLower = false, hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c))
                hasUpper = true;
            else if (Character.isLowerCase(c))
                hasLower = true;
            else if (Character.isDigit(c))
                hasDigit = true;
        }

        return hasUpper && hasLower && hasDigit;
    }

    @Transactional(readOnly = true)
    public Optional<Utilisateur> getUserByToken(String tokenValue) {
        return tokenRepository.findByToken(tokenValue)
                .filter(PasswordResetToken::isValid)
                .map(PasswordResetToken::getUtilisateur);
    }
}