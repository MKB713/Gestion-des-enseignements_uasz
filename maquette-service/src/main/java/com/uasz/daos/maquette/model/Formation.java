package com.uasz.daos.maquette.model;

import com.uasz.daos.maquette.enums.StatutFormation;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "formations")
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    @Column(unique = true)
    private String libelle;

    private String description;
    private Date dateCreation;

    @ManyToOne
    @JoinColumn(name = "filiere_id")
    private Filiere filiere;

    @ManyToOne
    @JoinColumn(name = "niveau_id")
    private Niveau niveau;

    // Si vous n'avez pas encore la classe Maquette, commentez ces lignes :
    @OneToOne(mappedBy = "formation")
    private Maquette maquette;

    @Enumerated(EnumType.STRING)
    private StatutFormation statutFormation = StatutFormation.ACTIVE;

    public Formation() {
    }

    public Maquette getMaquette() {
        return maquette;
    }

    public void setMaquette(Maquette maquette) {
        this.maquette = maquette;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Filiere getFiliere() {
        return filiere;
    }

    public void setFiliere(Filiere filiere) {
        this.filiere = filiere;
    }

    public Niveau getNiveau() {
        return niveau;
    }

    public void setNiveau(Niveau niveau) {
        this.niveau = niveau;
    }

    public StatutFormation getStatutFormation() {
        return statutFormation;
    }

    public void setStatutFormation(StatutFormation statutFormation) {
        this.statutFormation = statutFormation;
    }
}
