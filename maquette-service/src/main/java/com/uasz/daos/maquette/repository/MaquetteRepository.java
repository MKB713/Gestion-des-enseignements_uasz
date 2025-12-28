package com.uasz.daos.maquette.repository;

import com.uasz.daos.maquette.model.Maquette;
import com.uasz.daos.maquette.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaquetteRepository extends JpaRepository<Maquette, Long> {
    boolean existsByCode(String code);

    Optional<Maquette> findByIdAndActifTrue(Long id);

    List<Maquette> findByActifTrue();
}
