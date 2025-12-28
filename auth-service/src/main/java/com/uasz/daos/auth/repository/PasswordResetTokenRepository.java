package com.uasz.daos.auth.repository;

import com.uasz.daos.auth.model.PasswordResetToken;
import com.uasz.daos.auth.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    List<PasswordResetToken> findByUtilisateur(Utilisateur utilisateur);

    @Query("SELECT t FROM PasswordResetToken t WHERE t.utilisateur = :utilisateur AND t.used = false AND t.expiryDate > CURRENT_TIMESTAMP")
    List<PasswordResetToken> findValidTokensByUtilisateur(@Param("utilisateur") Utilisateur utilisateur);

    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < CURRENT_TIMESTAMP OR t.used = true")
    void cleanupExpiredTokens();

    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.utilisateur = :utilisateur AND t.used = false")
    void invalidateUserTokens(@Param("utilisateur") Utilisateur utilisateur);
}