package com.uasz.daos.auth.dto;

public class DashboardStatsDTO {

    private long totalUsers;
    private long totalEtudiants;
    private long totalEnseignants;
    private long totalAdmins;
    private long activeUsers;

    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(long totalUsers, long totalEtudiants, long totalEnseignants, long totalAdmins, long activeUsers) {
        this.totalUsers = totalUsers;
        this.totalEtudiants = totalEtudiants;
        this.totalEnseignants = totalEnseignants;
        this.totalAdmins = totalAdmins;
        this.activeUsers = activeUsers;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalEtudiants() {
        return totalEtudiants;
    }

    public void setTotalEtudiants(long totalEtudiants) {
        this.totalEtudiants = totalEtudiants;
    }

    public long getTotalEnseignants() {
        return totalEnseignants;
    }

    public void setTotalEnseignants(long totalEnseignants) {
        this.totalEnseignants = totalEnseignants;
    }

    public long getTotalAdmins() {
        return totalAdmins;
    }

    public void setTotalAdmins(long totalAdmins) {
        this.totalAdmins = totalAdmins;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long totalUsers;
        private long totalEtudiants;
        private long totalEnseignants;
        private long totalAdmins;
        private long activeUsers;

        public Builder totalUsers(long totalUsers) {
            this.totalUsers = totalUsers;
            return this;
        }

        public Builder totalEtudiants(long totalEtudiants) {
            this.totalEtudiants = totalEtudiants;
            return this;
        }

        public Builder totalEnseignants(long totalEnseignants) {
            this.totalEnseignants = totalEnseignants;
            return this;
        }

        public Builder totalAdmins(long totalAdmins) {
            this.totalAdmins = totalAdmins;
            return this;
        }

        public Builder activeUsers(long activeUsers) {
            this.activeUsers = activeUsers;
            return this;
        }

        public DashboardStatsDTO build() {
            return new DashboardStatsDTO(totalUsers, totalEtudiants, totalEnseignants, totalAdmins, activeUsers);
        }
    }
}