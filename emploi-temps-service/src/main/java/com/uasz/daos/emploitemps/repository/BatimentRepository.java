package com.uasz.daos.emploitemps.repository;

import com.uasz.daos.emploitemps.model.Batiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatimentRepository extends JpaRepository<Batiment, Long> {
}
