package com.uasz.daos.emploitemps.repository;

import com.uasz.daos.emploitemps.model.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface SalleRepository extends JpaRepository<Salle, Long> {

    /**
     * Trouve toutes les salles d'un bâtiment
     */
    List<Salle> findByBatimentIdOrderByLibelleAsc(Long batimentId);

    /**
     * Trouve les salles avec capacité minimale
     */
    List<Salle> findByCapaciteGreaterThanEqualOrderByCapaciteAsc(int capaciteMin);

    /**
     * Recherche par libellé
     */
    List<Salle> findByLibelleContainingIgnoreCase(String libelle);

    /**
     * Trouve les salles disponibles à un créneau donné
     */
    @Query("SELECT s FROM Salle s WHERE s.id NOT IN " +
            "(SELECT se.salle.id FROM Seance se WHERE se.dateSeance = :date " +
            "AND se.heureDebut < :heureFin AND se.heureFin > :heureDebut " +
            "AND se.statut != 'ANNULEE')")
    List<Salle> findSallesDisponibles(
            @Param("date") LocalDate date,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin);

    /**
     * Trouve les salles disponibles avec capacité minimale
     */
    @Query("SELECT s FROM Salle s WHERE s.capacite >= :capaciteMin " +
            "AND s.id NOT IN " +
            "(SELECT se.salle.id FROM Seance se WHERE se.dateSeance = :date " +
            "AND se.heureDebut < :heureFin AND se.heureFin > :heureDebut " +
            "AND se.statut != 'ANNULEE')")
    List<Salle> findSallesDisponiblesAvecCapacite(
            @Param("date") LocalDate date,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin,
            @Param("capaciteMin") int capaciteMin);
}