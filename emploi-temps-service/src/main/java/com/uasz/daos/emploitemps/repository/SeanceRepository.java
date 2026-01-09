package com.uasz.daos.emploitemps.repository;

import com.uasz.daos.emploitemps.model.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeanceRepository extends JpaRepository<Seance, Long> {
    List<Seance> findByEnseignantId(Long enseignantId);

    List<Seance> findBySalleId(Long salleId);

    List<Seance> findByClasseId(Long classeId);

    List<Seance> findBySemestreId(Long semestreId);
}
