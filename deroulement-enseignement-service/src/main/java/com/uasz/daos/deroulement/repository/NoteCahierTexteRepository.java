package com.uasz.daos.deroulement.repository;

import com.uasz.daos.deroulement.model.NoteCahierTexte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoteCahierTexteRepository extends JpaRepository<NoteCahierTexte, Long> {

       List<NoteCahierTexte> findBySeanceId(Long seanceId);

       List<NoteCahierTexte> findByEnseignantId(Long enseignantId);

       List<NoteCahierTexte> findByEstValide(boolean estValide);

       List<NoteCahierTexte> findByTitreContainingIgnoreCase(String titre);

       // Note: Les requêtes complexes sont simplifiées car Seance et Enseignant sont
       // maintenant des IDs
       // TODO: Implémenter avec Feign clients vers emploi-temps-service et
       // enseignant-service
       @Query("SELECT n FROM NoteCahierTexte n ORDER BY n.dateCreation DESC")
       List<NoteCahierTexte> findAllOrderByDateAndEC();

       @Query("SELECT n FROM NoteCahierTexte n WHERE n.enseignantId = :enseignantId ORDER BY n.dateCreation DESC")
       List<NoteCahierTexte> findByEnseignantIdOrderByDateAndEC(@Param("enseignantId") Long enseignantId);

       // Note: La méthode findBySemestre est simplifiée car nous n'avons plus de
       // relation directe avec Semestre
       @Query("SELECT n FROM NoteCahierTexte n ORDER BY n.dateCreation DESC")
       List<NoteCahierTexte> findBySemestre();

       @Query("SELECT n FROM NoteCahierTexte n WHERE " +
                     "(:enseignantId IS NULL OR n.enseignantId = :enseignantId) AND " +
                     "(:seanceId IS NULL OR n.seanceId = :seanceId) " +
                     "ORDER BY n.dateCreation DESC")
       List<NoteCahierTexte> findWithFilters(@Param("enseignantId") Long enseignantId,
                     @Param("seanceId") Long seanceId);

       @Query("SELECT n FROM NoteCahierTexte n WHERE n.enseignantId = :enseignantId AND n.dateCreation BETWEEN :start AND :end ORDER BY n.dateCreation DESC")
       List<NoteCahierTexte> findByEnseignantAndDateRange(@Param("enseignantId") Long enseignantId,
                     @Param("start") LocalDateTime start,
                     @Param("end") LocalDateTime end);
}
