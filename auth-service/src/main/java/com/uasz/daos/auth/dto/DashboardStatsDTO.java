package com.uasz.daos.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {

    private long totalUsers;
    private long totalEtudiants;
    private long totalEnseignants;
    private long totalAdmins;
    private long activeUsers;
}