package com.uasz.daos.emploitemps.repository;

import com.uasz.daos.emploitemps.model.Remplacement;
import com.uasz.daos.emploitemps.model.StatutRemplacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RemplacementRepository extends JpaRepository<Remplacement, Long> {

    /**
     * Trouve tous les remplacements d'une séance
     */
    List<Remplacement> findBySeanceId(Long seanceId);

    /**
     * Trouve les remplacements par enseignant remplacé
     */
    List<Remplacement> findByEnseignantRemplaceIdOrderByDateRemplacementDesc(Long enseignantId);

    /**
     * Trouve les remplacements par enseignant remplaçant
     */
    List<Remplacement> findByEnseignantRemplacantIdOrderByDateRemplacementDesc(Long enseignantId);

    /**
     * Trouve les remplacements par statut
     */
    List<Remplacement> findByStatutOrderByDateCreationDesc(StatutRemplacement statut);

    /**
     * Trouve les remplacements en attente pour un remplaçant
     */
    List<Remplacement> findByEnseignantRemplacantIdAndStatutOrderByDateCreationDesc(
            Long enseignantId, StatutRemplacement statut);

    /**
     * Compte les remplacements d'une séance
     */
    long countBySeanceId(Long seanceId);

    /**
     * Vérifie si une séance a déjà un remplacement actif
     */
    @Query("SELECT COUNT(r) > 0 FROM Remplacement r WHERE r.seance.id = :seanceId AND r.statut = 'ACCEPTE'")
    boolean existeRemplacementActifPourSeance(@Param("seanceId") Long seanceId);
}