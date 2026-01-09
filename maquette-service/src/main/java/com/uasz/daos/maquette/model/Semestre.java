package com.uasz.daos.maquette.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "semestres")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Semestre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numero;
    private String libelle;

    private int creditsTotaux;
    private double coefficientsTotaux;

    @ManyToOne
    @JoinColumn(name = "maquette_id")
    @JsonIgnore  // Évite la boucle infinie JSON avec Maquette
    private Maquette maquette;

    @OneToMany(mappedBy = "semestre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UE> ues;
}
