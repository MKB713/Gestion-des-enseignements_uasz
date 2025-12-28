package com.uasz.daos.maquette.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "classes")
public class Classe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nom; // Ex: L1 Informatique - A

    @Column(nullable = false)
    private String semestre; // Ex: Semestre 1, Semestre 2

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation = new Date();

    private boolean archive = false;

    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;
}
