package com.uasz.daos.maquette.repository;

import com.uasz.daos.maquette.model.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemestreRepository extends JpaRepository<Semestre, Long> {
}
