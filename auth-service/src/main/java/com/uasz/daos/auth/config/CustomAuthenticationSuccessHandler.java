package com.uasz.daos.auth.config;

import com.uasz.daos.auth.enums.Role;
import com.uasz.daos.auth.services.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Role userRole = userDetails.getRole();

        String redirectUrl = determineRedirectUrl(userRole);

        // Log de la connexion réussie
        System.out.println("✅ Connexion réussie - Utilisateur: " + userDetails.getUsername() +
                " - Rôle: " + userRole + " - Redirection vers: " + redirectUrl);

        response.sendRedirect(redirectUrl);
    }

    private String determineRedirectUrl(Role userRole) {
        return switch (userRole) {
            case ETUDIANT -> "/dashboard/etudiant";
            case ENSEIGNANT -> "/dashboard/enseignant";
            case RESPONSABLE_MASTER -> "/dashboard/responsable";
            case COORDONATEUR_DES_LICENCES -> "/dashboard/coordinateur";
            case ADMIN, CHEF_DE_DEPARTEMENT -> "/dashboard/admin";
            default -> "/";
        };
    }
}