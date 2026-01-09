package com.uasz.daos.maquette.repository;

import com.uasz.daos.maquette.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByCode(String code);

    Optional<Module> findByLibelle(String libelle);

    List<Module> findByUeId(Long ueId);
}
