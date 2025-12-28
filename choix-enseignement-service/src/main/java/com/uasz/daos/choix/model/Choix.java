package com.uasz.daos.choix.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "choix_enseignements")
public class Choix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- RELATIONS EXTERNES (Microservices) ---
    // On ne met pas @ManyToOne vers Enseignant car il est dans une autre BDD
    @Column(nullable = false)
    private Long idEnseignant;

    @Column(nullable = false)
    private Long idUE; // Ou idEC selon votre granularité (Matière)

    // --- ATTRIBUTS LOCAUX ---

    // Type d'intervention souhaité (optionnel, mais recommandé)
    // Ex: CM, TD, TP. Si vous n'avez pas d'Enum, mettez String.
    private String typeIntervention;

    // Nombre de groupes souhaités (ex: 2 groupes de TD)
    private Integer nombreGroupes = 1;

    private boolean valide = false; // Validé par le chef de département ?

    private LocalDateTime dateChoix;

    private LocalDateTime dateValidation;

    // Pour l'affichage rapide sans rappeler les microservices à chaque fois (cache simple)
    // Ces champs sont remplis au moment de la création
    private String description; // Ex: "M. DIOP - Algorithmique"
}