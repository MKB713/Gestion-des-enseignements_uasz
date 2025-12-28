package com.uasz.daos.auth.services;

import com.uasz.daos.auth.dto.AuthenticationRequest;
import com.uasz.daos.auth.dto.AuthenticationResponse;
import com.uasz.daos.auth.dto.RegisterRequest;
import com.uasz.daos.auth.enums.Etat;
import com.uasz.daos.auth.enums.ResultatAction;
import com.uasz.daos.auth.enums.Role;
import com.uasz.daos.auth.exception.MatriculeAlreadyExistsException;
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

    public AuthService(
            UtilisateurRepository utilisateurRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            AuditLogService auditLogService
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new MatriculeAlreadyExistsException("Un utilisateur avec cet email existe déjà");
        }

        // Vérifier si le matricule existe déjà
        if (utilisateurRepository.existsByMatricule(request.getMatricule())) {
            throw new MatriculeAlreadyExistsException("Un utilisateur avec ce matricule existe déjà");
        }

        // Créer l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setMatricule(request.getMatricule());
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getPassword())); // CORRIGÉ ICI
        utilisateur.setDateNaissance(request.getDateNaissance());
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setAdresse(request.getAdresse());
        utilisateur.setRole(request.getRole() != null ? request.getRole() : Role.ETUDIANT);
        utilisateur.setEtat(Etat.ACTIF);

        utilisateur = utilisateurRepository.save(utilisateur);

        // Générer les tokens
        String accessToken = jwtService.generateToken(utilisateur);
        String refreshToken = jwtService.generateRefreshToken(utilisateur);

        // Sauvegarder le refresh token
        saveRefreshToken(utilisateur, refreshToken);

        // Log de l'action
        auditLogService.logAction(utilisateur, "INSCRIPTION", "Utilisateur", ResultatAction.SUCCES, null);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(86400L) // 24 heures
                .build();
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect"));
        System.out.println("Utilisateur trouvé : " + utilisateur.getEmail());
        System.out.println("Compte verrouillé ? " + utilisateur.getCompteVerrouille());
        System.out.println("Mot de passe hashé en DB : " + utilisateur.getMotDePasse());
        System.out.println("Mot de passe envoyé : " + request.getPassword());
        // Vérifier si le compte est verrouillé
        if (utilisateur.getCompteVerrouille()) {
            throw new BadCredentialsException("Compte verrouillé. Contactez l'administrateur.");
        }

        try {
            System.out.println("Authentification : email=" + request.getEmail() + ", password=" + request.getPassword());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword() // CORRIGÉ ICI
                    )
            );

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

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(86400L)
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
            auditLogService.logAction(utilisateur, "TENTATIVE_CONNEXION_ECHEC", "Utilisateur", ResultatAction.ECHEC, null);

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