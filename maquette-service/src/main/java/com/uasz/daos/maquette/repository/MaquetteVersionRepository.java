package com.uasz.daos.maquette.repository;

import com.uasz.daos.maquette.model.Maquette;
import com.uasz.daos.maquette.model.MaquetteVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaquetteVersionRepository extends JpaRepository<MaquetteVersion, Long> {
    List<MaquetteVersion> findByMaquetteOrderByNumeroVersionDesc(Maquette maquette);
}
