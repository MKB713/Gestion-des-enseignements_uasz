package com.uasz.daos.auth.dto;

import com.uasz.daos.auth.enums.Role;
import com.uasz.daos.auth.validation.MinimumAge;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class RegisterRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    // Email institutionnel (généré automatiquement si vide)
    private String email;

    // Email personnel pour recevoir les identifiants
    @NotBlank(message = "L'email personnel est obligatoire")
    @Email(message = "Format d'email personnel invalide")
    private String emailPersonnel;

    // Mot de passe (généré automatiquement si vide)
    private String password;

    @NotNull(message = "La date de naissance est obligatoire")
    @MinimumAge(value = 18, message = "L'utilisateur doit avoir au moins 18 ans")
    private LocalDate dateNaissance;

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;

    // Matricule (généré automatiquement si vide)
    private String matricule;

    private String telephone;

    private String adresse;

    public RegisterRequest() {
    }

    public RegisterRequest(String nom, String prenom, String emailPersonnel, Role role, LocalDate dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.emailPersonnel = emailPersonnel;
        this.role = role;
        this.dateNaissance = dateNaissance;
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

    public String getEmailPersonnel() {
        return emailPersonnel;
    }

    public void setEmailPersonnel(String emailPersonnel) {
        this.emailPersonnel = emailPersonnel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
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
}
