package com.uasz.daos.auth.services;

import com.uasz.daos.auth.model.Enseignant;
import com.uasz.daos.auth.model.Utilisateur;
import com.uasz.daos.auth.enums.Role;
import com.uasz.daos.auth.enums.Etat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final Utilisateur utilisateur;
    private final Enseignant enseignant;

    // Constructeur pour Utilisateur
    public CustomUserDetails(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        this.enseignant = null;
    }

    // Constructeur pour Enseignant
    public CustomUserDetails(Enseignant enseignant) {
        this.utilisateur = null;
        this.enseignant = enseignant;
    }

    // -------- Accès unifié aux données --------
    public Long getId() {
        if (utilisateur != null) {
            return utilisateur.getId();
        } else if (enseignant != null) {
            return enseignant.getId();
        }
        return null;
    }

    public String getNom() {
        if (utilisateur != null) {
            return utilisateur.getNom();
        } else if (enseignant != null) {
            return enseignant.getNom();
        }
        return "";
    }

    public String getPrenom() {
        if (utilisateur != null) {
            return utilisateur.getPrenom();
        } else if (enseignant != null) {
            return enseignant.getPrenom();
        }
        return "";
    }

    public Role getRole() {
        if (utilisateur != null) {
            return utilisateur.getRole();
        } else {
            // Pour les enseignants, on retourne le rôle ENSEIGNANT par défaut
            return Role.ENSEIGNANT;
        }
    }

    public Etat getEtat() {
        if (utilisateur != null) {
            return utilisateur.getEtat();
        } else if (enseignant != null) {
            return enseignant.getEtat();
        }
        return null;
    }

    /** Retourne l'entité d'origine (Utilisateur OU Enseignant) */
    public Object getEntity() {
        return (utilisateur != null) ? utilisateur : enseignant;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public Enseignant getEnseignant() {
        return enseignant;
    }

    // -------- Implémentation UserDetails --------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = getRole();
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        if (utilisateur != null) {
            return utilisateur.getPassword();
        } else {
            // Pour les enseignants, pas de mot de passe dans ce système
            return "";
        }
    }

    @Override
    public String getUsername() {
        if (utilisateur != null) {
            return utilisateur.getEmail();
        } else if (enseignant != null) {
            return enseignant.getEmail();
        }
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (utilisateur != null) {
            return !utilisateur.getCompteVerrouille();
        } else if (enseignant != null) {
            return true; // Les enseignants ne sont jamais verrouillés dans ce système
        }
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (utilisateur != null) {
            return utilisateur.getEtat() == Etat.ACTIF;
        } else if (enseignant != null) {
            return true; // Les enseignants sont toujours actifs dans ce système
        }
        return false;
    }
}