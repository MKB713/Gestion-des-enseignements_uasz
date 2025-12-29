package com.uasz.daos.maquette.repository;

import com.uasz.daos.maquette.model.EC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ECRepository extends JpaRepository<EC, Long> {
    boolean existsByCode(String code);

    Optional<EC> findByCode(String code);

    List<EC> findByArchive(boolean archive);
}
