package com.uasz.daos.enseignant.model;

import com.uasz.daos.enseignant.enums.Statut;
import com.uasz.daos.enseignant.enums.StatutEnseignant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "enseignants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enseignant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long matricule;

    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;

    @Column(unique = true)
    private String email; // Email Institutionnel

    private String mailPersonnel; // Email Personnel

    private String grade;
    private LocalDate dateEmbauche;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private LocalDate dateNaissance;
    private String lieuNaissance;

    @Enumerated(EnumType.STRING)
    private Statut statut;

    @Enumerated(EnumType.STRING)
    private StatutEnseignant statutEnseignant;

    private boolean estActif;
    private String specialite;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }
}
