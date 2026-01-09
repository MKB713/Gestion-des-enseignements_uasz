package com.uasz.daos.maquette.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "ecs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;
    private String libelle;

    // Volumes horaires détaillés
    private int cm;
    private int td;
    private int tp;
    private int tpe;
    private int vht;

    private double coefficient;

    private boolean archive = false;
    private boolean actif = true;

    @ManyToOne
    @JoinColumn(name = "ue_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private UE ue;

    @OneToMany(mappedBy = "ec")
    @JsonIgnore // Évite la boucle infinie JSON avec Module
    private List<Module> modules;

    private String description;
}
