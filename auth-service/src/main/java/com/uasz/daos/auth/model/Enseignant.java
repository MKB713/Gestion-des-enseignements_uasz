package com.uasz.daos.auth.model;

import com.uasz.daos.auth.enums.Etat;
import com.uasz.daos.auth.enums.StatutEnseignant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "enseignants")
@PrimaryKeyJoinColumn(name = "utilisateur_id")
public class Enseignant extends Utilisateur {

    @Column(name = "specialite")
    @Size(max = 200, message = "La spécialité ne peut pas dépasser 200 caractères")
    private String specialite;

    @Column(name = "grade")
    @Size(max = 100, message = "Le grade ne peut pas dépasser 100 caractères")
    private String grade;

    @Column(name = "departement")
    @Size(max = 200, message = "Le département ne peut pas dépasser 200 caractères")
    private String departement;

    @Column(name = "bureau")
    @Size(max = 50, message = "Le bureau ne peut pas dépasser 50 caractères")
    private String bureau;

    @Column(name = "date_recrutement")
    private LocalDate dateRecrutement;

    @Column(name = "cv_url")
    private String cvUrl;

    @Column(name = "statut")
    @Enumerated(EnumType.STRING)
    private StatutEnseignant statut = StatutEnseignant.PERMANENT;

    @Column(name = "heures_statutaires")
    private Integer heuresStatutaires;

    // Ajouter pour compatibilité avec CustomUserDetails
    public boolean isEstActif() {
        return getEtat() == Etat.ACTIF;
    }

    // Getters and Setters
    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getBureau() {
        return bureau;
    }

    public void setBureau(String bureau) {
        this.bureau = bureau;
    }

    public LocalDate getDateRecrutement() {
        return dateRecrutement;
    }

    public void setDateRecrutement(LocalDate dateRecrutement) {
        this.dateRecrutement = dateRecrutement;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public StatutEnseignant getStatut() {
        return statut;
    }

    public void setStatut(StatutEnseignant statut) {
        this.statut = statut;
    }

    public Integer getHeuresStatutaires() {
        return heuresStatutaires;
    }

    public void setHeuresStatutaires(Integer heuresStatutaires) {
        this.heuresStatutaires = heuresStatutaires;
    }
}