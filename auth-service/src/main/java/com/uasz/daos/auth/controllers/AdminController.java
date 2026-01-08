package com.uasz.daos.auth.controllers;

import com.uasz.daos.auth.dto.DashboardStatsDTO;
import com.uasz.daos.auth.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('ADMIN', 'CHEF_DE_DEPARTEMENT')")
// @CrossOrigin removed - CORS is handled by API Gateway
public class AdminController {

    private final DashboardService dashboardService;

    public AdminController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Admin endpoint accessible");
    }

    @GetMapping("/locked-accounts")
    public ResponseEntity<Long> getLockedAccountsCount() {
        long count = dashboardService.getLockedAccountsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/today-registrations")
    public ResponseEntity<Long> getTodayRegistrations() {
        long count = dashboardService.getTodayRegistrations();
        return ResponseEntity.ok(count);
    }
}