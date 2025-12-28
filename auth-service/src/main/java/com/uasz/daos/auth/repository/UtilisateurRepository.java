package com.uasz.daos.auth.repository;

import com.uasz.daos.auth.enums.Role;
import com.uasz.daos.auth.enums.Etat;
import com.uasz.daos.auth.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    Optional<Utilisateur> findByMatricule(String matricule);

    boolean existsByEmail(String email);

    boolean existsByMatricule(String matricule);

    List<Utilisateur> findByRole(Role role);

    List<Utilisateur> findByEtat(Etat etat);

    @Query("SELECT u FROM Utilisateur u WHERE u.role = :role AND u.etat = :etat")
    List<Utilisateur> findByRoleAndEtat(@Param("role") Role role, @Param("etat") Etat etat);

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);

    @Query("SELECT u FROM Utilisateur u WHERE LOWER(u.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Utilisateur> searchUtilisateurs(@Param("search") String search);

    // NOUVELLES MÉTHODES
    @Query("SELECT u FROM Utilisateur u WHERE u.etat = 'ACTIF'")
    List<Utilisateur> findActiveUsers();

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.etat = 'ACTIF'")
    long countActiveUsers();

    @Query("SELECT u FROM Utilisateur u WHERE u.compteVerrouille = true")
    List<Utilisateur> findLockedUsers();
}