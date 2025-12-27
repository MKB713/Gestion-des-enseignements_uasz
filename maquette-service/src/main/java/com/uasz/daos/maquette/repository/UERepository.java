package com.uasz.daos.maquette.repository;

import com.uasz.daos.maquette.model.UE;
import com.uasz.daos.maquette.model.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UERepository extends JpaRepository<UE, Long> {
    boolean existsByCode(String code);

    Optional<UE> findByCode(String code);

    boolean existsBySemestre(Semestre semestre);

    List<UE> findByArchive(boolean archive);
}
