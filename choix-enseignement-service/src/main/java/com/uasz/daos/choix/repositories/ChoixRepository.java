package com.uasz.daos.choix.repositories;

import com.uasz.daos.choix.model.Choix;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChoixRepository extends JpaRepository<Choix, Long> {

    List<Choix> findByIdEnseignant(Long idEnseignant);

    Optional<Choix> findByIdAndIdEnseignant(Long id, Long idEnseignant);

    boolean existsByIdEnseignantAndIdEnseignement(Long idEnseignant, Long idEnseignement);

    Page<Choix> findAll(Pageable pageable);

    @Query("SELECT c FROM Choix c WHERE " +
            "(:idEnseignant IS NULL OR c.idEnseignant = :idEnseignant) AND " +
            "(:idEnseignement IS NULL OR c.idEnseignement = :idEnseignement) AND " +
            "(:dateDebut IS NULL OR c.dateChoix >= :dateDebut) AND " +
            "(:dateFin IS NULL OR c.dateChoix <= :dateFin)")
    Page<Choix> findWithFilters(
            @Param("idEnseignant") Long idEnseignant,
            @Param("idEnseignement") Long idEnseignement,
            @Param("dateDebut") LocalDateTime dateDebut,
            @Param("dateFin") LocalDateTime dateFin,
            Pageable pageable
    );
}