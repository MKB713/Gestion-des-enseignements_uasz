package com.uasz.daos.emploitemps.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String libelle;
    private int capacite;
    private String description;

    @ManyToOne
    @JoinColumn(name = "batiment_id")
    @JsonBackReference
    private Batiment batiment;
}
