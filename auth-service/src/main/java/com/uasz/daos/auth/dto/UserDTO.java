package com.uasz.daos.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.uasz.daos.auth.enums.Etat;
import com.uasz.daos.auth.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateNaissance;

    private String matricule;
    private Etat etat;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateCreation;

    public UserDTO() {
    }

    public UserDTO(Long id, String nom, String prenom, String email, Role role, LocalDate dateNaissance, String matricule, Etat etat, LocalDateTime dateCreation) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.dateNaissance = dateNaissance;
        this.matricule = matricule;
        this.etat = etat;
        this.dateCreation = dateCreation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String nom;
        private String prenom;
        private String email;
        private Role role;
        private LocalDate dateNaissance;
        private String matricule;
        private Etat etat;
        private LocalDateTime dateCreation;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nom(String nom) {
            this.nom = nom;
            return this;
        }

        public Builder prenom(String prenom) {
            this.prenom = prenom;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder dateNaissance(LocalDate dateNaissance) {
            this.dateNaissance = dateNaissance;
            return this;
        }

        public Builder matricule(String matricule) {
            this.matricule = matricule;
            return this;
        }

        public Builder etat(Etat etat) {
            this.etat = etat;
            return this;
        }

        public Builder dateCreation(LocalDateTime dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(id, nom, prenom, email, role, dateNaissance, matricule, etat, dateCreation);
        }
    }
}
