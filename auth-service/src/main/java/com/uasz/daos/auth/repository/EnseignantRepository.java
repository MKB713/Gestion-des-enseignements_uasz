package com.uasz.daos.auth.repository;

import com.uasz.daos.auth.model.Enseignant;
import com.uasz.daos.auth.enums.StatutEnseignant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {

    // Méthodes existantes
    List<Enseignant> findByDepartement(String departement);
    List<Enseignant> findByGrade(String grade);
    List<Enseignant> findByStatut(StatutEnseignant statut);

    @Query("SELECT e FROM Enseignant e WHERE e.specialite LIKE %:specialite%")
    List<Enseignant> findBySpecialiteContaining(@Param("specialite") String specialite);

    @Query("SELECT COUNT(e) FROM Enseignant e WHERE e.statut = :statut")
    long countByStatut(@Param("statut") StatutEnseignant statut);

    // NOUVELLES MÉTHODES
    @Query("SELECT e FROM Enseignant e WHERE e.email = :email")
    Optional<Enseignant> findByEmail(@Param("email") String email);

    @Query("SELECT e FROM Enseignant e WHERE e.departement = :departement AND e.statut = 'PERMANENT'")
    List<Enseignant> findPermanentByDepartement(@Param("departement") String departement);
}