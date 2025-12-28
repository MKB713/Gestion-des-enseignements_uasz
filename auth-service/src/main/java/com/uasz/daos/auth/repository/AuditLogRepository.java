package com.uasz.daos.auth.repository;

import com.uasz.daos.auth.model.AuditLog;
import com.uasz.daos.auth.model.Utilisateur;
import com.uasz.daos.auth.enums.ResultatAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUtilisateur(Utilisateur utilisateur);

    List<AuditLog> findByAction(String action);

    List<AuditLog> findByResultat(ResultatAction resultat);

    @Query("SELECT a FROM AuditLog a WHERE a.dateAction BETWEEN :debut AND :fin")
    List<AuditLog> findByDateActionBetween(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("SELECT a FROM AuditLog a WHERE a.utilisateur = :utilisateur AND a.dateAction BETWEEN :debut AND :fin")
    List<AuditLog> findByUtilisateurAndDateActionBetween(@Param("utilisateur") Utilisateur utilisateur, @Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    // NOUVELLES MÉTHODES
    @Query("SELECT a FROM AuditLog a ORDER BY a.dateAction DESC")
    List<AuditLog> findAllOrderByDateDesc();

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.resultat = 'SUCCES' AND a.dateAction BETWEEN :debut AND :fin")
    long countSuccessActions(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.resultat = 'ECHEC' AND a.dateAction BETWEEN :debut AND :fin")
    long countFailedActions(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("SELECT a FROM AuditLog a WHERE a.dateAction < :date")
    List<AuditLog> findByDateActionBefore(@Param("date") LocalDateTime date);
}