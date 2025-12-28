package com.uasz.daos.auth.services;

import com.uasz.daos.auth.dto.DashboardStatsDTO;
import com.uasz.daos.auth.enums.Role;
import com.uasz.daos.auth.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final UtilisateurRepository utilisateurRepository;

    public DashboardService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        long totalUsers = utilisateurRepository.count();
        long totalEtudiants = utilisateurRepository.countByRole(Role.ETUDIANT);
        long totalEnseignants = utilisateurRepository.countByRole(Role.ENSEIGNANT);
        long totalAdmins = utilisateurRepository.countByRole(Role.ADMIN) +
                utilisateurRepository.countByRole(Role.CHEF_DE_DEPARTEMENT);
        long activeUsers = utilisateurRepository.countActiveUsers();

        return DashboardStatsDTO.builder()
                .totalUsers(totalUsers)
                .totalEtudiants(totalEtudiants)
                .totalEnseignants(totalEnseignants)
                .totalAdmins(totalAdmins)
                .activeUsers(activeUsers)
                .build();
    }

    @Transactional(readOnly = true)
    public DashboardStatsDTO getStats() {
        return getDashboardStats();
    }

    @Transactional(readOnly = true)
    public long getLockedAccountsCount() {
        return utilisateurRepository.findLockedUsers().size();
    }

    @Transactional(readOnly = true)
    public long getTodayRegistrations() {
        // Implémentation simplifiée - à compléter avec une requête JPA
        return utilisateurRepository.findAll().stream()
                .filter(u -> u.getDateCreation().toLocalDate().equals(java.time.LocalDate.now()))
                .count();
    }
}