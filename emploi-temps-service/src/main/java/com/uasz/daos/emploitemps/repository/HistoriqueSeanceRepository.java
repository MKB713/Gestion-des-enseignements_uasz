package com.uasz.daos.emploitemps.repository;

import com.uasz.daos.emploitemps.model.HistoriqueSeance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueSeanceRepository extends JpaRepository<HistoriqueSeance, Long> {
    List<HistoriqueSeance> findBySeanceIdOrderByDateModificationDesc(Long seanceId);
}
