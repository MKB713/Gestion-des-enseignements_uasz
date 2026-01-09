package com.uasz.daos.auth.services;

import com.uasz.daos.auth.model.Enseignant;
import com.uasz.daos.auth.model.Utilisateur;
import com.uasz.daos.auth.repository.EnseignantRepository;
import com.uasz.daos.auth.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@SuppressWarnings("null")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final EnseignantRepository enseignantRepository;

    public UserDetailsServiceImpl(UtilisateurRepository utilisateurRepository,
            EnseignantRepository enseignantRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.enseignantRepository = enseignantRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Chercher d'abord dans les utilisateurs
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findByEmail(email);
        if (utilisateurOptional.isPresent()) {
            Utilisateur utilisateur = utilisateurOptional.get();

            // Vérifier si le compte est verrouillé
            if (utilisateur.getCompteVerrouille()) {
                throw new UsernameNotFoundException("Compte verrouillé. Contactez l'administrateur.");
            }

            // Vérifier si le compte est actif
            if (utilisateur.getEtat() != com.uasz.daos.auth.enums.Etat.ACTIF) {
                throw new UsernameNotFoundException("Compte inactif. Contactez l'administrateur.");
            }

            return new CustomUserDetails(utilisateur);
        }

        // Chercher ensuite dans les enseignants
        Optional<Enseignant> enseignantOptional = enseignantRepository.findByEmail(email);
        if (enseignantOptional.isPresent()) {
            return new CustomUserDetails(enseignantOptional.get());
        }

        throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email);
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        // Chercher d'abord dans les utilisateurs
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findById(id);
        if (utilisateurOptional.isPresent()) {
            return new CustomUserDetails(utilisateurOptional.get());
        }

        // Chercher ensuite dans les enseignants
        Optional<Enseignant> enseignantOptional = enseignantRepository.findById(id);
        if (enseignantOptional.isPresent()) {
            return new CustomUserDetails(enseignantOptional.get());
        }

        throw new UsernameNotFoundException("Utilisateur non trouvé avec l'ID : " + id);
    }
}