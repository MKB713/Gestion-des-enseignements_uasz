package com.uasz.daos.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Configuration du Gateway avec Rate Limiting et Circuit Breaker
 */
@Configuration
public class GatewayConfig {

    /**
     * KeyResolver pour le Rate Limiting basé sur l'adresse IP du client
     * Chaque IP a sa propre limite de requêtes
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            return Mono.just(ip);
        };
    }

    /*
     * @Bean
     * public CorsWebFilter corsWebFilter() {
     * CorsConfiguration corsConfig = new CorsConfiguration();
     * corsConfig.addAllowedOrigin("http://localhost:5173"); // Allow your frontend
     * origin
     * corsConfig.addAllowedMethod("*"); // Allow all HTTP methods
     * corsConfig.addAllowedHeader("*"); // Allow all headers
     * corsConfig.setAllowCredentials(true); // Allow credentials (e.g., cookies,
     * authorization headers)
     * 
     * UrlBasedCorsConfigurationSource source = new
     * UrlBasedCorsConfigurationSource();
     * source.registerCorsConfiguration("/**", corsConfig); // Apply CORS to all
     * paths
     * 
     * return new CorsWebFilter(source);
     * }
     */

    /**
     * Alternative: KeyResolver basé sur un header d'API key
     * 
     * @Bean
     *       public KeyResolver apiKeyResolver() {
     *       return exchange -> {
     *       String apiKey =
     *       exchange.getRequest().getHeaders().getFirst("X-API-KEY");
     *       return Mono.just(apiKey != null ? apiKey : "anonymous");
     *       };
     *       }
     */
}
