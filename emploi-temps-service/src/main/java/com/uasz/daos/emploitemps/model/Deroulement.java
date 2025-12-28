package com.uasz.daos.emploitemps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Deroulement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateValidation;

    private String statut; // e.g., EFFECTUE, ANNULE, REPORTÉ
    private double volumeHoraireEffectue;
    @Lob
    private String compteRendu;

    @OneToOne
    @JoinColumn(name = "seance_id", referencedColumnName = "id")
    private Seance seance;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(Date dateValidation) {
        this.dateValidation = dateValidation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public double getVolumeHoraireEffectue() {
        return volumeHoraireEffectue;
    }

    public void setVolumeHoraireEffectue(double volumeHoraireEffectue) {
        this.volumeHoraireEffectue = volumeHoraireEffectue;
    }

    public String getCompteRendu() {
        return compteRendu;
    }

    public void setCompteRendu(String compteRendu) {
        this.compteRendu = compteRendu;
    }

    public Seance getSeance() {
        return seance;
    }

    public void setSeance(Seance seance) {
        this.seance = seance;
    }
}
