package com.uasz.daos.emploitemps.repository;

import com.uasz.daos.emploitemps.model.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface SeanceRepository extends JpaRepository<Seance, Long> {

    // ========== REQUÊTES EXISTANTES ==========

    List<Seance> findBySalleId(Long salleId);

    List<Seance> findByEnseignantIdAndDateSeanceAndHeureDebutBeforeAndHeureFinAfter(
            Long enseignantId, LocalDate dateSeance, LocalTime heureFin, LocalTime heureDebut);

    List<Seance> findBySalleIdAndDateSeanceAndHeureDebutBeforeAndHeureFinAfter(
            Long salleId, LocalDate dateSeance, LocalTime heureFin, LocalTime heureDebut);

    List<Seance> findByEnseignantIdAndDateSeanceAndHeureDebutBeforeAndHeureFinAfterAndIdNot(
            Long enseignantId, LocalDate dateSeance, LocalTime heureFin, LocalTime heureDebut, Long seanceId);

    List<Seance> findBySalleIdAndDateSeanceAndHeureDebutBeforeAndHeureFinAfterAndIdNot(
            Long salleId, LocalDate dateSeance, LocalTime heureFin, LocalTime heureDebut, Long seanceId);

    List<Seance> findByEnseignantId(Long enseignantId);

    // ========== NOUVELLES REQUÊTES POUR PLANNING ==========

    /**
     * Récupère toutes les séances entre deux dates (pour planning hebdo/semestriel)
     */
    List<Seance> findByDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
            LocalDate dateDebut, LocalDate dateFin);

    /**
     * Récupère les séances d'un enseignant entre deux dates
     */
    List<Seance> findByEnseignantIdAndDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
            Long enseignantId, LocalDate dateDebut, LocalDate dateFin);

    /**
     * Récupère les séances d'une salle entre deux dates
     */
    List<Seance> findBySalleIdAndDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
            Long salleId, LocalDate dateDebut, LocalDate dateFin);

    /**
     * Récupère les séances d'un EC entre deux dates
     */
    List<Seance> findByEcIdAndDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
            Long ecId, LocalDate dateDebut, LocalDate dateFin);

    /**
     * Récupère les séances d'une classe entre deux dates
     */
    List<Seance> findByClasseIdAndDateSeanceBetweenOrderByDateSeanceAscHeureDebutAsc(
            Long classeId, LocalDate dateDebut, LocalDate dateFin);

    /**
     * Recherche multicritère pour la fonctionnalité de recherche
     * Tous les paramètres sont optionnels (null = pas de filtre)
     */
    @Query("SELECT s FROM Seance s WHERE " +
            "(:enseignantId IS NULL OR s.enseignantId = :enseignantId) AND " +
            "(:salleId IS NULL OR s.salle.id = :salleId) AND " +
            "(:ecId IS NULL OR s.ecId = :ecId) AND " +
            "(:classeId IS NULL OR s.classeId = :classeId) AND " + // Ajout du filtre classeId
            "(:dateDebut IS NULL OR s.dateSeance >= :dateDebut) AND " +
            "(:dateFin IS NULL OR s.dateSeance <= :dateFin) " +
            "ORDER BY s.dateSeance ASC, s.heureDebut ASC")
    List<Seance> rechercherSeances(
            @Param("enseignantId") Long enseignantId,
            @Param("salleId") Long salleId,
            @Param("ecId") Long ecId,
            @Param("classeId") Long classeId, // Ajout du paramètre classeId
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin);

    /**
     * Trouve les séances en conflit pour une classe donnée
     */
    List<Seance> findByClasseIdAndDateSeanceAndHeureDebutBeforeAndHeureFinAfter(
            Long classeId, LocalDate dateSeance, LocalTime heureFin, LocalTime heureDebut);

    /**
     * Trouve les séances en conflit pour une classe donnée, excluant une séance spécifique
     */
    List<Seance> findByClasseIdAndDateSeanceAndHeureDebutBeforeAndHeureFinAfterAndIdNot(
            Long classeId, LocalDate dateSeance, LocalTime heureFin, LocalTime heureDebut, Long id);
}