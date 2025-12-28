package com.uasz.daos.auth.services;

import com.uasz.daos.auth.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PermissionService {

    private static final Role[] ADMIN_ROLES = {Role.ADMIN, Role.CHEF_DE_DEPARTEMENT};
    private static final Role[] ENSEIGNANT_ROLES = {Role.ENSEIGNANT, Role.ADMIN, Role.CHEF_DE_DEPARTEMENT};

    public boolean hasRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Role userRole = userDetails.getRole();

        return Arrays.stream(roles)
                .anyMatch(role -> userRole.name().equals(role));
    }

    public boolean hasAnyRole(String... roles) {
        return hasRole(roles);
    }

    public boolean isAdmin() {
        return hasRole("ADMIN", "CHEF_DE_DEPARTEMENT");
    }

    public boolean isEnseignant() {
        return hasRole("ENSEIGNANT", "ADMIN", "CHEF_DE_DEPARTEMENT");
    }

    public boolean isEtudiant() {
        return hasRole("ETUDIANT");
    }

    public boolean canAccessResource(Long resourceOwnerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Admin peut tout accéder
        if (isAdmin()) {
            return true;
        }

        // L'utilisateur peut accéder à ses propres ressources
        return userDetails.getId().equals(resourceOwnerId);
    }

    public boolean canModifyUser(Long targetUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Admin peut modifier n'importe quel utilisateur
        if (isAdmin()) {
            return true;
        }

        // Un utilisateur peut se modifier lui-même
        return userDetails.getId().equals(targetUserId);
    }

    public boolean hasHigherOrEqualRole(Role requiredRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Role userRole = userDetails.getRole();

        return userRole.getNiveau() >= requiredRole.getNiveau();
    }

    public Role getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getRole();
    }
}