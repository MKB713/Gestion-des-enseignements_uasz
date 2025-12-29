package com.uasz.daos.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Filtre pour ajouter les headers de sécurité HTTP
 */
@Component
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            HttpHeaders headers = exchange.getResponse().getHeaders();

            // Protection XSS
            headers.add("X-XSS-Protection", "1; mode=block");

            // Prévention du clickjacking
            headers.add("X-Frame-Options", "DENY");

            // Prévention du MIME sniffing
            headers.add("X-Content-Type-Options", "nosniff");

            // Content Security Policy
            headers.add("Content-Security-Policy",
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data: https:; " +
                "font-src 'self' data:; " +
                "connect-src 'self'; " +
                "frame-ancestors 'none'");

            // Strict Transport Security (HSTS)
            // Note: Activer uniquement si HTTPS est configuré
            // headers.add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

            // Referrer Policy
            headers.add("Referrer-Policy", "no-referrer-when-downgrade");

            // Permissions Policy
            headers.add("Permissions-Policy",
                "geolocation=(), " +
                "microphone=(), " +
                "camera=()");
        }));
    }

    @Override
    public int getOrder() {
        return -50; // Après le WAF mais avant les autres
    }
}
