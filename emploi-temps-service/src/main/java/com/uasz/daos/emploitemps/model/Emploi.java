package com.uasz.daos.emploitemps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Emploi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle;

    @Temporal(TemporalType.DATE)
    private Date dateCreation;

    // Assuming 'Emploi' is the owning side of the relationship
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "emploi_seance",
        joinColumns = @JoinColumn(name = "emploi_id"),
        inverseJoinColumns = @JoinColumn(name = "seance_id")
    )
    private List<Seance> seances;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<Seance> getSeances() {
        return seances;
    }

    public void setSeances(List<Seance> seances) {
        this.seances = seances;
    }
}
