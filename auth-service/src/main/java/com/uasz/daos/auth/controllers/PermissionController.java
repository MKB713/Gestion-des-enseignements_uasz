package com.uasz.daos.auth.controllers;

import com.uasz.daos.auth.services.CustomUserDetails;
import com.uasz.daos.auth.services.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkPermissions(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Map<String, Object> permissions = new HashMap<>();
        permissions.put("isAdmin", permissionService.isAdmin());
        permissions.put("isEnseignant", permissionService.isEnseignant());
        permissions.put("isEtudiant", permissionService.isEtudiant());
        permissions.put("role", userDetails.getRole().name());
        permissions.put("userId", userDetails.getId());
        permissions.put("nom", userDetails.getNom());
        permissions.put("prenom", userDetails.getPrenom());
        permissions.put("email", userDetails.getUsername());

        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/can-access/{resourceOwnerId}")
    public ResponseEntity<Map<String, Boolean>> canAccessResource(
            @PathVariable Long resourceOwnerId
    ) {
        boolean canAccess = permissionService.canAccessResource(resourceOwnerId);
        return ResponseEntity.ok(Map.of("canAccess", canAccess));
    }

    @GetMapping("/can-modify/{targetUserId}")
    public ResponseEntity<Map<String, Boolean>> canModifyUser(
            @PathVariable Long targetUserId
    ) {
        boolean canModify = permissionService.canModifyUser(targetUserId);
        return ResponseEntity.ok(Map.of("canModify", canModify));
    }

    @GetMapping("/has-role/{role}")
    public ResponseEntity<Map<String, Boolean>> hasRole(
            @PathVariable String role,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boolean hasRole = permissionService.hasRole(role.toUpperCase());
        return ResponseEntity.ok(Map.of("hasRole", hasRole));
    }

    @GetMapping("/current-role")
    public ResponseEntity<Map<String, String>> getCurrentRole(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Map<String, String> response = new HashMap<>();
        response.put("role", userDetails.getRole().name());
        response.put("roleDisplay", userDetails.getRole().getLibelle());
        return ResponseEntity.ok(response);
    }
}