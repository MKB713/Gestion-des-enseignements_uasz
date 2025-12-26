//package com.uasz.daos.apigateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        CorsConfiguration corsConfig = new CorsConfiguration();
//
//        // 1. Origines autorisées (Frontend React)
//        // Utilisation de setAllowedOriginPatterns pour gérer les wildcards proprement
//        corsConfig.setAllowedOriginPatterns(Arrays.asList(
//                "http://localhost:3000",       // React standard
//                "http://localhost:5173",       // Vite (souvent utilisé avec React maintenant)
//                "http://127.0.0.1:3000",
//                "http://127.0.0.1:5173",
//                "http://localhost:*"           // Pour couvrir d'autres ports locaux en dev
//        ));
//
//        // 2. Méthodes HTTP autorisées
//        // On autorise tout (*) ou on spécifie la liste
//        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
//
//        // 3. Headers autorisés
//        // "*" autorise tous les headers, c'est plus simple pour le développement
//        corsConfig.addAllowedHeader("*");
//
//        // 4. Headers exposés au frontend
//        // Important pour que React puisse lire le token JWT s'il est envoyé dans un header
//        corsConfig.setExposedHeaders(Arrays.asList("Authorization", "Link", "X-Total-Count"));
//
//        // 5. Credentials (Cookies / Auth Headers)
//        // Indispensable si vous envoyez des tokens ou des cookies
//        corsConfig.setAllowCredentials(true);
//
//        // 6. Cache de la configuration (1 heure)
//        corsConfig.setMaxAge(3600L);
//
//        // Application de la config à toutes les routes du Gateway
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfig);
//
//        return new CorsWebFilter(source);
//    }
//}