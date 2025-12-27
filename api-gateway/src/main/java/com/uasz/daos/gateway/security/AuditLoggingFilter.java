package com.uasz.daos.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * Filtre d'audit pour logger toutes les requêtes et réponses
 * Format de log structuré pour faciliter l'analyse
 */
@Component
public class AuditLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private static final String START_TIME_ATTR = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Enregistrer le temps de début
        exchange.getAttributes().put(START_TIME_ATTR, Instant.now());

        // Log de la requête entrante
        logRequest(request);

        return chain.filter(exchange)
            .then(Mono.fromRunnable(() -> {
                // Log de la réponse sortante
                logResponse(exchange);
            }));
    }

    private void logRequest(ServerHttpRequest request) {
        HttpMethod method = request.getMethod();
        String path = request.getURI().getPath();
        String query = request.getURI().getQuery();
        String remoteAddress = getClientIp(request);
        String userAgent = request.getHeaders().getFirst(HttpHeaders.USER_AGENT);
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Format JSON pour faciliter l'ingestion dans ELK/Loki
        auditLogger.info("{{" +
            "\"event\":\"request\"," +
            "\"method\":\"{}\"," +
            "\"path\":\"{}\"," +
            "\"query\":\"{}\"," +
            "\"ip\":\"{}\"," +
            "\"user_agent\":\"{}\"," +
            "\"has_auth\":{}," +
            "\"timestamp\":\"{}\"" +
            "}}",
            method,
            path,
            query != null ? query : "",
            remoteAddress,
            userAgent != null ? userAgent : "unknown",
            authorization != null,
            Instant.now()
        );
    }

    private void logResponse(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        Instant startTime = exchange.getAttribute(START_TIME_ATTR);

        HttpMethod method = request.getMethod();
        String path = request.getURI().getPath();
        String remoteAddress = getClientIp(request);
        int statusCode = response.getStatusCode() != null ? response.getStatusCode().value() : 0;

        long duration = startTime != null
            ? Duration.between(startTime, Instant.now()).toMillis()
            : 0;

        // Déterminer le niveau de log selon le status code
        String logLevel = getLogLevel(statusCode);

        // Log structuré
        String logMessage = "{{" +
            "\"event\":\"response\"," +
            "\"method\":\"{}\"," +
            "\"path\":\"{}\"," +
            "\"ip\":\"{}\"," +
            "\"status\":{}," +
            "\"duration_ms\":{}," +
            "\"timestamp\":\"{}\"" +
            "}}";

        switch (logLevel) {
            case "ERROR":
                auditLogger.error(logMessage, method, path, remoteAddress, statusCode, duration, Instant.now());
                break;
            case "WARN":
                auditLogger.warn(logMessage, method, path, remoteAddress, statusCode, duration, Instant.now());
                break;
            default:
                auditLogger.info(logMessage, method, path, remoteAddress, statusCode, duration, Instant.now());
        }
    }

    private String getClientIp(ServerHttpRequest request) {
        // Vérifier les headers de proxy en premier
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
        }
        // Si X-Forwarded-For contient plusieurs IPs, prendre la première
        if (ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String getLogLevel(int statusCode) {
        if (statusCode >= 500) {
            return "ERROR";
        } else if (statusCode >= 400) {
            return "WARN";
        } else {
            return "INFO";
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // Exécuter en premier
    }
}
