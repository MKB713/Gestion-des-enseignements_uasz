package com.uasz.daos.auth.repository;

import com.uasz.daos.auth.model.RefreshToken;
import com.uasz.daos.auth.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUtilisateur(Utilisateur utilisateur);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.utilisateur = :utilisateur")
    void deleteByUtilisateur(@Param("utilisateur") Utilisateur utilisateur);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.dateExpiration < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.utilisateur = :utilisateur AND rt.revoque = false AND rt.dateExpiration > :now")
    List<RefreshToken> findValidTokensByUtilisateur(@Param("utilisateur") Utilisateur utilisateur, @Param("now") LocalDateTime now);

    // NOUVELLE MÉTHODE
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoque = true WHERE rt.utilisateur = :utilisateur AND rt.revoque = false")
    void revokeAllUserTokens(@Param("utilisateur") Utilisateur utilisateur);
}