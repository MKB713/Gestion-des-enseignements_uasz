package com.uasz.daos.auth.repository;

import com.uasz.daos.auth.model.PasswordHistory;
import com.uasz.daos.auth.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {

    List<PasswordHistory> findByUtilisateurOrderByChangedAtDesc(Utilisateur utilisateur);

    @Query("SELECT ph FROM PasswordHistory ph WHERE ph.utilisateur = :utilisateur ORDER BY ph.changedAt DESC")
    List<PasswordHistory> findLastPasswords(@Param("utilisateur") Utilisateur utilisateur, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT COUNT(ph) FROM PasswordHistory ph WHERE ph.utilisateur = :utilisateur AND ph.passwordHash = :passwordHash")
    int countByUtilisateurAndPasswordHash(@Param("utilisateur") Utilisateur utilisateur, @Param("passwordHash") String passwordHash);

    @Modifying
    @Query("DELETE FROM PasswordHistory ph WHERE ph.utilisateur = :utilisateur AND ph.changedAt < :date")
    void deleteOldHistory(@Param("utilisateur") Utilisateur utilisateur, @Param("date") java.time.LocalDateTime date);
}