package com.uasz.daos.maquette.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "maquettes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Maquette {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String libelle;

    private String description;

    @Enumerated(EnumType.STRING)
    private StatutMaquette statut;

    @Column(nullable = false)
    private Integer version = 1;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    private boolean actif = true;

    @OneToOne
    @JoinColumn(name = "formation_id")
    private Formation formation;

    // Attributs ajoutés selon demande (déplacés depuis UE)
    private int credits;
    private double coefficientUE;
    private int cm;
    private int td;
    private int tp;
    private int vht;
    private String responsable;
    private String prerequis;
    @Column(columnDefinition = "TEXT")
    private String objectifs;
    private String modalitesEvaluation;

    @OneToMany(mappedBy = "maquette", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Semestre> semestres;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;

    @ManyToOne
    @JoinColumn(name = "maquette_parent_id")
    private Maquette maquetteParent;
}
