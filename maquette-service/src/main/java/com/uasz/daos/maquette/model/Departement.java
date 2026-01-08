package com.uasz.daos.maquette.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "departements")
public class Departement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String libelle;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    public Departement() {
    }

    public Departement(Long id, String libelle, String description, Date dateCreation) {
        this.id = id;
        this.libelle = libelle;
        this.description = description;
        this.dateCreation = dateCreation;
    }

    @PrePersist
    public void prePersist() {
        this.dateCreation = new Date();
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }
}
