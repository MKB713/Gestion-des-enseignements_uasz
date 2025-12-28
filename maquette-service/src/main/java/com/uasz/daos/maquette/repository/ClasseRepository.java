package com.uasz.daos.maquette.repository;

import com.uasz.daos.maquette.model.Classe;
import com.uasz.daos.maquette.model.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClasseRepository extends JpaRepository<Classe, Long> {
    boolean existsByNom(String nom);

    List<Classe> findByFormation(Formation formation);
}
