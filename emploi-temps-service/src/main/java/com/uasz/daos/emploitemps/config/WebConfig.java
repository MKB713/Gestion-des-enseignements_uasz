package com.uasz.daos.emploitemps.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration Web pour CORS et autres paramètres HTTP
 * Permet la communication avec les frontends et autres microservices
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configuration CORS (Cross-Origin Resource Sharing)
     * Permet aux frontends d'appeler l'API depuis d'autres domaines
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Tous les endpoints /api/*
                .allowedOrigins(
                        "http://localhost:3000",  // React dev server
                        "http://localhost:4200",  // Angular dev server
                        "http://localhost:8080",  // Frontend potentiel
                        "http://localhost:8085"   // Même service (pour tests)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // Cache preflight pendant 1h

        // Configuration spécifique pour le service Python
        registry.addMapping("/api/generation/**")
                .allowedOrigins("http://localhost:8000") // Service Python
                .allowedMethods("GET", "POST")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    /**
     * Note: En production, remplacer allowedOrigins par des domaines spécifiques
     *
     * Exemple production:
     * .allowedOrigins(
     *     "https://emploi-temps.uasz.sn",
     *     "https://app.uasz.sn"
     * )
     */
}