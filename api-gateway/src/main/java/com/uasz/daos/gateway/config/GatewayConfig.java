package com.uasz.daos.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

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
            String ip = exchange.getRequest().getRemoteAddress() != null ?
                    exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() :
                    "unknown";
            return Mono.just(ip);
        };
    }

    /**
     * Alternative: KeyResolver basé sur un header d'API key
     */
    @Bean
    public KeyResolver apiKeyResolver() {
        return exchange -> {
            String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");
            return Mono.just(apiKey != null ? apiKey : "anonymous");
        };
    }
}
