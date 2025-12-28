package com.uasz.daos.emploitemps.repository;

import com.uasz.daos.emploitemps.model.Deroulement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeroulementRepository extends JpaRepository<Deroulement, Long> {

    /**
     * Trouve le déroulement d'une séance
     */
    Optional<Deroulement> findBySeanceId(Long seanceId);

    /**
     * Trouve les déroulements par statut
     */
    List<Deroulement> findByStatutOrderByDateValidationDesc(String statut);

    /**
     * Trouve les déroulements validés entre deux dates
     */
    List<Deroulement> findByDateValidationBetweenOrderByDateValidationDesc(
            Date dateDebut, Date dateFin);

    /**
     * Compte les séances effectuées
     */
    @Query("SELECT COUNT(d) FROM Deroulement d WHERE d.statut = 'EFFECTUE'")
    long countSeancesEffectuees();

    /**
     * Compte les séances annulées
     */
    @Query("SELECT COUNT(d) FROM Deroulement d WHERE d.statut = 'ANNULE'")
    long countSeancesAnnulees();

    /**
     * Calcule le volume horaire total effectué
     */
    @Query("SELECT SUM(d.volumeHoraireEffectue) FROM Deroulement d WHERE d.statut = 'EFFECTUE'")
    Double sumVolumeHoraireEffectue();

    /**
     * Trouve les déroulements par enseignant
     */
    @Query("SELECT d FROM Deroulement d WHERE d.seance.enseignantId = :enseignantId")
    List<Deroulement> findBySeanceEnseignantId(Long enseignantId);
}