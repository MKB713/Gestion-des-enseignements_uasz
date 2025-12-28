package com.uasz.daos.emploitemps.repository;

import com.uasz.daos.emploitemps.model.Emploi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmploiRepository extends JpaRepository<Emploi, Long> {

    /**
     * Trouve un emploi du temps par libellé
     */
    Optional<Emploi> findByLibelle(String libelle);

    /**
     * Recherche par libellé partiel
     */
    List<Emploi> findByLibelleContainingIgnoreCase(String libelle);

    /**
     * Trouve les emplois du temps créés entre deux dates
     */
    List<Emploi> findByDateCreationBetweenOrderByDateCreationDesc(Date dateDebut, Date dateFin);

    /**
     * Trouve les emplois du temps récents
     */
    List<Emploi> findTop10ByOrderByDateCreationDesc();
}