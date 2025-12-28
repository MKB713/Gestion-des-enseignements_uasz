package com.uasz.daos.emploitemps.repository;

import com.uasz.daos.emploitemps.model.Batiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatimentRepository extends JpaRepository<Batiment, Long> {

    /**
     * Recherche par libellé (insensible à la casse)
     */
    List<Batiment> findByLibelleContainingIgnoreCase(String libelle);

    /**
     * Trouve un bâtiment par libellé exact
     */
    Optional<Batiment> findByLibelle(String libelle);

    /**
     * Compte le nombre de salles d'un bâtiment
     */
    @Query("SELECT COUNT(s) FROM Salle s WHERE s.batiment.id = :batimentId")
    long countSallesByBatimentId(Long batimentId);
}