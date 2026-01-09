package com.uasz.daos.auth.services;

import com.uasz.daos.auth.dto.AuthenticationRequest;
import com.uasz.daos.auth.dto.AuthenticationResponse;
import com.uasz.daos.auth.dto.RegisterRequest;
import com.uasz.daos.auth.enums.Etat;
import com.uasz.daos.auth.enums.ResultatAction;
import com.uasz.daos.auth.enums.Role;

import com.uasz.daos.auth.model.*;
import com.uasz.daos.auth.repository.RefreshTokenRepository;
import com.uasz.daos.auth.repository.UtilisateurRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuditLogService auditLogService;
    private final MailService mailService;

    public AuthService(
            UtilisateurRepository utilisateurRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            AuditLogService auditLogService,
            MailService mailService // Injection MailService
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.auditLogService = auditLogService;
        this.mailService = mailService;
    }

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        // 1. Générer le Matricule Automatique (Année + Séquence)
        String year = String.valueOf(java.time.Year.now().getValue());
        long count = utilisateurRepository.count() + 1;
        String matricule = year + String.format("%04d", count);

        // Vérifier unicité et incrémenter si nécessaire (boucle simple pour MVP)
        while (utilisateurRepository.existsByMatricule(matricule)) {
            count++;
            matricule = year + String.format("%04d", count);
        }

        // 2. Générer l'Email Institutionnel (P.N + 3 chiffres aléatoires +
        // @zig.univ.sn)
        char firstP = request.getPrenom() != null && !request.getPrenom().isEmpty()
                ? request.getPrenom().toLowerCase().charAt(0)
                : 'x';
        char firstN = request.getNom() != null && !request.getNom().isEmpty() ? request.getNom().toLowerCase().charAt(0)
                : 'x';
        String randomDigits = String.format("%03d", (int) (Math.random() * 1000));
        String generatedEmail = firstP + "." + firstN + randomDigits + "@zig.univ.sn";

        // Vérifier unicité email
        while (utilisateurRepository.existsByEmail(generatedEmail)) {
            randomDigits = String.format("%03d", (int) (Math.random() * 1000));
            generatedEmail = firstP + "." + firstN + randomDigits + "@zig.univ.sn";
        }

        // 3. Générer le Mot de passe
        String generatedPassword = generateSecurePassword(10); // Helper method needed or duplicate logic
        System.out.println("Generated Password: " + generatedPassword);
        System.out.println("Encoded Password: " + passwordEncoder.encode(generatedPassword));
        // Créer l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setMatricule(matricule);
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(generatedEmail); // Email Institutionnel
        utilisateur.setMotDePasse(passwordEncoder.encode(generatedPassword));
        utilisateur.setDateNaissance(request.getDateNaissance());
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setAdresse(request.getAdresse());
        utilisateur.setRole(request.getRole() != null ? request.getRole() : Role.ETUDIANT);
        utilisateur.setEtat(Etat.ACTIF); // Actif par défaut

        utilisateur = utilisateurRepository.save(utilisateur);

        // 4. Envoyer l'email avec les identifiants
        if (request.getEmailPersonnel() != null && !request.getEmailPersonnel().isEmpty()) {
            mailService.sendWelcomeEmail(
                    request.getEmailPersonnel(), // Envoi au mail personnel
                    request.getNom(),
                    request.getPrenom(),
                    generatedEmail, // On envoie l'email institutionnel comme login
                    generatedPassword);
        }

        // Générer les tokens
        String accessToken = jwtService.generateToken(utilisateur);
        String refreshToken = jwtService.generateRefreshToken(utilisateur);

        saveRefreshToken(utilisateur, refreshToken);
        auditLogService.logAction(utilisateur, "INSCRIPTION_AUTO", "Utilisateur", ResultatAction.SUCCES,
                "Compte créé avec " + generatedEmail);

        AuthenticationResponse.UserInfo userInfo = AuthenticationResponse.UserInfo.builder()
                .id(utilisateur.getId())
                .email(utilisateur.getEmail())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .role(utilisateur.getRole())
                .build();

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(86400L)
                .user(userInfo)
                .build();
    }

    private String generateSecurePassword(int length) {
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        StringBuilder sb = new StringBuilder(length);
        java.util.Random random = new java.security.SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect"));

        // Vérifier si le compte est verrouillé
        if (utilisateur.getCompteVerrouille()) {
            throw new BadCredentialsException("Compte verrouillé. Contactez l'administrateur.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword() // CORRIGÉ ICI
                    ));

            // Réinitialiser les tentatives de connexion
            utilisateur.setTentativesConnexion(0);
            utilisateur.setDerniereConnexion(LocalDateTime.now());
            utilisateurRepository.save(utilisateur);

            // Générer les tokens
            String accessToken = jwtService.generateToken(utilisateur);
            String refreshToken = jwtService.generateRefreshToken(utilisateur);

            // Révoquer les anciens refresh tokens
            revokeAllUserTokens(utilisateur);

            // Sauvegarder le nouveau refresh token
            saveRefreshToken(utilisateur, refreshToken);

            // Log de l'action
            auditLogService.logAction(utilisateur, "CONNEXION", "Utilisateur", ResultatAction.SUCCES, null);

            // Créer les informations utilisateur
            AuthenticationResponse.UserInfo userInfo = AuthenticationResponse.UserInfo.builder()
                    .id(utilisateur.getId())
                    .email(utilisateur.getEmail())
                    .nom(utilisateur.getNom())
                    .prenom(utilisateur.getPrenom())
                    .role(utilisateur.getRole())
                    .build();

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(86400L)
                    .user(userInfo)
                    .build();

        } catch (BadCredentialsException e) {
            // Incrémenter les tentatives de connexion
            int tentatives = utilisateur.getTentativesConnexion() + 1;
            utilisateur.setTentativesConnexion(tentatives);

            // Verrouiller le compte après 5 tentatives
            if (tentatives >= 5) {
                utilisateur.setCompteVerrouille(true);
            }

            utilisateurRepository.save(utilisateur);

            // Log de l'action
            auditLogService.logAction(utilisateur, "TENTATIVE_CONNEXION_ECHEC", "Utilisateur", ResultatAction.ECHEC,
                    null);

            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }
    }

    @Transactional
    public AuthenticationResponse refreshToken(String refreshToken) {
        String userEmail = jwtService.extractUsername(refreshToken);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadCredentialsException("Token invalide"));

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Token invalide"));

        if (storedToken.getRevoque() || storedToken.isExpired()) {
            throw new BadCredentialsException("Token expiré ou révoqué");
        }

        if (!jwtService.isTokenValid(refreshToken, utilisateur)) {
            throw new BadCredentialsException("Token invalide");
        }

        String newAccessToken = jwtService.generateToken(utilisateur);

        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expiresIn(86400L)
                .build();
    }

    private void saveRefreshToken(Utilisateur utilisateur, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUtilisateur(utilisateur);
        refreshToken.setDateExpiration(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoque(false);
        refreshTokenRepository.save(refreshToken);
    }

    private void revokeAllUserTokens(Utilisateur utilisateur) {
        var validTokens = refreshTokenRepository.findValidTokensByUtilisateur(utilisateur, LocalDateTime.now());
        if (!validTokens.isEmpty()) {
            validTokens.forEach(token -> token.setRevoque(true));
            refreshTokenRepository.saveAll(validTokens);
        }
    }
}