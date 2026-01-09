package com.uasz.daos.maquette.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "ues")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;
    private String libelle;

    private int credits;
    private double coefficientUE;

    // Volumes horaires totaux
    private int cm;
    private int td;
    private int tp;
    private int vht;

    // Champs pour compatibilité avec les services existants
    private java.util.Date dateCreation;
    private boolean active = true;
    private boolean archive = false;

    @ManyToOne
    @JoinColumn(name = "semestre_id")
    private Semestre semestre;

    @OneToMany(mappedBy = "ue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EC> ecs;

    @OneToMany(mappedBy = "ue")
    @JsonIgnore // Évite la boucle infinie JSON avec Module
    private List<Module> modules;

    private String description;
}
