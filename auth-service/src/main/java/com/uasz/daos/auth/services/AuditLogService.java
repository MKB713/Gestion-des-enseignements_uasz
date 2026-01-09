package com.uasz.daos.auth.services;

import com.uasz.daos.auth.model.AuditLog;
import com.uasz.daos.auth.enums.ResultatAction;
import com.uasz.daos.auth.model.Utilisateur;
import com.uasz.daos.auth.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@SuppressWarnings("null")
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void logAction(Utilisateur utilisateur, String action, String ressource, ResultatAction resultat,
            String details) {
        AuditLog log = new AuditLog();
        log.setUtilisateur(utilisateur);
        log.setAction(action);
        log.setRessource(ressource);
        log.setResultat(resultat);
        log.setDetails(details);
        auditLogRepository.save(log);
    }

    @Transactional
    public void logActionWithRequest(Utilisateur utilisateur, String action, String ressource,
            ResultatAction resultat, String details, HttpServletRequest request) {
        AuditLog log = new AuditLog();
        log.setUtilisateur(utilisateur);
        log.setAction(action);
        log.setRessource(ressource);
        log.setResultat(resultat);
        log.setDetails(details);
        log.setAdresseIp(getClientIpAddress(request));
        log.setUserAgent(request.getHeader("User-Agent"));
        auditLogRepository.save(log);
    }

    public List<AuditLog> getLogsByUtilisateur(Utilisateur utilisateur) {
        return auditLogRepository.findByUtilisateur(utilisateur);
    }

    public List<AuditLog> getLogsByPeriod(LocalDateTime debut, LocalDateTime fin) {
        return auditLogRepository.findByDateActionBetween(debut, fin);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllOrderByDateDesc();
    }

    public List<AuditLog> getRecentLogs(int limit) {
        return auditLogRepository.findAllOrderByDateDesc()
                .stream()
                .limit(limit)
                .toList();
    }

    public long getSuccessCount(LocalDateTime debut, LocalDateTime fin) {
        return auditLogRepository.countSuccessActions(debut, fin);
    }

    public long getFailureCount(LocalDateTime debut, LocalDateTime fin) {
        return auditLogRepository.countFailedActions(debut, fin);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Transactional
    public void cleanupOldLogs(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        List<AuditLog> oldLogs = auditLogRepository.findByDateActionBefore(cutoffDate);
        auditLogRepository.deleteAll(oldLogs);
    }
}