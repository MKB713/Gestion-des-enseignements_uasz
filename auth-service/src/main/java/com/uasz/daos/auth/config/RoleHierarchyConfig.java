package com.uasz.daos.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
public class RoleHierarchyConfig {

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = """
                ADMIN > CHEF_DE_DEPARTEMENT
                CHEF_DE_DEPARTEMENT > RESPONSABLE_MASTER
                RESPONSABLE_MASTER > COORDONATEUR_DES_LICENCES
                COORDONATEUR_DES_LICENCES > ENSEIGNANT
                ENSEIGNANT > ETUDIANT
                """;
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
}
