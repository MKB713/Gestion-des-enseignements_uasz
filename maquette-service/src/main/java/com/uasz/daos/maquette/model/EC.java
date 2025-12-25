package com.uasz.daos.maquette.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private UE ue;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;
}
