package com.uasz.daos.choix.model;

import com.uasz.daos.choix.enums.TypeEnseignement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "repartition")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Repartition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Référence à l'UE (pour l'affichage groupé)
    @Column(nullable = false)
    private Long ueId;

    // Référence à l'EC spécifique
    @Column(nullable = false)
    private Long ecId;

    // Référence à la maquette (ou classe/niveau) pour le filtrage
    @Column(nullable = false)
    private Long maquetteId;

    @Column(nullable = false)
    private Integer semestre;

    // L'enseignant assigné
    @Column(nullable = false)
    private Long enseignantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEnseignement type; // CM, TD, TP

    // Nombre de groupes (utile pour TD/TP)
    private Integer nombreGroupes = 1;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;
}
