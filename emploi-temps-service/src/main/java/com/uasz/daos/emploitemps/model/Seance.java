package com.uasz.daos.emploitemps.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "seances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek jour;

    private LocalTime heureDebut;
    private LocalTime heureFin;

    // Relation avec Salle (Local entity)
    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    // Références externes (IDs seulement, car microservices différents)
    private Long ecId; // De maquette-service
    private Long enseignantId; // De enseignant-service
    private Long classeId; // De maquette-service (Groupe/Classe)
    private Long semestreId; // De maquette-service
}
